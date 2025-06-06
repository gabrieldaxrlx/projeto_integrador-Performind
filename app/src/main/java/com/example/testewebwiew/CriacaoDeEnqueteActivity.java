package com.example.testewebwiew;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class CriacaoDeEnqueteActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://czflkjinwqeokpxesucd.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E";

    private TextInputEditText editTitulo, editTema;
    private Button btnCriarEnquete;

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criacao_de_enquete);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_500));
        }// Confirme que seu layout tem os IDs usados abaixo

        editTitulo = findViewById(R.id.editTitulo);
        editTema = findViewById(R.id.editTema);
        btnCriarEnquete = findViewById(R.id.btnCriarEnquete);

        btnCriarEnquete.setOnClickListener(v -> criarEnquete());
    }

    private void criarEnquete() {
        String titulo = editTitulo.getText().toString().trim();
        String tema = editTema.getText().toString().trim();

        if (titulo.isEmpty() || tema.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        salvarEnqueteNoBanco(titulo, tema);
    }

    private void salvarEnqueteNoBanco(String titulo, String tema) {
        try {
            JSONObject json = new JSONObject();
            json.put("titulo", titulo);
            json.put("tema", tema);

            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/enquetes")
                    .addHeader("apikey", SUPABASE_API_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(CriacaoDeEnqueteActivity.this, "Erro ao conectar com o banco", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(CriacaoDeEnqueteActivity.this, "Enquete criada com sucesso!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(CriacaoDeEnqueteActivity.this, "Erro ao salvar enquete: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Erro ao criar JSON", Toast.LENGTH_SHORT).show());
        }
    }
}
