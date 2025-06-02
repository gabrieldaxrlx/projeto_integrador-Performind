package com.example.testewebwiew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class MainActivity extends AppCompatActivity {
    private PlayerUiController playerUiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtubePlayerr);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.setEnableAutomaticInitialization(false);

        View controlsUi = youTubePlayerView.inflateCustomPlayerUi(R.layout.custom_controls);

        String videoId = getIntent().getStringExtra("videoId");
        if (videoId == null) {
            videoId = "N6NJUYTmCYQ"; // ID padrão
        }

        String finalVideoId = videoId;
        YouTubePlayerListener youTubePlayerListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                playerUiController = new PlayerUiController(controlsUi, youTubePlayerView, youTubePlayer);
                youTubePlayer.addListener(playerUiController);
                YouTubePlayerUtils.loadOrCueVideo(youTubePlayer, getLifecycle(), finalVideoId, 0F);
            }
        };

        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
        youTubePlayerView.initialize(youTubePlayerListener, options);

        findViewById(R.id.cadastroNewVideo).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListaVideosActivity.class);
            startActivity(intent);
        });
    }
}