package com.example.testewebwiew;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;

import org.json.JSONObject;
import java.io.IOException;

public class ResponderEnqueteActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://czflkjinwqeokpxesucd.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E"; // ⚠️ Use uma variável de ambiente segura

    private int enqueteId;
    private EditText editRespTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_enquete);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_500));
        }

        TextView txtTitulo = findViewById(R.id.Titulo);
        editRespTxt = findViewById(R.id.Resposta);
        Button btnEnviar = findViewById(R.id.btnEnviarResposta);

        enqueteId = getIntent().getIntExtra("enquete_id", -1);
        String titulo = getIntent().getStringExtra("enquete_titulo");
        txtTitulo.setText(titulo);

        btnEnviar.setOnClickListener(v -> enviarResposta());
    }

    private void enviarResposta() {
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String usuarioMatriculaStr = prefs.getString("matricula", null); // pegando do perfil

        if (usuarioMatriculaStr == null) {
            Toast.makeText(this, "Usuário não está logado. Faça login primeiro.", Toast.LENGTH_SHORT).show();
            return;
        }

        String respostaTexto = editRespTxt.getText().toString().trim();
        if (respostaTexto.isEmpty()) {
            Toast.makeText(this, "Digite sua resposta.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int usuarioMatricula = Integer.parseInt(usuarioMatriculaStr);
            enviarRespostaJson(usuarioMatricula, respostaTexto);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Matrícula do usuário inválida.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void enviarRespostaJson(int usuarioMatricula, String respostaTexto) {
        try {
            // Corrigir o nome da coluna para enquetes_id
            JSONObject json = new JSONObject();
            json.put("resp_matricula", usuarioMatricula);
            json.put("enquetes_id", enqueteId);
            json.put("resp_txt", respostaTexto);

            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(SUPABASE_URL + "/rest/v1/resposta") // ✅ endpoint correto
                    .addHeader("apikey", SUPABASE_API_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(ResponderEnqueteActivity.this, "Erro de conexão ao enviar resposta", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(ResponderEnqueteActivity.this, "Resposta enviada com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(
                                ResponderEnqueteActivity.this,
                                "Erro ao enviar resposta. Código: " + response.code(),
                                Toast.LENGTH_LONG).show());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(ResponderEnqueteActivity.this, "Erro ao montar JSON", Toast.LENGTH_SHORT).show());
        }
    }
}
