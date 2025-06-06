package com.example.testewebwiew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> listaUsuarios;
    private Context context;
    private OnExcluirClickListener listener;

    public interface OnExcluirClickListener {
        void onExcluirClick(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> listaUsuarios, Context context, OnExcluirClickListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);
        holder.txtNome.setText(usuario.nome);
        holder.txtMatricula.setText("Matrícula: " + usuario.matricula);

        holder.btnExcluir.setOnClickListener(v -> {
            listener.onExcluirClick(usuario);
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome, txtMatricula;
        Button btnExcluir;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtMatricula = itemView.findViewById(R.id.txtMatricula);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}

