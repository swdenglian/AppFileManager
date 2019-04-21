package dla.cn.file_manager.download;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public class HttpDownload extends Download {
    public HttpDownload(String fileUrl, String savePath, String saveFileName) {
        super(fileUrl, savePath, saveFileName);
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
            public ObservableSource<DownloadAction> apply(Observable upstream) {
                return Observable.create(new ObservableOnSubscribe<DownloadAction>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void subscribe(ObservableEmitter<DownloadAction> emitter) throws Exception {
                        URL url = new URL(getFileUrl());
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        RandomAccessFile randomAccessFile = null;

                        // 尝试连接文件
                        if (httpURLConnection.getResponseCode() == 200) {

                            // 判断文件夹路径是否存在
                            File folder = new File(getSavePath());
                            if (!folder.exists() && !folder.isDirectory()) {
                                folder.mkdir();
                            }

                            randomAccessFile = new RandomAccessFile(new File(getSavePath(), getSaveFileName()), "rws");
                            long fileSize = httpURLConnection.getContentLengthLong();
                            setFileSize(fileSize);
                            randomAccessFile.setLength(fileSize);
                            setState(DownloadAction.ON_CONNECT);
                            emitter.onNext(new DownloadAction(
                                    HttpDownload.this,
                                    DownloadAction.ON_CONNECT
                            ));


                            // 尝试下载文件
                            URL downloadURL = new URL(getFileUrl());
                            HttpURLConnection httpURLConnectionDownload = (HttpURLConnection) downloadURL.openConnection();
                            httpURLConnectionDownload.setRequestProperty("Range", "bytes=" + getCurrentSize() + "-" + getFileSize());
                            if (httpURLConnectionDownload.getResponseCode() == 206) {
                                byte[] buffer = new byte[getBufferSize()];
                                InputStream inputStream = httpURLConnectionDownload.getInputStream();
                                int readSize = 0;

                                setState(DownloadAction.ON_DOWNLOADING);

                                while ((readSize = inputStream.read(buffer)) > 0 && !emitter.isDisposed()) {
                                    randomAccessFile.write(buffer, 0, readSize);
                                    setCurrentSize(readSize + getCurrentSize());
                                    emitter.onNext(new DownloadAction(
                                            HttpDownload.this,
                                            DownloadAction.ON_DOWNLOADING
                                    ));
                                }

                                if (emitter.isDisposed()) { // 暂停
                                    setState(DownloadAction.ON_PAUSE);
                                    emitter.onNext(new DownloadAction(HttpDownload.this, DownloadAction.ON_PAUSE));
                                } else { // 结束
                                    setState(DownloadAction.ON_PRECOMPLETE);
                                    emitter.onNext(new DownloadAction(HttpDownload.this, DownloadAction.ON_PRECOMPLETE));
                                    emitter.onComplete();
                                }

                                randomAccessFile.close();
                            }
                        }
                    }
                });
            }
        };
    }
}
