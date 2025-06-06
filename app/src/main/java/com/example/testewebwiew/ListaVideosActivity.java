package com.example.testewebwiew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    private SwipeRefreshLayout swipeRefreshLayout;

    private final OkHttpClient client = new OkHttpClient();

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_videos);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_500));
        }
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        String tipo = getSharedPreferences("user_data", MODE_PRIVATE)
                .getString("tipo", "funcionario");
        if ("usuario".equalsIgnoreCase(tipo) || "admin".equalsIgnoreCase(tipo)) {
            btnMenu.setVisibility(View.VISIBLE);
        } else {
            btnMenu.setVisibility(View.GONE);
        }
        btnMenu.setOnClickListener(v -> showMenuLateral());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VideoAdapter(videos, this, this);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        gestureDetector = new GestureDetector(this, new GestureListener());

        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                boolean handled = gestureDetector.onTouchEvent(e);
                return handled;  // se true, intercepta o evento
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> carregarVideosDoBanco());

        ImageButton btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(ListaVideosActivity.this, PerfilUsuario.class);
            startActivity(intent);
        });

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filtrarVideos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarVideos(newText);
                return true;
            }
        });

        carregarVideosDoBanco();
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarActivity();
    }

    public void atualizarActivity() {
        carregarVideosDoBanco();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            atualizarActivity();
        }
    }

    @Override
    public void onVideoClick(ItemVideo video) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("videoId", video.getVideoId());
        startActivity(intent);
    }

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
                runOnUiThread(() -> {
                    Toast.makeText(ListaVideosActivity.this, "Erro ao carregar vídeos", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
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
                            adapter.setVideos(videos);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Toast.makeText(ListaVideosActivity.this, "Erro ao processar dados", Toast.LENGTH_SHORT).show();
                        } finally {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ListaVideosActivity.this, "Erro ao buscar vídeos", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }
        });
    }

    private void filtrarVideos(String texto) {
        List<ItemVideo> videosFiltrados = new ArrayList<>();
        for (ItemVideo video : videos) {
            if (video.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                videosFiltrados.add(video);
            }
        }
        adapter.setVideos(videosFiltrados);
        adapter.notifyDataSetChanged();
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

        dimView.setOnClickListener(v -> drawerLayout.closeDrawer(Gravity.START));

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

        Button btnCadastrarVideo = menuView.findViewById(R.id.btnCadastrarVideo);
        Button btnAdicionarUsuario = menuView.findViewById(R.id.btnAdicionarUsuario);
        Button btnExcluirUsuario = menuView.findViewById(R.id.btnExcluirUsuario);
        Button btnCriarEnquete = menuView.findViewById(R.id.btnCriarEnquete);
        Button btnExcluirVideo = menuView.findViewById(R.id.btnExcluirVideo);
        Button btnSair = menuView.findViewById(R.id.btnSair);

        btnCadastrarVideo.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(ListaVideosActivity.this, CadastroVideo.class);
            startActivity(intent);
        });

        btnAdicionarUsuario.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(ListaVideosActivity.this, CriacaoDeConta.class);
            startActivity(intent);
        });

        btnExcluirUsuario.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(ListaVideosActivity.this, ExclusaoDeUsuarioActivity.class);
            startActivity(intent);
        });

        btnCriarEnquete.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(ListaVideosActivity.this, CriacaoDeEnqueteActivity.class);
            startActivity(intent);
        });

        btnExcluirVideo.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            Intent intent = new Intent(ListaVideosActivity.this, ListaExcluirVideoActivity.class);
            startActivity(intent);
        });

        btnSair.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.START);
            finishAffinity();
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

    // GestureListener para detectar swipe
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX < 0) {
                        // Swipe da direita para a esquerda
                        Intent intent = new Intent(ListaVideosActivity.this, ListaEnquetesActivity.class);
                        startActivity(intent);
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
