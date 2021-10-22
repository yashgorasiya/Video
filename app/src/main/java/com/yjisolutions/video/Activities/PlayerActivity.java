package com.yjisolutions.video.Activities;

/*
https://github.com/google/ExoPlayer/blob/r2.11.7/demos/main/src/main/java/com/google/android/exoplayer2/demo/TrackSelectionDialog.java#L115
 */

import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ffmpeg.FfmpegLibrary;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.yjisolutions.video.R;
import com.yjisolutions.video.code.Conversion;
import com.yjisolutions.video.code.TrackSelectionDialog;
import com.yjisolutions.video.code.Utils;

public class PlayerActivity extends AppCompatActivity {

    // Experimental
    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector trackSelector;
    String[] speed = {"0.25x", "0.5x", "Normal", "1.5x", "2x"};

    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    private GestureDetectorCompat gestureDetectorCompat;
    DisplayMetrics metrics = new DisplayMetrics();
    private int INDICATOR_WIDTH = 600;
    private LinearLayout BVIndicator;
    private LinearLayout SeekGesturePreviewLayout;
    private int getWidth, getHeight;
    private int cPotion;
    private String videoTitle;
    private MediaItem mediaItem;
    private Uri videoURL;

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables", "SourceLockedOrientationActivity", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        videoURL = Uri.parse(getIntent().getStringExtra("url"));
        videoTitle = getIntent().getStringExtra("title");
        mediaItem = MediaItem.fromUri(videoURL);

        ImageView fitToScreen = findViewById(R.id.fitToScreen);
        TextView playerTitle = findViewById(R.id.titlePlayer);
        ImageView backArrow = findViewById(R.id.controllerBackArrow);
        ImageView moreControls = findViewById(R.id.moreControls);

        ImageButton speedControl = findViewById(R.id.exo_playback_speed);
        ImageButton oriantation = findViewById(R.id.exo_fullscreen);
        ImageButton trackSelection = findViewById(R.id.exo_track_selection_view);
        TextView speedTxt = findViewById(R.id.speedTEXT);

        ImageView seekbarPreview = findViewById(R.id.seekbarPreview);
        PreviewTimeBar previewSeekBar = findViewById(R.id.exo_progress);
        PreviewLoader imagePreviewLoader = ImagePreviewLoader(seekbarPreview);
        previewSeekBar.setPreviewLoader(imagePreviewLoader);


        // Full Screen For Notch Devices
        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        getWindowManager().getDefaultDisplay().getMetrics(metrics);


        playerTitle.setText(videoTitle);
        backArrow.setOnClickListener(v -> onBackPressed());

        moreControls.setOnClickListener(v -> {
            // Do something in three dot Click
        });

