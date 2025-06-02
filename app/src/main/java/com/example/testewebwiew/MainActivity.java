package com.example.testewebwiew;

import static android.content.ContentValues.TAG;
import static android.content.Intent.getIntent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private OkHttpClient client = new OkHttpClient();
    private String currentVideoId;

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

        // Poll functionality
        RadioGroup pollGroup = findViewById(R.id.pollGroup);
        Button pollSubmit = findViewById(R.id.pollSubmit);
        pollSubmit.setOnClickListener(v -> {
            int selectedId = pollGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(MainActivity.this, "Selecione uma opção", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = findViewById(selectedId);
            String vote = selectedRadioButton.getText().toString();
            Toast.makeText(MainActivity.this, "Você votou: " + vote, Toast.LENGTH_SHORT).show();
            // Here you would typically send the vote to a server
        });

        // Comments functionality
        EditText commentInput = findViewById(R.id.commentInput);
        Button commentSubmit = findViewById(R.id.commentSubmit);
        LinearLayout commentsContainer = findViewById(R.id.commentsContainer);

        commentSubmit.setOnClickListener(v -> {
            String comment = commentInput.getText().toString().trim();
            if (comment.isEmpty()) {
                Toast.makeText(this, "Digite um comentário", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add comment to the UI
            TextView commentView = new TextView(this);
            commentView.setText(comment);
            commentView.setPadding(0, 8, 0, 8);
            commentsContainer.addView(commentView);

            commentInput.setText("");
            Toast.makeText(this, "Comentário adicionado", Toast.LENGTH_SHORT).show();

        });
    }
}
