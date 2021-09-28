package com.yjisolutions.video;

import static android.view.View.GONE;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yjisolutions.video.code.sizeConversion;

public class player extends AppCompatActivity {


    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    private GestureDetectorCompat gestureDetectorCompat;
    DisplayMetrics metrics = new DisplayMetrics();
    private int INDICATOR_WIDTH = 600;
    private LinearLayout BVIndicator;
    LinearLayout SeekGesturePreviewLayout;
    private int getWidth,getHeight;
    private int cpotion;
    private Uri videoURL;
    private String videoTitle;

    SharedPreferences sp;
    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        videoURL = Uri.parse(getIntent().getStringExtra("url"));
        videoTitle = getIntent().getStringExtra("title");
        MediaItem mediaItem = MediaItem.fromUri(videoURL);
        ImageView fitToScreen = findViewById(R.id.fitToScreen);
        TextView playerTitle = findViewById(R.id.titlePlayer);
        ImageView backArrow = findViewById(R.id.controllerbackarrow);
//        ImageView subtitleCC = findViewById(R.id.subtitle);
        ImageView moreControls = findViewById(R.id.moreControls);


        // Full Screen For Notch Devices
        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        getWindowManager().getDefaultDisplay().getMetrics(metrics);


        playerTitle.setText(videoTitle);
        backArrow.setOnClickListener(v -> finish());
//        subtitleCC.setOnClickListener(v -> { subTitleToggle(subtitleCC); });
        moreControls.setOnClickListener(v -> moreControlls());

        fitToScreen.setOnClickListener(v -> { videoFitToScreen(fitToScreen); });

