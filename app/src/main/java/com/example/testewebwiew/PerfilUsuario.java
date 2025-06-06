package com.example.testewebwiew;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        TextView txtNome = findViewById(R.id.txtNome);
        TextView txtMatricula = findViewById(R.id.txtMatricula);
        TextView txtTipoConta = findViewById(R.id.txtTipoConta);

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String nome = prefs.getString("nome", "Nome não disponível");
        String matricula = prefs.getString("matricula", "N/A");
        String tipo = prefs.getString("tipo", "N/A");

        txtNome.setText(nome);
        txtMatricula.setText("Matrícula: " + matricula);
        txtTipoConta.setText("Tipo: " + tipo);
    }
}
