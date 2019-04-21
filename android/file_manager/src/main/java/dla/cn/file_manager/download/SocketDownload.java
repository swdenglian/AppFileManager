package dla.cn.file_manager.download;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketDownload extends Download {
    Socket socket;
    RandomAccessFile randomAccessFile;

    public SocketDownload(
            String socketUrl,
            String fileUrl, String savePath, String saveFileName) {
        super(fileUrl, savePath, saveFileName);

        try {
            socket = IO.socket(socketUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Observable<DownloadAction> download(Activity activity) {
        return permissions(activity)
                .compose(downloadTransformer());
    }

    @Override
    public ObservableTransformer<Boolean, DownloadAction> downloadTransformer() {
        return new ObservableTransformer<Boolean, DownloadAction>() {
            @Override
            public ObservableSource<DownloadAction> apply(Observable<Boolean> upstream) {
                return Observable.create(new ObservableOnSubscribe<DownloadAction>() {


                    @Override
                    public void subscribe(final ObservableEmitter<DownloadAction> emitter) throws Exception {

                        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                try {
                                    Log.d("Download", "connect");
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("currSn", "msn");
                                    jsonObject.put("groupSn", "nassn");

                                    socket.emit("login", jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        socket.on("logined", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("file", getFileUrl());
                                    emit("transfer", jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        socket.on("error", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                if (null != args[0]) {
                                    try {

                                        JSONObject jsonObject = (JSONObject) args[0];
                                        int code = jsonObject.getInt("code");
                                        if (code == 1001) {
                                            JSONObject data = (JSONObject) jsonObject.get("data");
                                            if (null == data) {
                                                data = new JSONObject();
                                            }
                                            data.put("size", data.get("maxFileBufferSize"));
                                            emit("transfer_index", data);
                                            emit("transfer_error", jsonObject);
                                            return;
                                        }

                                        emit("transfer_error", jsonObject);
                                        socket.disconnect();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        socket.on("reconnect", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                try {
                                    Log.d("Download", "connect");
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("currSn", "msn");
                                    socket.emit("login", jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        socket.on("headerPackage", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                try {
                                    if (null != args[0]) {
                                        JSONObject jsonObject = (JSONObject) args[0];
                                        long fileSize = jsonObject.getLong("total");
                                        setFileSize(fileSize);
                                        randomAccessFile = new RandomAccessFile(new File(getSavePath(), getSaveFileName()), "rws");
                                        randomAccessFile.setLength(fileSize);
                                        emitter.onNext(new DownloadAction(SocketDownload.this, DownloadAction.ON_CONNECT));

                                        JSONObject params = new JSONObject();
                                        JSONObject packageInfo = new JSONObject();

                                        packageInfo.put("start", getCurrentSize());
                                        packageInfo.put("size", getBufferSize());

                                        params.put("file", getFileUrl());
                                        params.put("packageInfo", packageInfo);
                                        emit("transfer_index", params);
                                    } else {

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        socket.on("fileBuffer", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                try {
                                    if (null != args[0]) {
                                        JSONObject jsonObject = (JSONObject) args[0];

                                        if (!emitter.isDisposed()) {
                                            byte[] buffer = (byte[]) jsonObject.get("buffer");
                                            setCurrentSize(getCurrentSize() + buffer.length);
                                            randomAccessFile.write(buffer);
                                            emitter.onNext(new DownloadAction(SocketDownload.this, DownloadAction.ON_DOWNLOADING));
                                            int status = jsonObject.getInt("status");
                                            if (status == 1000) {
                                                emit("transfer_package_error", jsonObject);
                                                return;
                                            }

                                            emit("transfer_package_success", jsonObject);
                                            emit(" ", jsonObject);
                                            JSONObject packageInfo = jsonObject.getJSONObject("packageInfo");
                                            if (packageInfo.get("size") == null) {
                                                return;
                                            }
                                        }


                                        if (getCurrentSize() == getFileSize()) {
                                            if (emitter.isDisposed()) { // 暂停
                                                setState(DownloadAction.ON_PAUSE);
                                                emitter.onNext(new DownloadAction(SocketDownload.this, DownloadAction.ON_PAUSE));
                                            } else { // 结束
                                                setState(DownloadAction.ON_PRECOMPLETE);
                                                emitter.onNext(new DownloadAction(SocketDownload.this, DownloadAction.ON_PRECOMPLETE));
                                                emitter.onComplete();
                                            }

                                            randomAccessFile.close();
                                        } else {
                                            JSONObject params = new JSONObject();
                                            JSONObject packageInfo = new JSONObject();

                                            packageInfo.put("start", getCurrentSize() + 1);
                                            packageInfo.put("size", getBufferSize());

                                            params.put("file", getFileUrl());
                                            params.put("packageInfo", packageInfo);
                                            emit("transfer_index", params);
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }
                        });


                        // 启动
                        socket.connect();
                    }
                });
            }
        };
    }


    public Emitter emit(String targetEvent, JSONObject data) {

        if (null == data) {
            data = new JSONObject();
        }

        try {
            data.put("_id", socket.id());
            data.put("targetEvent", targetEvent);
            data.put("targetSn", "nassn");
            data.put("currSn", "msn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return socket.emit("send_client", data);
    }
}
