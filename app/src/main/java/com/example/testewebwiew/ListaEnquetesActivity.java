package com.example.testewebwiew;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListaEnquetesActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://czflkjinwqeokpxesucd.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E";

    private RecyclerView recyclerEnquetes;
    private EnqueteAdapter adapter;
    private List<Enquete> listaEnquetes = new ArrayList<>();

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_enquetes);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_500));
        }

        recyclerEnquetes = findViewById(R.id.recyclerEnquetes);
        recyclerEnquetes.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EnqueteAdapter(listaEnquetes, enquete -> {
            Intent intent = new Intent(ListaEnquetesActivity.this, ResponderEnqueteActivity.class);
            intent.putExtra("enquete_id", enquete.getId());
            intent.putExtra("enquete_titulo", enquete.getTitulo());
            startActivity(intent);
        });
        recyclerEnquetes.setAdapter(adapter);

        // Se quiser botão manual, crie no layout com id diferente e faça o tratamento aqui:
        /*
        Button btnResponderManual = findViewById(R.id.btnResponderManual);
        btnResponderManual.setOnClickListener(v -> {
            Intent intent = new Intent(ListaEnquetesActivity.this, ResponderEnqueteActivity.class);
            intent.putExtra("enquete_id", -1);
            intent.putExtra("enquete_titulo", "Responder Enquete Manual");
            startActivity(intent);
        });
        */

        carregarEnquetesDoBanco();
    }

    private void carregarEnquetesDoBanco() {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/enquetes?select=*")
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ListaEnquetesActivity.this, "Erro ao carregar enquetes", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(ListaEnquetesActivity.this, "Erro na resposta: " + response.code(), Toast.LENGTH_SHORT).show());
                    return;
                }

                String jsonData = response.body().string();

                try {
                    JSONArray jsonArray = new JSONArray(jsonData);
                    listaEnquetes.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String titulo = obj.getString("titulo");
                        String tema = obj.getString("tema");
                        listaEnquetes.add(new Enquete(id, titulo, tema));
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ListaEnquetesActivity.this, "Erro ao processar dados", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
