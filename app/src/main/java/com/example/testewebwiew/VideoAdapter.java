package com.example.testewebwiew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<ItemVideo> videos;
    private OnVideoClickListener listener;
    private Context context;

    public interface OnVideoClickListener {
        void onVideoClick(ItemVideo video);
    }

    public VideoAdapter(List<ItemVideo> videos, OnVideoClickListener listener, Context context) {
        this.videos = videos;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        ItemVideo video = videos.get(position);
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitulo;
        private ImageView imgThumbnail;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onVideoClick(videos.get(position));
                }
            });
        }

        public void bind(ItemVideo video) {
            txtTitulo.setText(video.getTitle());

            // Carrega a thumbnail usando Glide
            String videoId = extractYouTubeId(video.getUrl());
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";

            Glide.with(context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.placeholder) // Adicione um placeholder no seu drawable
                    .error(R.drawable.error) // Adicione uma imagem de erro no seu drawable
                    .into(imgThumbnail);
        }

        private String extractYouTubeId(String url) {
            // Extrai o ID do vídeo da URL do YouTube
            String videoId = "";
            if (url != null && url.trim().length() > 0) {
                if (url.contains("youtu.be/")) {
                    videoId = url.substring(url.lastIndexOf("/") + 1);
                } else if (url.contains("v=")) {
                    videoId = url.substring(url.indexOf("v=") + 2);
                    if (videoId.contains("&")) {
                        videoId = videoId.substring(0, videoId.indexOf("&"));
                    }
                } else if (url.contains("embed/")) {
                    videoId = url.substring(url.indexOf("embed/") + 6);
                }
            }
            return videoId;
        }
    }
}