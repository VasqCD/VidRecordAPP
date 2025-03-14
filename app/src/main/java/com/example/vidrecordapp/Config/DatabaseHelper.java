package com.example.vidrecordapp.Config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.vidrecordapp.Models.VideoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private SQLiteConexion conexion;
    private Context context;

    public DatabaseHelper(Context context) {
        this.context = context;
        conexion = new SQLiteConexion(context, Transacciones.NameDB, null, 1);
    }

    // Guardar un video en la base de datos
    public long guardarVideo(VideoModel video) {
        long id = -1;
        SQLiteDatabase db = conexion.getWritableDatabase();

        try {
            ContentValues valores = new ContentValues();
            valores.put(Transacciones.nombre, video.getName());
            valores.put(Transacciones.ruta, video.getPath());
            valores.put(Transacciones.fecha, video.getDate());
            valores.put(Transacciones.duracion, video.getDuracion());
            valores.put(Transacciones.tamano, video.getTamano());
            valores.put(Transacciones.descripcion, video.getDescripcion());

            id = db.insert(Transacciones.tabla_videos, null, valores);
        } catch (Exception e) {
            Log.e(TAG, "Error al guardar video: " + e.getMessage());
        } finally {
            db.close();
        }

        return id;
    }

    // Obtener todos los videos
    public List<VideoModel> obtenerVideos() {
        List<VideoModel> listaVideos = new ArrayList<>();
        SQLiteDatabase db = conexion.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(Transacciones.SelectTableVideos, null);

            while(cursor.moveToNext()) {
                VideoModel video = new VideoModel();
                video.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Transacciones.id)));
                video.setName(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nombre)));
                video.setPath(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.ruta)));
                video.setDate(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.fecha)));
                video.setDuracion(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.duracion)));
                video.setTamano(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.tamano)));
                video.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.descripcion)));

                listaVideos.add(video);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener videos: " + e.getMessage());
        } finally {
            db.close();
        }

        return listaVideos;
    }

    // Eliminar un video
    public boolean eliminarVideo(int idVideo) {
        SQLiteDatabase db = conexion.getWritableDatabase();
        int filasEliminadas = 0;

        try {
            String[] args = {String.valueOf(idVideo)};
            filasEliminadas = db.delete(Transacciones.tabla_videos, Transacciones.id + "=?", args);
        } catch (Exception e) {
            Log.e(TAG, "Error al eliminar video: " + e.getMessage());
        } finally {
            db.close();
        }

        return filasEliminadas > 0;
    }

    // Obtener un video por ID
    public VideoModel obtenerVideoPorId(int idVideo) {
        VideoModel video = null;
        SQLiteDatabase db = conexion.getReadableDatabase();

        try {
            String[] args = {String.valueOf(idVideo)};
            Cursor cursor = db.rawQuery(Transacciones.SelectVideoById, args);

            if (cursor.moveToFirst()) {
                video = new VideoModel();
                video.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Transacciones.id)));
                video.setName(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.nombre)));
                video.setPath(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.ruta)));
                video.setDate(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.fecha)));
                video.setDuracion(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.duracion)));
                video.setTamano(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.tamano)));
                video.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(Transacciones.descripcion)));
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error al obtener video: " + e.getMessage());
        } finally {
            db.close();
        }

        return video;
    }
}