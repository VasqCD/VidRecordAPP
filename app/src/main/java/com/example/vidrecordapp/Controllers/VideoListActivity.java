package com.example.vidrecordapp.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vidrecordapp.Adapters.VideoAdapter;
import com.example.vidrecordapp.Config.DatabaseHelper;
import com.example.vidrecordapp.Models.VideoModel;
import com.example.vidrecordapp.R;

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