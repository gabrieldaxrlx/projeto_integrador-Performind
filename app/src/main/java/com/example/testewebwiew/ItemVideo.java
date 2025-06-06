package com.example.testewebwiew;

public class ItemVideo {

    private int id;           // ID da tabela no Supabase (chave primária)
    private String url;       // URL completa do vídeo no YouTube
    private String title;     // Título do vídeo

    // Construtor principal (com id)
    public ItemVideo(int id, String url, String title) {
        this.id = id;
        this.url = url;
        this.title = title;
    }

    // Construtor secundário (sem id, id inicializado como 0)
    public ItemVideo(String url, String title) {
        this.id = 0;
        this.url = url;
        this.title = title;
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    /**
     * Método que retorna o título do vídeo em português (mesmo que getTitle)
     */
    public String getTitulo() {
        return this.title;
    }

    /**
     * Extrai o ID do vídeo do YouTube da URL.
     * Suporta URLs do tipo:
     * - https://youtu.be/{videoId}
     * - https://www.youtube.com/watch?v={videoId}
     * - https://www.youtube.com/embed/{videoId}
     *
     * @return String com o videoId do YouTube
     */
    public String getVideoId() {
        if (url == null || url.isEmpty()) return "";

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

        // Se a URL não tiver nenhum dos formatos esperados, retorna a própria url (ou pode retornar vazio)
        return url;
    }
}
