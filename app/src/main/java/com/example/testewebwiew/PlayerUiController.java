package com.example.testewebwiew;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.utils.FadeViewHelper;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.views.YouTubePlayerSeekBarListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class PlayerUiController extends AbstractYouTubePlayerListener {
    private final YouTubePlayerView youTubePlayerView;
    private final YouTubePlayer youTubePlayer;
    private final YouTubePlayerTracker playerTracker;
    private boolean isFullScreen = false;

    public PlayerUiController(View controlsUi, YouTubePlayerView youTubePlayerView, YouTubePlayer youTubePlayer) {
        this.youTubePlayerView = youTubePlayerView;
        this.youTubePlayer = youTubePlayer;
        playerTracker = new YouTubePlayerTracker();
        youTubePlayer.addListener(playerTracker);
        initViews(controlsUi);
    }

    private void initViews(View view) {
        View container = view.findViewById(R.id.container);
        RelativeLayout relativeLayout = view.findViewById(R.id.root);
        YouTubePlayerSeekBar seekBar = view.findViewById(R.id.seekBar);
        ImageButton pausePlay = view.findViewById(R.id.pausePlay);
        ImageButton fullScreen = view.findViewById(R.id.togleFullScreen);
        youTubePlayer.addListener(seekBar);

        seekBar.setYoutubePlayerSeekBarListener(new YouTubePlayerSeekBarListener() {
            @Override
            public void seekTo(float v) {
                youTubePlayer.seekTo(v);
            }
        });

        pausePlay.setOnClickListener(v -> {
            if (playerTracker.getState() == PlayerConstants.PlayerState.PLAYING) {
                pausePlay.setImageResource(R.drawable.outline_auto_read_play_24);
                youTubePlayer.pause();
            } else {
                pausePlay.setImageResource(R.drawable.outline_auto_read_pause_24);
                youTubePlayer.play();
            }
        });

        fullScreen.setOnClickListener(v -> {
            if (!isFullScreen) {
                youTubePlayerView.wrapContent();
            } else {
                youTubePlayerView.matchParent();
            }
            isFullScreen = !isFullScreen;
        });

        FadeViewHelper fadeViewHelper = new FadeViewHelper(container);
        fadeViewHelper.setAnimationDuration(FadeViewHelper.DEFAULT_ANIMATION_DURATION);
        fadeViewHelper.setFadeOutDelay(FadeViewHelper.DEFAULT_FADE_OUT_DELAY);
        youTubePlayer.addListener(fadeViewHelper);

        relativeLayout.setOnClickListener(view1 -> fadeViewHelper.toggleVisibility());
    }

    public void changeVideo(String videoId, float startTime) {
        if (youTubePlayer == null) return;

        // Usa o lifecycle da Activity como fallback
        Object lifecycle = (youTubePlayerView != null) ?
                youTubePlayerView.getLayerType() :
                getActivityLifecycle();

        YouTubePlayerUtils.loadOrCueVideo(youTubePlayer, (Lifecycle) lifecycle, videoId, startTime);
    }

    private Lifecycle getActivityLifecycle() {
        if (youTubePlayerView.getContext() instanceof AppCompatActivity) {
            return ((AppCompatActivity) youTubePlayerView.getContext()).getLifecycle();
        }
        return null;
    }
}