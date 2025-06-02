package com.example.testewebwiew;

public class ItemVideo {
    private String id;    // ID do banco Supabase (chave primária)
    private String url;
    private String title;

    // Construtor principal com id, url e title
    public ItemVideo(String id, String url, String title) {
        this.id = id;
        this.url = url;
        this.title = title;
    }

    // Construtor extra com url e title (id vazio por padrão)
    public ItemVideo(String url, String title) {
        this.id = "";
        this.url = url;
        this.title = title;
    }

    // Getter e Setter para o ID do banco
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter e Setter para URL e Title (como você já tem)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Extrai o ID do vídeo do YouTube da URL
    public String getVideoId() {
        if (url.contains("youtu.be/")) {
            return url.substring(url.lastIndexOf("/") + 1);
        } else if (url.contains("v=")) {
            String videoId = url.substring(url.indexOf("v=") + 2);
            if (videoId.contains("&")) {
                videoId = videoId.substring(0, videoId.indexOf("&"));
            }
            return videoId;
        } else if (url.contains("embed/")) {
            return url.substring(url.indexOf("embed/") + 6);
        }
        return url; // Se não for uma URL, assume que já é o ID
    }
}
