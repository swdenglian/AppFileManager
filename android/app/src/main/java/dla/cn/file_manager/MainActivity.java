package dla.cn.file_manager;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import dla.cn.file_manager.download.Download;
import dla.cn.file_manager.download.DownloadObserver;
import dla.cn.file_manager.download.HttpDownload;
import dla.cn.file_manager.download.SocketDownload;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressbar;
    DownloadObserver downloadObserver;
    HttpDownload httpDownload;

    ProgressBar progressbar1;
    DownloadObserver downloadObserver1;
    SocketDownload socketDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressbar = findViewById(R.id.progressbar);
        progressbar.setMax(100);
        downloadObserver = new DownloadObserver(this, progressbar);
        httpDownload = new HttpDownload(" http://192.168.1.8:8080/1234.mp4", getFilesDir().getPath(), "http1234.mp4");

        progressbar1 = findViewById(R.id.progressbar1);
        progressbar1.setMax(100);
        downloadObserver1 = new DownloadObserver(this, progressbar1);
        socketDownload = new SocketDownload(
                "http://192.168.1.8:4000/file",
                "1234.mp4", getFilesDir().getPath(), "socket1234.mp4");

    }

    public void httpDownload(View v) {
        if (httpDownload.getState() == HttpDownload.DownloadAction.NO_START ||
                httpDownload.getState() == HttpDownload.DownloadAction.ON_PAUSE ) {

            httpDownload
                    .download(this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(downloadObserver);

        } else {
            downloadObserver.getDisposable().dispose();
        }
    }


    public void socketDownload(View v) {
        if (socketDownload.getState() == Download.DownloadAction.NO_START ||
                socketDownload.getState() == Download.DownloadAction.ON_PAUSE ) {
            socketDownload
                    .download(this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(downloadObserver1);
        } else {
            downloadObserver1.getDisposable().dispose();
        }
    }
}