        // Init Player
        init(mediaItem);
        hideSystemUi();


    }

    private void moreControlls() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.player_more_control_bottom_sheet);

        LinearLayout share = bottomSheetDialog.findViewById(R.id.playbackSpeed);
        LinearLayout audioTrack = bottomSheetDialog.findViewById(R.id.audioTrack);
        LinearLayout subtitle = bottomSheetDialog.findViewById(R.id.subTitle);
        subtitle.setOnClickListener(v -> subTitleToggle());

        bottomSheetDialog.show();

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    void videoFitToScreen(ImageView fitToScreen){
        if (simpleExoPlayer.getVideoScalingMode() == VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING) {
            fitToScreen.setImageDrawable(getDrawable(R.drawable.ic_baseline_crop_7_5_24));
            simpleExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        } else {
            fitToScreen.setImageDrawable(getDrawable(R.drawable.ic_baseline_crop_5_4_24));
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            simpleExoPlayer.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void subTitleToggle(){
        SubtitleView subtitleView = playerView.getSubtitleView();
        if (subtitleView != null) {
            if (subtitleView.getVisibility()==GONE){
                subtitleView.setVisibility(View.VISIBLE);
            }else {
                subtitleView.setVisibility(GONE);
            }
        }
    }
    @SuppressLint({"ClickableViewAccessibility"})
    public void init(MediaItem mediaItem) {

        sp = getSharedPreferences("UserData",Context.MODE_PRIVATE);
        gestureDetectorCompat = new GestureDetectorCompat(getApplicationContext(), new MyGestureDetector());
        playerView = findViewById(R.id.idExoPlayerVIew);

        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();

        BVIndicator = findViewById(R.id.BrightnessVolumeCard);
        SeekGesturePreviewLayout = findViewById(R.id.SeekGesturePreviewLayout);

        getWidth = metrics.widthPixels;
        getHeight = metrics.heightPixels;

        playerView.setPlayer(simpleExoPlayer);

        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare();

        long lastPlayed = sp.getLong(videoTitle,0)-3000;
        if (lastPlayed>0){ simpleExoPlayer.seekTo(lastPlayed); }

        simpleExoPlayer.play();

        playerView.setControllerShowTimeoutMs(2000);

        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (!isPlaying){
                   saveLastPosition();
                }
            }
        });

        playerView.setControllerVisibilityListener(visibility -> {
            if (visibility==8){
                hideSystemUi();
            }else{
                showSystemUi();
            }
        });
        playerView.setOnTouchListener((v, event) -> {

            gestureDetectorCompat.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    endScroll();
                    break;
            }
            return playerView.getUseController();
        });
        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    finish();
                    releasePlayer();
                }
            }
        });

    }

    void saveLastPosition(){
        SharedPreferences.Editor spe = sp.edit();
        spe.putLong(videoTitle,simpleExoPlayer.getCurrentPosition());
        final boolean commit = spe.commit();
    }

    public void hideSystemUi() {
        getWindow().clearFlags(FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
        getWindow().clearFlags(FLAG_TRANSLUCENT_STATUS);
        getWindow().clearFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    public void showSystemUi() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            swapHeightWidth();
            INDICATOR_WIDTH = getWidth/2;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            swapHeightWidth();
            INDICATOR_WIDTH = getWidth/2;
        }
    }

    void swapHeightWidth(){
        int temp = getWidth;
        getWidth = getHeight;
        getHeight = temp;
    }

    private void releasePlayer() {
        simpleExoPlayer.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveLastPosition();
        simpleExoPlayer.stop();
    }

    @Override
    public void onBackPressed() {
        saveLastPosition();
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveLastPosition();
        simpleExoPlayer.pause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        saveLastPosition();
        simpleExoPlayer.stop();
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")



    private void adjustVolume(float yPercent) {
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int cVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int volume= (int) (yPercent * maxVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

        BVIndicator(yPercent,getDrawable(R.drawable.ic_baseline_volume_up_24));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void adjustBrightness(float yPercent) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = yPercent;
        window.setAttributes(lp);
        BVIndicator(yPercent,getDrawable(R.drawable.ic_baseline_brightness_5_24));
    }

    // Brightness And Volume Indicator Canter of Screen While using Gesture
    private void BVIndicator(float persent, Drawable res){
        float temp = 100*persent;
        int val = (int)temp;
        BVIndicator.setVisibility(View.VISIBLE);
        ImageView BV = findViewById(R.id.BrightnessVolumeRes);
        TextView BVText = findViewById(R.id.BrightnessVolumeText);
        BV.setImageDrawable(res);
        BVText.setText(String.format("%02d",val)+"%");
//        BVText.setText(persent+"");
    }

    // Seek bar Indicator Canter of Screen While using Gesture
    private void SeekPreviewIndicator(String text){
        SeekGesturePreviewLayout.setVisibility(View.VISIBLE);
        TextView SeekGesPreview = findViewById(R.id.SeekGesPreview);
        SeekGesPreview.setText(text);
    }

    public void performClick() {
        if (playerView.getUseController()) {
            toggleControls();
        }
    }

    public void toggleControls() {
        if (playerView.isControllerVisible()) {
            playerView.hideController();
            hideSystemUi();
        } else {
            playerView.showController();
            showSystemUi();
        }
    }

    private void endScroll() {
        BVIndicator.setVisibility(View.INVISIBLE);
        SeekGesturePreviewLayout.setVisibility(View.INVISIBLE);
    }



    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private boolean canUseWipeControls = true;
        private float maxVerticalMovement;
        private float maxHorizontalMovement;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Seek Gesture
            float xPercent = e1.getX() - e2.getX();
            float distanceXSinceTouchbegin = e1.getX() - e2.getX();
            maxHorizontalMovement = Math.max(maxHorizontalMovement, Math.abs(distanceXSinceTouchbegin));
            boolean enoughHorizontalMovement = maxHorizontalMovement > 100;

            if (enoughHorizontalMovement) {
                String ctime = sizeConversion.timerConversion(cpotion);

                // right to left
                if (xPercent>0) {
                    simpleExoPlayer.seekTo((long) (cpotion-10*xPercent));
                    SeekPreviewIndicator(sizeConversion.timerConversion((long) (cpotion-10*xPercent))+"/"+ctime);
                    return true;
                }
                // left to right swipe
                else if (xPercent<=0) {
                    simpleExoPlayer.seekTo((long) (-10*xPercent+cpotion));
                    SeekPreviewIndicator(sizeConversion.timerConversion((long) (-10*xPercent+cpotion))+"/"+ctime);
                    return true;
                }
            }


            // Volume And Brightness gestures
            if (!canUseWipeControls || e1 == null) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            float distanceYSinceTouchbegin = e1.getY() - e2.getY();
            maxVerticalMovement = Math.max(maxVerticalMovement, Math.abs(distanceYSinceTouchbegin));
            boolean enoughVerticalMovement = maxVerticalMovement > 100;

            if (!enoughVerticalMovement) { return super.onScroll(e1, e2, distanceX, distanceY); }

            float yPercent = 1 - (e2.getY() / getHeight);

            if (e2.getX() < INDICATOR_WIDTH) {
                adjustBrightness(yPercent);
                return true;
            } else if (e2.getX() > getWidth - INDICATOR_WIDTH) {
                adjustVolume(yPercent);
                return true;
            } else {
                endScroll();
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            performClick();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            cpotion = (int) simpleExoPlayer.getCurrentPosition();
            maxVerticalMovement = 0;
            maxHorizontalMovement = 0;
            canUseWipeControls = !playerView.isControllerVisible();
            return super.onDown(e);
        }



        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // devided double tap gestures to three part of screen

                // Forward
                if (e.getX() > getWidth * 2 / 3) {
                    SeekPreviewIndicator("+10 Seconds");
                    simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);
                    return true;
                }
                // Backward
                if (e.getX() < getWidth / 3) {
                    SeekPreviewIndicator("-10 Seconds");
                    simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
                    return true;
                }
                // Play Pause
                if (getWidth / 3 < e.getX() && e.getX() < getWidth * 2 / 3) {
                    if (simpleExoPlayer.isPlaying()) {
                        simpleExoPlayer.pause();
                    } else {
                        simpleExoPlayer.play();
                    }
                    return true;

            }
            return super.onDoubleTap(e);
        }

    }

}