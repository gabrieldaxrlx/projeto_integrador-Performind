package com.example.testewebwiew;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class ListaExcluirVideoActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://czflkjinwqeokpxesucd.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E";

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();
    private List<ItemVideo> listaVideos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_excluir_video);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_500));
        }

        recyclerView = findViewById(R.id.recyclerViewVideos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VideoAdapter(listaVideos, this::confirmarExclusao, this);
        recyclerView.setAdapter(adapter);

        carregarVideosDoBanco();
    }

    private void carregarVideosDoBanco() {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/videos?select=*")
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ListaExcluirVideoActivity.this, "Erro ao carregar vídeos", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(res);
                        listaVideos.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id = obj.getInt("id");
                            String url = obj.getString("url");
                            String titulo = obj.getString("titulo");

                            listaVideos.add(new ItemVideo(id, url, titulo));
                        }

                        runOnUiThread(() -> adapter.notifyDataSetChanged());

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(ListaExcluirVideoActivity.this, "Erro ao processar dados", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ListaExcluirVideoActivity.this, "Erro ao buscar vídeos", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void confirmarExclusao(ItemVideo video) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Vídeo")
                .setMessage("Tem certeza que deseja excluir o vídeo: " + video.getTitle() + "?")
                .setPositiveButton("Sim", (dialog, which) -> excluirVideo(video))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirVideo(ItemVideo video) {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL + "/rest/v1/videos")
                .newBuilder()
                .addQueryParameter("id", "eq." + video.getId())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("Prefer", "return=minimal")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ListaExcluirVideoActivity.this, "Erro ao excluir vídeo", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        listaVideos.remove(video);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ListaExcluirVideoActivity.this, "Vídeo excluído!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListaExcluirVideoActivity.this, "Erro ao excluir", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}