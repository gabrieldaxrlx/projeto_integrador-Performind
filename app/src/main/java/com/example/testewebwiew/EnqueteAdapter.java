package com.example.testewebwiew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EnqueteAdapter extends RecyclerView.Adapter<EnqueteAdapter.EnqueteViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Enquete enquete);
    }

    private final List<Enquete> enquetes;
    private final OnItemClickListener listener;

    public EnqueteAdapter(List<Enquete> enquetes, OnItemClickListener listener) {
        this.enquetes = enquetes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EnqueteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_enquete, parent, false);
        return new EnqueteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnqueteViewHolder holder, int position) {
        Enquete enquete = enquetes.get(position);
        holder.titulo.setText(enquete.getTitulo());
        holder.tema.setText(enquete.getTema());

        holder.btnResponder.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(enquete);
            }
        });
    }

    @Override
    public int getItemCount() {
        return enquetes.size();
    }

    static class EnqueteViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, tema;
        Button btnResponder;

        public EnqueteViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.Titulo);
            tema = itemView.findViewById(R.id.Tema);
            btnResponder = itemView.findViewById(R.id.btnResponder);
        }
    }
}
