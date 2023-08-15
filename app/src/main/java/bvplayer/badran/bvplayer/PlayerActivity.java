package bvplayer.badran.bvplayer;

import static bvplayer.badran.bvplayer.VideoFolderAdapter.folderVideoFile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import bvplayer.badran.bvplayer.databinding.ActivityPlayerBinding;

public class PlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback, ActionPlaying, ServiceConnection {

    ActivityPlayerBinding bundle;
    private int position;
    private static int testPosition;
    private String testPath;
    static String sender;
    private static String testSender = "Badran";
    static String mTimerIs = "00";
    static String sTimerIs = "00";
    int width;
    int height;
    int screenWidth;
    int screenHeight;
    static int playPauseImage;
    static ArrayList<VideoFiles> myFiles = new ArrayList<>();
    static boolean isPlay = false;
    static boolean playRepeat = false;
    static int colorRepeat = R.drawable.radius_false;
    static boolean playOnBackground = false;
    static int colorOnBackground = R.drawable.radius_false;
    static boolean playTimer = false;
    static private boolean visible = false;
    private final Handler handler = new Handler();
    SurfaceHolder surfaceHolder;
    VideoService videoService;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String POSITION = "position";
    public static final String PATH = "path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMethod();
        bundle = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(bundle.getRoot());
        loadData();
        setOnStartActivity();
        setVisibleTouch();
    }

    public void saveData(String path) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(POSITION, position);
        editor.putString(PATH, path);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        testPosition = sharedPreferences.getInt(POSITION, -1);
        testPath = sharedPreferences.getString(PATH, null);
    }

    private void setOnStartActivity() {
        isPlay = true;
        bundle.backgroundPlay.setOnClickListener(v -> {
            if (playOnBackground) {
                playOnBackground = false;
                colorOnBackground = R.drawable.radius_false;
                bundle.backgroundPlay.setBackgroundResource(colorOnBackground);
            } else {
                playOnBackground = true;
                colorOnBackground = R.drawable.radius_true;
                bundle.backgroundPlay.setBackgroundResource(colorOnBackground);
            }
        });

        bundle.repeatPlay.setOnClickListener(v -> {
            if (playRepeat) {
                playRepeat = false;
                colorRepeat = R.drawable.radius_false;
                bundle.repeatPlay.setBackgroundResource(colorRepeat);
            } else {
                playRepeat = true;
                colorRepeat = R.drawable.radius_true;
                bundle.repeatPlay.setBackgroundResource(colorRepeat);
            }
        });

        bundle.playTimerImage.setOnClickListener(v -> {
            if (!playTimer) {
                playTimer = true;
                bundle.playTimerText.setVisibility(View.VISIBLE);
                bundle.mTimerInput.setVisibility(View.VISIBLE);
                bundle.sTimerInput.setVisibility(View.VISIBLE);
                bundle.playTimerImage.setVisibility(View.GONE);
            }
        });
        setTimer();
        bundle.playTimerText.setOnClickListener(v -> {
            if (playTimer) {
                playTimer = false;
                bundle.playTimerImage.setVisibility(View.VISIBLE);
                bundle.mTimerInput.setVisibility(View.GONE);
                bundle.sTimerInput.setVisibility(View.GONE);
                bundle.playTimerText.setVisibility(View.GONE);
            }
        });
        position = getIntent().getIntExtra("position", testPosition);
        sender = getIntent().getStringExtra("sender");
        myFiles = folderVideoFile;
        saveData(testPath);
        if (position != -1 && !Objects.equals(testSender, sender)) {
            testSender = sender;
            Log.e("STEEPS", "Steep 0.5 " + sender);
            Intent intent = new Intent(this, VideoService.class);
            intent.putExtra("servicePosition", position);
            startService(intent);
        }
    }

    private void setTimer() {
        bundle.mTimerInput.setOnFocusChangeListener((v, hasFocus) -> bundle.done.setVisibility(View.VISIBLE));
        bundle.sTimerInput.setOnFocusChangeListener((v, hasFocus) -> bundle.done.setVisibility(View.VISIBLE));
        bundle.done.setOnClickListener(v -> {
            if (bundle.mTimerInput.getText() != null) {
                mTimerIs = bundle.mTimerInput.getText().toString();
            } else {
                mTimerIs = "00";
            }
            if (bundle.sTimerInput.getText() != null) {
                sTimerIs = bundle.sTimerInput.getText().toString();
            } else {
                sTimerIs = "00";
            }
            String timer = mTimerIs + ":" + sTimerIs;
            bundle.playTimerText.setText(timer);
            bundle.done.setVisibility(View.GONE);
            bundle.mTimerInput.setVisibility(View.GONE);
            bundle.sTimerInput.setVisibility(View.GONE);
        });
    }

    public void setPreviousPlay() {
        if (position > 0 && !playRepeat) {
            position = position - 1;
        }
        setNewVideo();
        setVariables();
        testPosition = position;
    }

    public void setPlayPause() {
        isPlay = !isPlay;
        setVariables();
    }

    public void setNextPlay() {
        if (position < myFiles.size() - 1 && !playRepeat) {
            position = position + 1;
        }
        setNewVideo();
        setVariables();
        testPosition = position;
    }

    private void previousPlayThread() {
        Thread previousPlayThread = new Thread() {
            @Override
            public void run() {
                super.run();
                bundle.exoPrev.setOnClickListener(v -> setPreviousPlay());
            }
        };
        previousPlayThread.start();
    }

    private void playPauseThread() {
        Thread playPauseThread = new Thread() {
            @Override
            public void run() {
                super.run();
                bundle.exoPlayPause.setOnClickListener(v -> setPlayPause());
            }
        };
        playPauseThread.start();
    }

    private void nextPlayThread() {
        Thread nextPlayThread = new Thread() {
            @Override
            public void run() {
                super.run();
                bundle.xoNext.setOnClickListener(v -> setNextPlay());
            }
        };
        nextPlayThread.start();
    }

    private void setVisibleTouch() {

        bundle.exoplayerMovie.setOnClickListener(v -> {
            if (visible) {
                visible = false;
                bundle.bottoms.setVisibility(View.VISIBLE);
                bundle.appBarLayout.setVisibility(View.VISIBLE);
            } else {
                visible = true;
                bundle.bottoms.setVisibility(View.INVISIBLE);
                bundle.appBarLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setFullScreenMethod() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void rotateScreen(String path) {
        try {
            //Create a new instance of MediaMetadataRetriever
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            //Declare the Bitmap
            Bitmap bmp;
            //Set the video Uri as data source for MediaMetadataRetriever
            retriever.setDataSource(this, Uri.parse(path));
            //Get one "frame"/bitmap - * NOTE - no time was set, so the first available frame will be used
            bmp = retriever.getFrameAtTime();

            //Get the bitmap width and height
            width = bmp.getWidth();
            height = bmp.getHeight();

            //If the width is bigger then the height then it means that the video was taken in landscape mode and we should set the orientation to landscape
            if (width > height) {
                //Set orientation to landscape
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            //If the width is smaller then the height then it means that the video was taken in portrait mode and we should set the orientation to portrait
            if (width < height) {
                //Set orientation to portrait
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        } catch (RuntimeException ex) {
            //error occurred

        }
    }

    private void setNewVideo() {
        isPlay = true;
        visible = false;
        if (myFiles != null) {

            Log.e("POSITION2", "position is " + position);
            testPath = myFiles.get(position).getPath();
            rotateScreen(testPath);
            Log.e("STEEPS", " Steep 4" + position);
            try {
                if (videoService != null) {
                    if (position != testPosition) {
                        videoService.pause();
                        videoService.release();
                        videoService.createMediaPlayer(position);
                        Log.e("STEEPS" + testPosition, " Steep 5" + position);
                    }
                    surfaceHolder = bundle.exoplayerMovie.getHolder();
                    videoService.setDisplay(surfaceHolder);
                    videoService.prepareAsync();
                    surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                        @Override
                        public void surfaceCreated(@NonNull SurfaceHolder holder) {
                            videoService.setDisplay(holder);

                        }

                        @Override
                        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                            setVideoSize();
                        }

                        @Override
                        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

                        }
                    });
                    videoService.start();
                }
            } catch (Exception e) {
                Log.e("This", String.valueOf(e));
            }
            setVariables();
            Log.e("STEEPS", " Steep 7" + position);
        }
    }

    private void setVariables() {
        if (isPlay) {
            playPauseImage = R.drawable.ic_baseline_pause_circle;
            videoService.start();
        } else {
            playPauseImage = R.drawable.ic_baseline_play_circle;
            videoService.pause();
        }
        videoService.setOnCompletionListener();
        int mCurrentPosition = videoService.getDuration() / 1000;
        bundle.seekbar.setMax(mCurrentPosition);
        bundle.exoDuration.setText(formattedTime(mCurrentPosition));
        bundle.exoPlayPause.setImageResource(playPauseImage);
        bundle.barText.setText(testPath);
        bundle.backgroundPlay.setBackgroundResource(colorOnBackground);
        bundle.repeatPlay.setBackgroundResource(colorRepeat);
        saveData(testPath);
    }

    private void setSeekBar() {
        if (isPlay) {
            bundle.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        videoService.seekTo(progress * 1000);
                        int mCurrentPosition = videoService.getCurrentPosition() / 1000;
                        bundle.exoPosition.setText(formattedTime(mCurrentPosition));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (videoService != null) {
                        int mCurrentPosition = videoService.getCurrentPosition() / 1000;
                        bundle.seekbar.setProgress(mCurrentPosition);
                        bundle.exoPosition.setText(formattedTime(mCurrentPosition));
                        handler.post(this);
                    }
                }
            });
        }
    }

    private String formattedTime(int mCurrentPosition) {
        int seconds = mCurrentPosition % 60;
        double dHour = (mCurrentPosition / 60f) / 60.0;
        int hour = (int) dHour;
        int minutes = (int) ((dHour - hour) * 60);
        String s, m, h;
        if (seconds < 10) {
            s = "0" + seconds;
        } else {
            s = String.valueOf(seconds);
        }
        if (minutes < 10) {
            m = "0" + minutes;
        } else {
            m = String.valueOf(minutes);
        }
        if (hour == 0) {
            return m + ":" + s;
        } else {
            h = String.valueOf(hour);
            return h + ":" + m + ":" + s;
        }

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        videoService.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        setVideoSize();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    private void setVideoSize() {

        // // Get the dimensions of the video
        Toast.makeText(this, "it worked", Toast.LENGTH_SHORT).show();
        int videoWidth = width;
        int videoHeight = height;
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = bundle.exoplayerMovie.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        // Commit the layout parameters
        bundle.exoplayerMovie.setLayoutParams(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bundle.backBtn.setOnClickListener(v -> finish());
        previousPlayThread();
        playPauseThread();
        nextPlayThread();
        Intent intent = new Intent(this, VideoService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    public void finish() {
        super.finish();
        if (!playOnBackground) {
            videoService.pause();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (position != -1) {
            VideoService.MyBinder myBinder = (VideoService.MyBinder) service;
            videoService = myBinder.getService();
        }
        videoService.setCallBack(this);
        setNewVideo();
        setSeekBar();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        videoService = null;
    }
}