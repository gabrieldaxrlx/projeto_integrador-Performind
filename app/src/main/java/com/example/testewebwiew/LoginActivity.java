package com.example.testewebwiew;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editMatricula, editSenha;
    private Button btnEntrar;

    // Substitua com sua URL e API KEY reais
    private static final String SUPABASE_URL = "https://czflkjinwqeokpxesucd.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editMatricula = findViewById(R.id.editMatricula);
        editSenha = findViewById(R.id.editSenha);
        btnEntrar = findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(v -> {
            String matricula = editMatricula.getText().toString().trim();
            String senha = editSenha.getText().toString().trim();

            if (matricula.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            verificarLogin(matricula, senha);
        });
    }

    private void verificarLogin(String matricula, String senha) {
        String url = SUPABASE_URL + "/rest/v1/usuarios?matricula=eq." + matricula + "&senha=eq." + senha;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Erro de conexão com o banco.", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();

                runOnUiThread(() -> {
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);

                        if (jsonArray.length() == 1) {
                            // Extrai o JSON do usuário
                            org.json.JSONObject userObj = jsonArray.getJSONObject(0);

                            String nome = userObj.optString("nome", "Usuário");
                            String matricula = userObj.optString("matricula", "");
                            String tipo = userObj.optString("tipo", ""); // Ajuste o campo conforme sua tabela

                            // Salva no SharedPreferences
                            getSharedPreferences("user_data", MODE_PRIVATE)
                                    .edit()
                                    .putString("nome", nome)
                                    .putString("matricula", matricula)
                                    .putString("tipo", tipo)
                                    .apply();

                            Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                            // Redireciona para ListaVideosActivity
                            Intent intent = new Intent(LoginActivity.this, ListaVideosActivity.class);
                            startActivity(intent);
                            finish(); // Fecha a tela de login

                        } else {
                            Toast.makeText(LoginActivity.this, "Matrícula ou senha incorretos.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Erro ao processar resposta do servidor.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}

