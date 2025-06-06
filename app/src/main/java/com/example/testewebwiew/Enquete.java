package com.example.testewebwiew;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

public class Enquete {
    private int id;  // id da enquete
    private String titulo;
    private String tema;
    private List<String> respostas;

    // Construtor completo
    public Enquete(int id, String titulo, String tema, List<String> respostas) {
        this.id = id;
        this.titulo = titulo;
        this.tema = tema;
        this.respostas = respostas != null ? respostas : new ArrayList<>();
    }

    // Construtor simplificado (sem respostas)
    public Enquete(int id, String titulo, String tema) {
        this(id, titulo, tema, new ArrayList<>());
    }

    // Construtor sem id (id 0 por padrão)
    public Enquete(String titulo, String tema, List<String> respostas) {
        this(0, titulo, tema, respostas);
    }

    // Getters e setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getTema() {
        return tema;
    }
    public void setTema(String tema) {
        this.tema = tema;
    }
    public List<String> getRespostas() {
        return respostas;
    }
    public void setRespostas(List<String> respostas) {
        this.respostas = respostas != null ? respostas : new ArrayList<>();
    }

    public JSONArray getRespostasAsJsonArray() {
        return new JSONArray(respostas);
    }

    @Override
    public String toString() {
        return "Enquete{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", tema='" + tema + '\'' +
                ", respostas=" + respostas +
                '}';
    }
}
