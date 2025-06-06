package com.example.testewebwiew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://czflkjinwqeokpxesucd.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E";

    private final OkHttpClient client = new OkHttpClient();
    private PlayerUiController playerUiController;
    private YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_500));
        }

        youTubePlayerView = findViewById(R.id.youtubePlayerr);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.setEnableAutomaticInitialization(false);

        String videoUrl = getIntent().getStringExtra("videoUrl");
        String videoId = null;

        if (videoUrl != null && !videoUrl.isEmpty()) {
            videoId = extrairIdDoYoutube(videoUrl);
        } else {
            videoId = getIntent().getStringExtra("videoId");
        }

        if (videoId == null || videoId.isEmpty()) {
            videoId = "N6NJUYTmCYQ";
        }

        View controlsUi = youTubePlayerView.inflateCustomPlayerUi(R.layout.custom_controls);

        String finalVideoId = videoId;
        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                playerUiController = new PlayerUiController(controlsUi, youTubePlayerView, youTubePlayer);
                youTubePlayer.addListener(playerUiController);
                YouTubePlayerUtils.loadOrCueVideo(youTubePlayer, getLifecycle(), finalVideoId, 0f);
            }
        }, new IFramePlayerOptions.Builder().controls(0).build());
    }
    private String extrairIdDoYoutube(String url) {
        if (url.contains("youtu.be/")) {
            return url.substring(url.lastIndexOf("/") + 1);
        } else if (url.contains("v=")) {
            String[] parts = url.split("v=");
            String idPart = parts[1];
            int ampIndex = idPart.indexOf("&");
            return ampIndex != -1 ? idPart.substring(0, ampIndex) : idPart;
        }
        return null;
    }
}
