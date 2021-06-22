package com.example.yosigo.Persona.ActivitiesPersona;

import java.util.Date;

public class Activity {
    private String id;
    private String nombre;
    private String imagen;
    private String categoria;
    private int dias;
    private Date fecha_inicio;
    private Date fecha_fin;

    public String getImagen() { return imagen; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getId() { return id; }
    public int getDias() { return dias; }
    public Date getFecha_fin() { return fecha_fin; }
    public Date getFecha_inicio() { return fecha_inicio; }

    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setId(String id) { this.id = id; }
    public void setFecha_inicio(Date fecha_inicio) { this.fecha_inicio = fecha_inicio; }
    public void setFecha_fin(Date fecha_fin) { this.fecha_fin = fecha_fin; }
    public void setDias(int dias) { this.dias = dias; }

    @Override
    public String toString() {
        return "Activity{" +
                "id='" + id + '\n' +
                ", nombre='" + nombre + '\n' +
                ", imagen='" + imagen + '\n' +
                ", dias=" + dias + '\n' +
                ", fecha_inicio=" + fecha_inicio + '\n' +
                ", fecha_fin=" + fecha_fin +
                '}';
    }
}


