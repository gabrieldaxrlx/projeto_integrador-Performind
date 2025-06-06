package com.example.testewebwiew;

public class Resposta {
    private int id;
    private int usuario_id;
    private int enquete_id;
    private String resp_txt;

    public Resposta(int id, int usuario_id, int enquete_id, String resp_txt) {
        this.id = id;
        this.usuario_id = usuario_id;
        this.enquete_id = enquete_id;
        this.resp_txt = resp_txt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public int getEnquete_id() {
        return enquete_id;
    }

    public String getResp_txt() {
        return resp_txt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public void setEnquete_id(int enquete_id) {
        this.enquete_id = enquete_id;
    }

    public void setResp_txt(String resp_txt) {
        this.resp_txt = resp_txt;
    }

    @Override
    public String toString() {
        return "Resposta{" +
                "id=" + id +
                ", usuario_id=" + usuario_id +
                ", enquete_id=" + enquete_id +
                ", resp_txt='" + resp_txt + '\'' +
                '}';
    }
}
