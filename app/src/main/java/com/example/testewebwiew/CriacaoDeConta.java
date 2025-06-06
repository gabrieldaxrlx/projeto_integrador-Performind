package com.example.testewebwiew;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class CriacaoDeConta extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://czflkjinwqeokpxesucd.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E";

    private TextInputEditText editNome, editMatricula, editSenha, editConfirmarSenha;
    private AutoCompleteTextView spinnerTipoUsuario;
    private Button btnCadastrar;

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criacao_de_conta);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_500));
        }

        editNome = findViewById(R.id.editNome);
        editMatricula = findViewById(R.id.editMatricula);
        editSenha = findViewById(R.id.editSenha);
        editConfirmarSenha = findViewById(R.id.editConfirmarSenha);
        spinnerTipoUsuario = findViewById(R.id.spinnerTipoUsuario);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        String[] tipos = {"admin", "funcionario"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tipos);
        spinnerTipoUsuario.setAdapter(adapter);

        btnCadastrar.setOnClickListener(v -> cadastrarUsuario());
    }

    private void cadastrarUsuario() {
        String nome = editNome.getText().toString().trim();
        String matricula = editMatricula.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();
        String confirmarSenha = editConfirmarSenha.getText().toString().trim();
        String tipoUsuario = spinnerTipoUsuario.getText().toString().trim();

        if (nome.isEmpty() || matricula.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty() || tipoUsuario.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show();
            return;
        }

        verificarMatriculaExistente(nome, matricula, senha, tipoUsuario);
    }

    private void verificarMatriculaExistente(String nome, String matricula, String senha, String tipoUsuario) {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL + "/rest/v1/usuarios")
                .newBuilder()
                .addQueryParameter("matricula", "eq." + matricula)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(CriacaoDeConta.this, "Erro ao verificar matrícula", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        if (json.equals("[]")) {
                            salvarUsuarioNoBanco(nome, matricula, senha, tipoUsuario);
                        } else {
                            Toast.makeText(CriacaoDeConta.this, "Matrícula já está em uso!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CriacaoDeConta.this, "Erro ao consultar matrícula", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void salvarUsuarioNoBanco(String nome, String matricula, String senha, String tipoUsuario) {
        try {
            JSONObject json = new JSONObject();
            json.put("nome", nome);
            json.put("matricula", matricula);
            json.put("senha", senha);
            json.put("tipo", tipoUsuario);

            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/usuarios")
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
                            Toast.makeText(CriacaoDeConta.this, "Erro ao conectar com o banco", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(CriacaoDeConta.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CriacaoDeConta.this, ListaVideosActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CriacaoDeConta.this, "Erro ao salvar (verifique os dados)", Toast.LENGTH_SHORT).show();
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
