package dla.cn.file_manager.download;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Consumer;
import io.socket.emitter.Emitter;

public abstract class Download {
    private String fileUrl;
    private String savePath;
    private String saveFileName;

    private long fileSize;
    private long currentSize;
    private int bufferSize;
    private int state;

    public Download(String fileUrl, String savePath, String saveFileName) {
        this.fileUrl = fileUrl;
        this.savePath = savePath;
        this.saveFileName = saveFileName;

        this.currentSize = 0;
        this.bufferSize = 1024;
        this.state = DownloadAction.NO_START;
    }

    public abstract Observable<DownloadAction> download(final Activity activity);
    public abstract ObservableTransformer<Boolean, DownloadAction> downloadTransformer();

    public Observable<Boolean> permissions(Activity activity){
        return new RxPermissions((FragmentActivity) activity)
                .request(
                        Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .doOnNext(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (!aBoolean) {
                            throw new NoPermissionException("应用未获取权限");
                        }
                    }
                });
    }


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static class DownloadAction {
        public static int NO_START = -1;
        public static int ON_CONNECT = 0;
        public static int ON_DOWNLOADING = 1;
        public static int ON_PRECOMPLETE = 2;
        public static int ON_PAUSE = 3;

        private Download download;
        private int state;

        public DownloadAction(Download download, int state) {
            this.download = download;
            this.state = state;
        }

        public Download getDownload() {
            return download;
        }

        public void setDownload(Download download) {
            this.download = download;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }
    }


    public class NoPermissionException extends Exception {
        public NoPermissionException(String msg) {
            super(msg);
        }
    }
}