        speedControl.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
            builder.setTitle("Set Speed");
            builder.setItems(speed, (dialog, which) -> {
                // the user clicked on colors[which]

                if (which == 0) {
                    speedTxt.setVisibility(View.VISIBLE);
                    speedTxt.setText("0.25X");
                    PlaybackParameters param = new PlaybackParameters(0.5f);
                    simpleExoPlayer.setPlaybackParameters(param);
                }
                if (which == 1) {
                    speedTxt.setVisibility(View.VISIBLE);
                    speedTxt.setText("0.5X");
                    PlaybackParameters param = new PlaybackParameters(0.5f);
                    simpleExoPlayer.setPlaybackParameters(param);
                }
                if (which == 2) {
                    speedTxt.setVisibility(View.GONE);
                    PlaybackParameters param = new PlaybackParameters(1f);
                    simpleExoPlayer.setPlaybackParameters(param);
                }
                if (which == 3) {
                    speedTxt.setVisibility(View.VISIBLE);
                    speedTxt.setText("1.5X");
                    PlaybackParameters param = new PlaybackParameters(1.5f);
                    simpleExoPlayer.setPlaybackParameters(param);
                }
                if (which == 4) {
                    speedTxt.setVisibility(View.VISIBLE);
                    speedTxt.setText("2X");
                    PlaybackParameters param = new PlaybackParameters(2f);
                    simpleExoPlayer.setPlaybackParameters(param);
                }
            });
            builder.show();
        });

        oriantation.setOnClickListener(v -> {
            int orientation = PlayerActivity.this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        });

        trackSelection.setOnClickListener(v -> trackSelectionSetup());

        fitToScreen.setOnClickListener(v -> videoFitToScreen(fitToScreen));


        trackSelector = new DefaultTrackSelector(this);

        // Init Player
        init();
        hideSystemUi();


    }

    private PreviewLoader ImagePreviewLoader(ImageView imageView) {
        return (currentPosition, max) -> {
            RequestOptions options = new RequestOptions().frame(currentPosition * 1000);
            Glide.with(getBaseContext())
                    .asDrawable()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(videoURL)
                    .apply(options)
                    .override(160, 90)
                    .into(imageView);
        };
    }


    private void trackSelectionSetup() {
        if (!isShowingTrackSelectionDialog
                && TrackSelectionDialog.willHaveContent(trackSelector)) {
            isShowingTrackSelectionDialog = true;
            TrackSelectionDialog trackSelectionDialog =
                    TrackSelectionDialog.createForTrackSelector(
                            trackSelector,
                            /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
            trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);

        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    void videoFitToScreen(ImageView fitToScreen) {
        if (simpleExoPlayer.getVideoScalingMode() == VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING) {
            switch (playerView.getResizeMode()) {
                case AspectRatioFrameLayout.RESIZE_MODE_FIT:
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH:
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT:
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_FILL:
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_ZOOM:
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    fitToScreen.setImageDrawable(getDrawable(R.drawable.ic_baseline_crop_7_5_24));
                    simpleExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                    break;
            }
        } else {
            switch (playerView.getResizeMode()) {
                case AspectRatioFrameLayout.RESIZE_MODE_FIT:
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH:
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT:
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_FILL:
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    break;
                case AspectRatioFrameLayout.RESIZE_MODE_ZOOM:
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    fitToScreen.setImageDrawable(getDrawable(R.drawable.ic_baseline_crop_5_4_24));
                    simpleExoPlayer.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    break;
            }
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void init() {


        if (!FfmpegLibrary.isAvailable())
            Toast.makeText(getApplicationContext(), "FFmpegLibrary not found", Toast.LENGTH_SHORT).show();

        gestureDetectorCompat = new GestureDetectorCompat(getApplicationContext(), new MyGestureDetector());
        playerView = findViewById(R.id.idExoPlayerVIew);

        final DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory()
                .setTsExtractorFlags(DefaultTsPayloadReaderFactory.FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS);

        // changed Renderer
        RenderersFactory renderersFactory = new DefaultRenderersFactory(this)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);

        simpleExoPlayer = new SimpleExoPlayer.Builder(this, renderersFactory)
                .setTrackSelector(trackSelector)
                .setMediaSourceFactory(new DefaultMediaSourceFactory(this, extractorsFactory))
                .build();


        BVIndicator = findViewById(R.id.BrightnessVolumeCard);
        SeekGesturePreviewLayout = findViewById(R.id.SeekGesturePreviewLayout);

        getWidth = metrics.widthPixels;
        getHeight = metrics.heightPixels;

        playerView.setPlayer(simpleExoPlayer);

        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare();

        long lastPlayed = Utils.sp.getLong(videoTitle, 0);
        if (lastPlayed > 0) {
            if (lastPlayed >= simpleExoPlayer.getDuration()) {
                Snackbar.make(this.findViewById(android.R.id.content), "Play From Start", Snackbar.LENGTH_LONG)
                        .setAction("START", v1 -> simpleExoPlayer.seekTo(0))
                        .setActionTextColor(Color.RED)
                        .setBackgroundTint(Color.BLACK)
                        .setTextColor(Color.WHITE)
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .show();
            }
            simpleExoPlayer.seekTo(lastPlayed - 3000);
        }

        simpleExoPlayer.play();

        playerView.setControllerShowTimeoutMs(2000);

        playerView.setControllerVisibilityListener(visibility -> {
            if (visibility == 8) {
                hideSystemUi();
            } else {
                showSystemUi();
            }
        });

        playerView.setOnTouchListener((v, event) -> {

            gestureDetectorCompat.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    endScroll();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Do something here
                    break;
            }
            return playerView.getUseController();
        });

        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (!isPlaying) {
                    saveLastPosition();
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    finish();
                    releasePlayer();
                }
            }

            @Override
            public void onVideoSizeChanged(@NonNull VideoSize videoSize) {
                if (videoSize.height < videoSize.width) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });

    }

    void saveLastPosition() {
        if (!Utils.setRecentlyPlayed(videoTitle,
                videoURL.toString(),
                simpleExoPlayer.getCurrentPosition()))
            Toast.makeText(getApplicationContext(), "Failed To Save Last Position", Toast.LENGTH_SHORT).show();
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
            INDICATOR_WIDTH = getWidth / 2;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            swapHeightWidth();
            INDICATOR_WIDTH = getWidth / 2;
        }
    }

    void swapHeightWidth() {
        int temp = getWidth;
        getWidth = getHeight;
        getHeight = temp;
    }

    private void releasePlayer() {
        if (simpleExoPlayer.isPlaying()) simpleExoPlayer.stop();
        simpleExoPlayer.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveLastPosition();
        releasePlayer();
    }

    @Override
    public void onBackPressed() {
        saveLastPosition();
        releasePlayer();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        simpleExoPlayer.play();
        super.onResume();
    }

    @Override
    protected void onPause() {
        simpleExoPlayer.pause();
        super.onPause();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void adjustVolume(float yPercent) {
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        // in my case max volume is 16
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int volume = (int) (yPercent * maxVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

        BVIndicator(yPercent, getDrawable(R.drawable.ic_baseline_volume_up_24));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void adjustBrightness(float yPercent) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = yPercent;
        window.setAttributes(lp);
        BVIndicator(yPercent, getDrawable(R.drawable.ic_baseline_brightness_5_24));
    }

    // Brightness And Volume Indicator Canter of Screen While using Gesture
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void BVIndicator(float persent, Drawable res) {
        float temp = 100 * persent;
        int val = (int) temp;
        BVIndicator.setVisibility(View.VISIBLE);
        ImageView BV = findViewById(R.id.BrightnessVolumeRes);
        TextView BVText = findViewById(R.id.BrightnessVolumeText);
        BV.setImageDrawable(res);
        BVText.setText(String.format("%02d", val) + "%");
    }

    // Seek bar Indicator Canter of Screen While using Gesture
    private void SeekPreviewIndicator(String text) {
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


            float distanceYSinceTouchbegin = e1.getY() - e2.getY();
            maxVerticalMovement = Math.max(maxVerticalMovement, Math.abs(distanceYSinceTouchbegin));
            boolean enoughVerticalMovement = maxVerticalMovement > 100;

            if (enoughHorizontalMovement && !enoughVerticalMovement) {
                String ctime = Conversion.timerConversion(cPotion);

                // right to left
                if (xPercent > 0) {
                    simpleExoPlayer.seekTo((long) (cPotion - 10 * xPercent));
                    SeekPreviewIndicator(Conversion.timerConversion((long) (cPotion - 10 * xPercent)) + "/" + ctime);
                    return true;
                }
                // left to right swipe
                else if (xPercent <= 0) {
                    simpleExoPlayer.seekTo((long) (-10 * xPercent + cPotion));
                    SeekPreviewIndicator(Conversion.timerConversion((long) (-10 * xPercent + cPotion)) + "/" + ctime);
                    return true;
                } else {
                    endScroll();
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }
            }


            // Volume And Brightness gestures
            if (!canUseWipeControls) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }


            if (!enoughVerticalMovement) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            float yPercent = 1 - (e2.getY() / getHeight);

            if (!enoughHorizontalMovement) {
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

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            performClick();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            cPotion = (int) simpleExoPlayer.getCurrentPosition();
            maxVerticalMovement = 0;
            maxHorizontalMovement = 0;
            canUseWipeControls = !playerView.isControllerVisible();
            return super.onDown(e);
        }


        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // divided double tap gestures to three part of screen

            if (playerView.isControllerVisible()) playerView.hideController();
            // Forward
            if (e.getX() > Float.parseFloat(String.valueOf(getWidth * 2 / 3))) {
                SeekPreviewIndicator("+10 Seconds");
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);
                return true;
            }
            // Backward
            if (e.getX() < Float.parseFloat(String.valueOf(getWidth / 3))) {
                SeekPreviewIndicator("-10 Seconds");
                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
                return true;
            }
            // Play Pause
            if (Float.parseFloat(String.valueOf(getWidth / 3)) <
                    e.getX() && e.getX() <
                    Float.parseFloat(String.valueOf(getWidth * 2 / 3))) {

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