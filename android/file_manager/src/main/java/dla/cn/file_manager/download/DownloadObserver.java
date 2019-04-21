package dla.cn.file_manager.download;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Observable;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DownloadObserver implements Observer<HttpDownload.DownloadAction> {
    ProgressBar progressBar;
    Disposable disposable;
    Activity activity;


    public DownloadObserver(Activity activity, ProgressBar progressBar) {
        this.activity = activity;
        this.progressBar = progressBar;
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onNext(HttpDownload.DownloadAction downloadAction) {
        int state = downloadAction.getState();
        long currentSize = downloadAction.getDownload().getCurrentSize();
        long fileSize = downloadAction.getDownload().getFileSize();
        double progress = currentSize / (double) fileSize * 100;

        progressBar.setProgress((int) progress, true);

        if (state == HttpDownload.DownloadAction.ON_CONNECT) {
            Toast.makeText(activity, "开始下载", Toast.LENGTH_SHORT).show();
        }

        Log.d("DownLoad", "CurrentSize:" + currentSize + " FileSize:" + fileSize + " progress:" + progress);
    }


    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public Disposable getDisposable() {
        return disposable;
    }

    public void setDisposable(Disposable disposable) {
        this.disposable = disposable;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
