package com.financialeducation.model;

public class Video {
    private int id;
    private String titulo;
    private String url;
    private int index; // Novo atributo para facilitar a navegação

    public Video() {
    }
    
    public Video(int id, String titulo, String url, int index) {
        this.id = id;
        this.titulo = titulo;
        this.url = url;
        this.index = index;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getUrl() { return url; }
    public int getIndex() { return index; }

    public void setId(int id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setUrl(String url) { this.url = url; }
    public void setIndex(int index) { this.index = index; }
}
