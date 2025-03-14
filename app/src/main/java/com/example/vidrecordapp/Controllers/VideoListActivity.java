package com.example.vidrecordapp.Controllers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.vidrecordapp.Adapters.VideoAdapter;
import com.example.vidrecordapp.Config.DatabaseHelper;
import com.example.vidrecordapp.Models.VideoModel;
import com.example.vidrecordapp.R;

import java.io.File;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    private ListView listViewVideos;
    private DatabaseHelper databaseHelper;
    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        listViewVideos = findViewById(R.id.listViewVideos);
        databaseHelper = new DatabaseHelper(this);

        loadVideos();

        // Configurar el listener para clics en videos
        adapter.setOnVideoClickListener(video -> {
            // Crear un intent para volver a MainActivity con el video seleccionado
            Intent intent = new Intent();
            intent.putExtra("VIDEO_ID", video.getId());
            intent.putExtra("VIDEO_PATH", video.getPath());

            // Agregar la URI correctamente formateada
            try {
                File videoFile = new File(video.getPath());
                if (videoFile.exists()) {
                    Uri videoUri = FileProvider.getUriForFile(this,
                            "com.example.vidrecordapp.fileprovider", videoFile);
                    intent.putExtra("VIDEO_URI", videoUri.toString());
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            } catch (Exception e) {
                Log.e("VideoListActivity", "Error al crear URI: " + e.getMessage());
            }

            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void loadVideos() {
        List<VideoModel> videoList = databaseHelper.obtenerVideos();
        adapter = new VideoAdapter(this, videoList);
        listViewVideos.setAdapter(adapter);

        // Mostrar mensaje si no hay videos
        TextView tvNoVideos = findViewById(R.id.tvNoVideos);
        if (videoList.isEmpty()) {
            tvNoVideos.setVisibility(View.VISIBLE);
            listViewVideos.setVisibility(View.GONE);
        } else {
            tvNoVideos.setVisibility(View.GONE);
            listViewVideos.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar los videos al volver a la actividad
        loadVideos();
    }
}