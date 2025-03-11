package com.example.vidrecordapp.Config;

public class Transacciones {
    // Nombre de la base de datos
    public static final String NameDB = "VideoRecordDB";

    // Tabla de videos
    public static final String tabla_videos = "Videos";

    // Campos de la tabla videos
    public static final String id = "id";
    public static final String nombre = "nombre";
    public static final String ruta = "ruta";
    public static final String fecha = "fecha";
    public static final String duracion = "duracion";
    public static final String tamano = "tamano";
    public static final String descripcion = "descripcion";

    // DDL
    public static final String CreateTableVideos =
            "CREATE TABLE " + tabla_videos + " (" +
                    id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    nombre + " TEXT NOT NULL, " +
                    ruta + " TEXT NOT NULL, " +
                    fecha + " TEXT NOT NULL, " +
                    duracion + " TEXT, " +
                    tamano + " TEXT, " +
                    descripcion + " TEXT" +
                    ")";

    public static final String DropTableVideos =
            "DROP TABLE IF EXISTS " + tabla_videos;

    // DML
    public static final String SelectTableVideos =
            "SELECT * FROM " + tabla_videos + " ORDER BY " + fecha + " DESC";

    public static final String SelectVideoById =
            "SELECT * FROM " + tabla_videos + " WHERE " + id + " = ?";
}