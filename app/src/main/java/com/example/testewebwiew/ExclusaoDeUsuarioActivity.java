package com.example.testewebwiew;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExclusaoDeUsuarioActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://czflkjinwqeokpxesucd.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E";
    private RecyclerView recyclerView;
    private UsuarioAdapter adapter;
    private final List<Usuario> usuarios = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exclusao_de_usuario);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_500));
        }

        recyclerView = findViewById(R.id.recyclerUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UsuarioAdapter(usuarios, this, this::confirmarExclusao);
        recyclerView.setAdapter(adapter);

        carregarUsuarios();
    }

    private void carregarUsuarios() {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/usuarios")
                .get()
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONArray array = new JSONArray(json);
                            usuarios.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Usuario usuario = new Usuario(
                                        obj.getString("id"),
                                        obj.getString("nome"),
                                        obj.getString("matricula")
                                );
                                usuarios.add(usuario);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void confirmarExclusao(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Excluir " + usuario.nome + "?")
                .setPositiveButton("Sim", (dialog, which) -> excluirUsuario(usuario))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirUsuario(Usuario usuario) {
        HttpUrl url = HttpUrl.parse(SUPABASE_URL + "/rest/v1/usuarios")
                .newBuilder()
                .addQueryParameter("id", "eq." + usuario.id)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .addHeader("Prefer", "return=minimal")
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        usuarios.remove(usuario);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ExclusaoDeUsuarioActivity.this, "Usuário excluído", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}

