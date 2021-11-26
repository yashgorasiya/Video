package com.yjisolutions.video.Activities;

/*
https://github.com/google/ExoPlayer/blob/r2.11.7/demos/main/src/main/java/com/google/android/exoplayer2/demo/TrackSelectionDialog.java#L115
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.rubensousa.previewseekbar.PreviewLoader;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.yjisolutions.video.Adapters.PlayerVideoAdapter;
import com.yjisolutions.video.Fragments.VideosFragment;
import com.yjisolutions.video.Modal.Video;
import com.yjisolutions.video.R;
import com.yjisolutions.video.code.Conversion;
import com.yjisolutions.video.code.SetupFullDialog;
import com.yjisolutions.video.code.TrackSelectionDialog;
import com.yjisolutions.video.code.Utils;

import java.util.Objects;

import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

public class PlayerActivity extends AppCompatActivity{

    // Experimental
    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector trackSelector;

    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    private GestureDetectorCompat gestureDetectorCompat;
    DisplayMetrics metrics = new DisplayMetrics();
    private int INDICATOR_WIDTH = 600;
    private ConstraintLayout BIndicator, VIndicator;
    private LinearLayout SeekGesturePreviewLayout;
    private int getWidth, getHeight;
    private int cPotion;
    private int position;
    private float PlayBackSpeed = 1.00f;
    private float NightIntensity = 0.0f;
    private Video video;
    private boolean fromExternal = false;
    private View playListView;
//    private FrameLayout PlayListView;

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables", "SourceLockedOrientationActivity", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        position = getIntent().getIntExtra("position", 0);

        playListView = findViewById(R.id.playListLayout);
        playListView.animate().translationXBy(1000).setDuration(0);

        ImageView fitToScreen = findViewById(R.id.fitToScreen);

        ImageView backArrow = findViewById(R.id.controllerBackArrow);
        ImageView moreControls = findViewById(R.id.moreControls);
        ImageView playerNight = findViewById(R.id.playerNight);
        ImageView subTitleToggle = findViewById(R.id.subTitleToggle);
        ImageView speedControl = findViewById(R.id.playback_speed);
        ImageView orientation = findViewById(R.id.screenRotation);
        ImageButton trackSelection = findViewById(R.id.exo_track_selection_view);
        ImageView seekbarPreview = findViewById(R.id.seekbarPreview);
        PreviewTimeBar previewSeekBar = findViewById(R.id.exo_progress);
        PreviewLoader imagePreviewLoader = ImagePreviewLoader(seekbarPreview);
        previewSeekBar.setPreviewLoader(imagePreviewLoader);


        // Full Screen For Notch Devices
        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        getWindow().addFlags(FLAG_FULLSCREEN);

        getWindow().setFlags(FLAG_LAYOUT_NO_LIMITS,
                FLAG_LAYOUT_NO_LIMITS);


        getWindowManager().getDefaultDisplay().getMetrics(metrics);


        backArrow.setOnClickListener(v -> onBackPressed());
        speedControl.setOnClickListener(v -> PlayBackSpeedDialog());
        playerNight.setOnClickListener(v -> setNightIntensity());
        subTitleToggle.setOnClickListener(view -> SubTitleToggle(subTitleToggle));
        moreControls.setOnClickListener(v -> ShowPlayList());


        orientation.setOnClickListener(v -> {
            int ori = PlayerActivity.this.getResources().getConfiguration().orientation;
            if (ori == Configuration.ORIENTATION_PORTRAIT)
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

    private void ShowPlayList() {
        if (playerView.isControllerVisible()) playerView.hideController();
        playListView.setVisibility(View.VISIBLE);
        playListView.animate().translationX(0).setDuration(400);

        ImageView listClose = playListView.findViewById(R.id.playListClose);
        listClose.setOnClickListener(view -> playListView.animate().translationXBy(1000).setDuration(400));

        RecyclerView rc = playListView.findViewById(R.id.playListRV);
        PlayerVideoAdapter playerVideoAdapter = new PlayerVideoAdapter(this,playListView);
        rc.setLayoutManager(new LinearLayoutManager(this));
        rc.setAdapter(playerVideoAdapter);
        rc.setHasFixedSize(true);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void SubTitleToggle(ImageView btn) {
        int s = Objects.requireNonNull(playerView.getSubtitleView()).getVisibility();
        if (s == View.VISIBLE) {
            playerView.getSubtitleView().setVisibility(View.INVISIBLE);
            btn.setImageDrawable(getDrawable(R.drawable.subtitles_off));
        } else {
            playerView.getSubtitleView().setVisibility(View.VISIBLE);
            btn.setImageDrawable(getDrawable(R.drawable.subtitles));
        }
    }

    private void setNightIntensity() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetSpeed);
        bottomSheetDialog.setContentView(R.layout.bottom_night_dialog);
        SetupFullDialog.setupFullHeight(bottomSheetDialog, 270);
        ImageView imageView = bottomSheetDialog.findViewById(R.id.doneImgNDialog);
        Objects.requireNonNull(imageView).setOnClickListener(v -> bottomSheetDialog.cancel());

        final float[] intensities = {0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f};
        SeekBar seekBar = bottomSheetDialog.findViewById(R.id.nightSeekbar);
        Objects.requireNonNull(seekBar).setMax(10);
        int c = 0;
        for (float f : intensities) {
            if (NightIntensity == f) break;
            c++;
        }
        seekBar.setProgress(c);
        LinearLayout l = findViewById(R.id.LLNightSurface);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                NightIntensity = intensities[progress];
                l.setAlpha(NightIntensity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bottomSheetDialog.show();
    }


    @SuppressLint("DefaultLocale")
    private void PlayBackSpeedDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetSpeed);
        bottomSheetDialog.setContentView(R.layout.playback_speed_dialog);
        SetupFullDialog.setupFullHeight(bottomSheetDialog, 370);

        FloatingActionButton add = bottomSheetDialog.findViewById(R.id.speedIncreaseButton);
        FloatingActionButton remove = bottomSheetDialog.findViewById(R.id.speedDecreaseButton);
        TextView textView = bottomSheetDialog.findViewById(R.id.playBack_Speed_text);
        ImageView imageView = bottomSheetDialog.findViewById(R.id.doneImgPSDialog);
        Objects.requireNonNull(imageView).setOnClickListener(v -> bottomSheetDialog.cancel());
        Objects.requireNonNull(textView).setText(String.format("%.2f", PlayBackSpeed));

        Objects.requireNonNull(add).setOnClickListener(v -> {
            PlayBackSpeed = PlayBackSpeed + 0.05f;
            Objects.requireNonNull(textView).setText(String.format("%.2f", PlayBackSpeed));
            simpleExoPlayer.setPlaybackSpeed(PlayBackSpeed);
        });

        Objects.requireNonNull(remove).setOnClickListener(v -> {
            if (PlayBackSpeed > 0.10) PlayBackSpeed = PlayBackSpeed - 0.05f;
            Objects.requireNonNull(textView).setText(String.format("%.2f", PlayBackSpeed));
            simpleExoPlayer.setPlaybackSpeed(PlayBackSpeed);
        });

        bottomSheetDialog.show();

    }

    private PreviewLoader ImagePreviewLoader(ImageView imageView) {
        return (currentPosition, max) -> {
            RequestOptions options = new RequestOptions().frame(currentPosition * 1000);
            Glide.with(getBaseContext())
                    .asDrawable()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(video.getUri())
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

        //    private String videoTitle;


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


        BIndicator = findViewById(R.id.BrightnessCard);
        VIndicator = findViewById(R.id.VolumeCard);
        SeekGesturePreviewLayout = findViewById(R.id.SeekGesturePreviewLayout);

        getWidth = metrics.widthPixels;
        getHeight = metrics.heightPixels;

        playerView.setPlayer(simpleExoPlayer);

        playExo();

        setExoListener();

    }

    private void playExo() {
        try {
            video = VideosFragment.videos.get(position);
        } catch (Exception e) {
            fromExternal = true;
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, getIntent().getData());
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            video = new Video(getIntent().getData(), title, 0, 0);
        }

        MediaItem mediaItem = MediaItem.fromUri(video.getUri());

        if (simpleExoPlayer.isPlaying()) simpleExoPlayer.stop();

        simpleExoPlayer.setMediaItem(mediaItem);
        simpleExoPlayer.prepare();

        if (!fromExternal) {
            long lastPlayed = Utils.sp.getLong(video.getName(), 0);
            if (lastPlayed > 0) {
                if (lastPlayed >= simpleExoPlayer.getDuration()) {
                    Snackbar.make(this.findViewById(android.R.id.content), "Play From Start", Snackbar.LENGTH_SHORT)
                            .setAction("START", v1 -> simpleExoPlayer.seekTo(0))
                            .setActionTextColor(Color.RED)
                            .setBackgroundTint(Color.BLACK)
                            .setTextColor(Color.WHITE)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show();
                }
                simpleExoPlayer.seekTo(lastPlayed - 3000);
            }
        }

        TextView playerTitle = findViewById(R.id.titlePlayer);
        playerTitle.setText(video.getName());

        simpleExoPlayer.play();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setExoListener() {
        playerView.setControllerShowTimeoutMs(2000);

        playerView.setControllerVisibilityListener(visibility -> {
            if (visibility == 8) {
                hideSystemUi();
            } else {
                showSystemUi();
            }
        });

        playerView.setOnTouchListener((@SuppressLint("ClickableViewAccessibility") View v, @SuppressLint("ClickableViewAccessibility") MotionEvent event) -> {

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
                    saveLastPosition();
                    ShowPlayList();
                }
            }

            //            @Override
//            public void onVideoSizeChanged(@NonNull VideoSize videoSize) {
//                if (videoSize.height < videoSize.width) {
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                }
//            }
        });
    }

    void saveLastPosition() {
        if (!fromExternal) {
            if (!Utils.setRecentlyPlayed(position,
                    VideosFragment.folderName,
                    video.getName(),
                    simpleExoPlayer.getCurrentPosition()))
                Toast.makeText(getApplicationContext(), "Failed To Save Last Position", Toast.LENGTH_SHORT).show();
        }
    }

    public void hideSystemUi() {
        getWindow().clearFlags(FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
        getWindow().clearFlags(FLAG_TRANSLUCENT_STATUS);
        getWindow().clearFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

    public void showSystemUi() {
        getWindow().setFlags(FLAG_FORCE_NOT_FULLSCREEN, FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


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
        if (simpleExoPlayer.isPlaying()) simpleExoPlayer.pause();
        super.onPause();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void adjustVolume(float yPercent) {
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        // in my case max volume is 16
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int volume = (int) (yPercent * maxVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        VIndicator(yPercent);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void adjustBrightness(float yPercent) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = yPercent;
        window.setAttributes(lp);
        BIndicator(yPercent);
    }

    // Brightness And Volume Indicator Canter of Screen While using Gesture
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void BIndicator(float p) {
        float temp = 100 * p;
        int val = (int) temp;
        BIndicator.setVisibility(View.VISIBLE);
        TextView BVText = findViewById(R.id.BrightnessText);
        SeekBar seekBar = findViewById(R.id.BrightnessSeekBar);
        seekBar.setMax(100);
        seekBar.setProgress(val);
        BVText.setText(String.format("%02d", val) + "%");
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void VIndicator(float p) {
        float temp = 100 * p;
        int val = (int) temp;
        VIndicator.setVisibility(View.VISIBLE);
        TextView BVText = findViewById(R.id.VolumeText);
        SeekBar seekBar = findViewById(R.id.VolumeSeekBar);
        seekBar.setMax(100);
        seekBar.setProgress(val);
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
        BIndicator.setVisibility(View.INVISIBLE);
        VIndicator.setVisibility(View.INVISIBLE);
        SeekGesturePreviewLayout.setVisibility(View.INVISIBLE);
    }

    private class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private final boolean canUseWipeControls = true;
        private float maxVerticalMovement;
        private float maxHorizontalMovement;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Seek Gesture
            float xPercent = e1.getX() - e2.getX();
            float distanceXSinceTouchbegin = e1.getX() - e2.getX();
            maxHorizontalMovement = Math.max(maxHorizontalMovement, Math.abs(distanceXSinceTouchbegin));
            boolean enoughHorizontalMovement = maxHorizontalMovement > 100;
            boolean SafeAreaBackBtn = e2.getX() < (getWidth - 300);


            float distanceYSinceTouchbegin = e1.getY() - e2.getY();
            maxVerticalMovement = Math.max(maxVerticalMovement, Math.abs(distanceYSinceTouchbegin));
            boolean enoughVerticalMovement = maxVerticalMovement > 100;

            if (enoughHorizontalMovement && !enoughVerticalMovement && SafeAreaBackBtn) {
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
            if (playListView.getVisibility() == View.VISIBLE) {
                playListView.animate().translationXBy(1000).setDuration(400);
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            cPotion = (int) simpleExoPlayer.getCurrentPosition();
            maxVerticalMovement = 0;
            maxHorizontalMovement = 0;
            //canUseWipeControls = !playerView.isControllerVisible();
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