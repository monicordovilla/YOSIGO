package com.example.yosigo.Persona;

import android.net.Uri;

public class itemGrid {
    private Uri pictograma;
    private String titulo;
    private String id;

    public itemGrid(String titulo, String id, Uri pictograma){
        this.pictograma = pictograma;
        this.titulo = titulo;
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getPictograma() {
        return pictograma;
    }

    public void setPictograma(Uri pictograma) {
        this.pictograma = pictograma;
    }
}
