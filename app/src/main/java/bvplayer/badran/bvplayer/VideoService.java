package bvplayer.badran.bvplayer;

import static bvplayer.badran.bvplayer.PlayerActivity.PATH;
import static bvplayer.badran.bvplayer.PlayerActivity.POSITION;
import static bvplayer.badran.bvplayer.PlayerActivity.SHARED_PREFS;
import static bvplayer.badran.bvplayer.PlayerActivity.isPlay;
import static bvplayer.badran.bvplayer.PlayerActivity.myFiles;
import static bvplayer.badran.bvplayer.PlayerActivity.playPauseImage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.SurfaceHolder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.exoplayer2.util.Log;

import java.util.ArrayList;

public class VideoService extends Service implements MediaPlayer.OnCompletionListener {

    MyBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    String path;
    private int position = -1;
    ActionPlaying actionPlaying;
    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_ID_2 = "channel2";
    MediaSessionCompat mediaSessionCompat;

    public VideoService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e("Bind", "Method");
        return mBinder;
    }

    public class MyBinder extends Binder {
        public VideoService getService() {
            return VideoService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.e("STEEPS", "Steep -1");
        super.onCreate();
        loadData();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        Log.e("STEEPS", "Steep 0");
        if (myPosition != -1) {
            Log.e("STEEPS", "Steep 1");
            playMedia(myPosition);
        }
        return START_STICKY;
    }

    private void playMedia(int startPosition) {
        Log.e("STEEPS", "Steep 2");
        position = startPosition;
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.release();
        }
        createMediaPlayer(position);
    }

    public void setDisplay(SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void prepareAsync() {
        mediaPlayer.prepareAsync();
    }

    public void release() {
        mediaPlayer.release();
    }

    public void start() {
        Log.e("STEEPS", "Path " + mediaPlayer);
        Log.e("STEEPS", "Steep 6");
        if (mediaPlayer == null) {
            playMedia(position);
        }
        mediaPlayer.start();
        notificationBuilder();
    }

    public void pause() {
        mediaPlayer.pause();
        notificationBuilder();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void createMediaPlayer(int position) {
        if (myFiles != null) {
            path = myFiles.get(position).getPath();
            mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(path));
            Log.e("STEEPS", "Steep 3");
        }
    }

    public void seekTo(int mSec) {
        mediaPlayer.seekTo(mSec);
    }

    public void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }

    public void setOnCompletionListener() {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlaying != null) {
            actionPlaying.setNextPlay();
        }
        notificationBuilder();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        position = sharedPreferences.getInt(POSITION, -1);
        path = sharedPreferences.getString(PATH, null);
    }

    public void notificationBuilder() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1, "Badran", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Desc..");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2, "Badran Music", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("Channel 2 Desc..");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }

        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID_1);
        builder.setSmallIcon(R.drawable.ic_baseline_play_circle)
                .setContentTitle("BV Player")
                .setContentText(myFiles.get(position).getTitle())
                .addAction(R.drawable.ic_baseline_skip_previous, "previous", null)
                .addAction(playPauseImage, "playPause", pendingIntent)
                .addAction(R.drawable.baseline_skip_next_24, "next", null)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(isPlay)
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);
        nmc.notify(10, builder.build());
    }

}