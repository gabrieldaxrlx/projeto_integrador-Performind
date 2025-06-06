package com.example.testewebwiew;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class CadastroVideo extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://czflkjinwqeokpxesucd.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E";

    private EditText editUrl, editTitulo;
    private Button btnSalvar;

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_video);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_500));
        }

        editUrl = findViewById(R.id.URLVideo);
        editTitulo = findViewById(R.id.TituloVideo);
        btnSalvar = findViewById(R.id.btnSalvar);

        btnSalvar.setOnClickListener(v -> {
            String url = editUrl.getText().toString().trim();
            String titulo = editTitulo.getText().toString().trim();

            if (url.isEmpty()) {
                editUrl.setError("Campo obrigatório");
                return;
            }

            if (titulo.isEmpty()) {
                editTitulo.setError("Campo obrigatório");
                return;
            }

            if (!isValidYouTubeUrl(url)) {
                editUrl.setError("URL do YouTube inválida");
                return;
            }

            salvarVideoNoBanco(url, titulo);
        });
    }

    private boolean isValidYouTubeUrl(String url) {
        String pattern = "^(https?://)?(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[\\w-]{11}(&\\S*)?$";
        return url.matches(pattern);
    }

    private void salvarVideoNoBanco(String url, String titulo) {
        try {
            JSONObject json = new JSONObject();
            json.put("url", url);
            json.put("titulo", titulo);

            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/videos")
                    .addHeader("apikey", SUPABASE_API_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(CadastroVideo.this, "Erro ao conectar com o banco", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(CadastroVideo.this, "Vídeo cadastrado com sucesso!", Toast.LENGTH_LONG).show();
                            Intent resultIntent = new Intent();
                            setResult(RESULT_OK, resultIntent);
                            finish(); // volta para ListaVideosActivity
                        } else {
                            Toast.makeText(CadastroVideo.this, "Erro ao salvar vídeo", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Erro ao criar JSON de envio.", Toast.LENGTH_SHORT).show());
        }
    }
}
