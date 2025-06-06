package com.example.testewebwiew;

public class ItemVideo {

    private int id;
    private String url;
    private String title;

    public ItemVideo(int id, String url, String title) {
        this.id = id;
        this.url = url;
        this.title = title;
    }

    public ItemVideo(String url, String title) {
        this.id = 0;
        this.url = url;
        this.title = title;
    }

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


    public String getTitulo() {
        return this.title;
    }
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
        return url;
    }
}
