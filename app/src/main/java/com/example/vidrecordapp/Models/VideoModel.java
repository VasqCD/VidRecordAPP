package com.example.vidrecordapp.Models;

public class VideoModel {

    private int id;
    private String name;
    private String path;
    private String date;
    private String duracion;
    private String tamano;
    private String descripcion;

    public VideoModel() {
    }

    public VideoModel(int id, String name, String path, String date, String duracion, String tamano, String descripcion) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.date = date;
        this.duracion = duracion;
        this.tamano = tamano;
        this.descripcion = descripcion;
    }

    public VideoModel(String name, String path, String date, String duracion, String tamano, String descripcion) {
        this.name = name;
        this.path = path;
        this.date = date;
        this.duracion = duracion;
        this.tamano = tamano;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
