package com.example.testewebwiew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class ListaVideosActivity extends AppCompatActivity implements VideoAdapter.OnVideoClickListener {

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<ItemVideo> videos = new ArrayList<>();

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_videos);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VideoAdapter(videos, this, this);
        recyclerView.setAdapter(adapter);

        ImageButton btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(ListaVideosActivity.this, PerfilUsuario.class);
            startActivity(intent);
        });

        ImageButton btnNotificacao = findViewById(R.id.btnNotificacao);
        btnNotificacao.setOnClickListener(v -> {
            showNotificacoes();
        });

        ImageButton btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            showMenuLateral();
        });

        carregarVideosDoBanco(); // 🔽 CARREGA OS VÍDEOS AO INICIAR
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            carregarVideosDoBanco(); // 🔽 RECARREGA DEPOIS DE ADICIONAR VÍDEO
        }
    }

    @Override
    public void onVideoClick(ItemVideo video) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("videoId", video.getVideoId());
        startActivity(intent);
    }

    // 🔽 MÉTODO QUE CONSULTA O SUPABASE E ATUALIZA A LISTA
    private void carregarVideosDoBanco() {
        String url = "https://czflkjinwqeokpxesucd.supabase.co/rest/v1/videos?select=*";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN6Zmxramlud3Flb2tweGVzdWNkIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0Nzk1Mjk3NiwiZXhwIjoyMDYzNTI4OTc2fQ.n4bNzO29sqfmHf7-FXHpX_5e6QCRMNL8JV5hitPAM8E")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ListaVideosActivity.this, "Erro ao carregar vídeos", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONArray array = new JSONArray(json);
                            videos.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                String url = obj.getString("url");
                                String titulo = obj.getString("titulo");
                                videos.add(new ItemVideo(url, titulo));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Toast.makeText(ListaVideosActivity.this, "Erro ao processar dados", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ListaVideosActivity.this, "Erro ao buscar vídeos", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void showNotificacoes() {
        View popupView = getLayoutInflater().inflate(R.layout.layout_notificacoes, null);
        RecyclerView rvNotificacoes = popupView.findViewById(R.id.rvNotificacoes);
        rvNotificacoes.setLayoutManager(new LinearLayoutManager(this));

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        popupWindow.setElevation(10);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        ImageButton btnNotificacao = findViewById(R.id.btnNotificacao);
        popupWindow.showAsDropDown(btnNotificacao, 0, 0, Gravity.END);

        popupView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                popupWindow.dismiss();
                return true;
            }
            return false;
        });
    }

    @SuppressLint("WrongConstant")
    private void showMenuLateral() {
        DrawerLayout drawerLayout = new DrawerLayout(this);
        drawerLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        drawerLayout.setFitsSystemWindows(true);

        View dimView = new View(this);
        dimView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        dimView.setBackgroundColor(Color.parseColor("#80000000"));
        drawerLayout.addView(dimView);

        View menuView = getLayoutInflater().inflate(R.layout.layout_menu_lateral, drawerLayout, false);

        int menuWidth = (int) (getScreenWidth() * 0.75);
        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(
                menuWidth,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.START);
        menuView.setLayoutParams(params);
        drawerLayout.addView(menuView);

        // Botão 1: Cadastrar vídeo
        Button btnCadastrarVideo = menuView.findViewById(R.id.btnCadastrarVideo);
        btnCadastrarVideo.setOnClickListener(v -> {
            Intent intent = new Intent(ListaVideosActivity.this, CadastroVideo.class);
            startActivityForResult(intent, 1);
            drawerLayout.closeDrawer(Gravity.LEFT);
        });

        // Botão 2: Adicionar usuário
        Button btnAdicionarUsuario = menuView.findViewById(R.id.btnAdicionarUsuario);
        btnAdicionarUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(ListaVideosActivity.this, CriacaoDeConta.class);
            startActivity(intent);
            drawerLayout.closeDrawer(Gravity.LEFT);
        });

        // Botão 5: Sair
        Button btnSair = menuView.findViewById(R.id.btnSair);
        btnSair.setOnClickListener(v -> {
            // Exemplo: encerrar a sessão
            finishAffinity(); // fecha todas as atividades
        });

        dimView.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                dimView.setAlpha(slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
                rootView.removeView(drawerLayout);
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        rootView.addView(drawerLayout);

        drawerLayout.openDrawer(Gravity.START);
    }


    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
