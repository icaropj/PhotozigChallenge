package icaro.com.br.photozigchallenge.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import icaro.com.br.photozigchallenge.R;
import icaro.com.br.photozigchallenge.event.DownloadEvent;
import icaro.com.br.photozigchallenge.model.Download;
import icaro.com.br.photozigchallenge.retrofit.RetrofitSingleton;
import icaro.com.br.photozigchallenge.util.Constants;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by icaro on 30/11/2017.
 */

public class DownloadService extends IntentService {

    private EventBus bus = EventBus.getDefault();
    private File outputFile;

    public DownloadService() {
        super("File Download Service");
    }

    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    private int mTotalFileSize;

    @Override
    protected void onHandleIntent(Intent intent) {
        String fileName = intent.getStringExtra(Constants.FILE_NAME);
        String path = intent.getStringExtra(Constants.ASSETS_PATH_EXTRA);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(getString(R.string.download_title))
                .setContentText(getString(R.string.download_message) + " " + fileName)
                .setAutoCancel(true);
        mNotificationManager.notify(0, mNotificationBuilder.build());

        initDownload(fileName, path);
    }

    private void initDownload(String fileName, String path) {
        Call<ResponseBody> downloadCall = RetrofitSingleton.getInstance().getBaseService().downloadFile(path);
        try {
            downloadFile(downloadCall.execute().body(), fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(ResponseBody body, String filename) throws IOException {
        int count;byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        outputFile = new File(downloadsDir, filename);
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {
            total += count;
            mTotalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);
            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(mTotalFileSize);

            if (currentTime > 1000 * timeCount) {
                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
            }
            output.write(data, 0, count);
        }
        onDownloadComplete();
        output.flush();
        output.close();
        bis.close();
    }

    private void sendNotification(Download download) {
        sendIntent(download);
        mNotificationBuilder.setProgress(100, download.getProgress(), false);
        mNotificationBuilder.setContentText(getString(R.string.download_message) + download.getCurrentFileSize() + "/" + mTotalFileSize + " MB");
        mNotificationManager.notify(0, mNotificationBuilder.build());
    }

    private void sendIntent(Download download) {
        Intent intent = new Intent(Constants.MESSAGE_DOWNLOAD_PROGRESS);
        intent.putExtra(Constants.DOWNLOAD_EXTRA, download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {
        Download download = new Download();
        download.setProgress(100);

        sendIntent(download);

        mNotificationManager.cancel(0);
        mNotificationBuilder.setProgress(0, 0, false);
        mNotificationBuilder.setContentText(getString(R.string.downloaded_message));
        mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        Notification notification = mNotificationBuilder.build();
        mNotificationManager.notify(0, notification);

        DownloadEvent event = new DownloadEvent(outputFile);
        bus.post(event);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mNotificationManager.cancel(0);
    }

}
