package com.example.testewebwiew;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnExcluir = findViewById(R.id.btnDeleteVideo);
        if (btnExcluir == null) {
            Log.e(TAG, "btnDeleteVideo não encontrado no layout!");
            Toast.makeText(this, "Erro: botão de excluir não encontrado", Toast.LENGTH_LONG).show();
            return;
        }

        btnExcluir.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Excluir vídeo")
                    .setMessage("Tem certeza que deseja excluir este vídeo?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        String videoIdParaExcluir = getVideoIdToDelete();

                        if (videoIdParaExcluir == null || videoIdParaExcluir.isEmpty()) {
                            Toast.makeText(MainActivity.this, "ID do vídeo inválido", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        excluirVideoDoBanco(videoIdParaExcluir);
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });
    }

    private String getVideoIdToDelete() {

        return "20";
    }

    private void excluirVideoDoBanco(String videoDbId) {
        String url = "https://czflkjinwqeokpxesucd.supabase.co/rest/v1/videos?id=eq." + videoDbId;

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Falha ao excluir vídeo", e);
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Falha ao excluir vídeo: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Vídeo excluído com sucesso");
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Vídeo excluído com sucesso!", Toast.LENGTH_SHORT).show();
                        // Atualize UI ou recarregue lista aqui se necessário
                    });
                } else {
                    Log.e(TAG, "Erro ao excluir vídeo: " + response.message());
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "Erro ao excluir vídeo: " + response.message(), Toast.LENGTH_SHORT).show()
                    );
                }
                response.close();
            }
        });
    }
}
