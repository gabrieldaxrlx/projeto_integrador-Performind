package com.example.testewebwiew;

public class ItemVideo {
    private String url;
    private String title;

    public ItemVideo(String url, String title) {
        this.url = url;
        this.title = title;
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
    public String getVideoId() {
        // Extrai o ID do vídeo da URL
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

