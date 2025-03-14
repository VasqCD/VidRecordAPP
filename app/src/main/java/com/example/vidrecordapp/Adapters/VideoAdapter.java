package com.example.vidrecordapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vidrecordapp.Models.VideoModel;
import com.example.vidrecordapp.R;

import java.io.File;
import java.util.List;

public class VideoAdapter extends ArrayAdapter<VideoModel> {

    private Context context;
    private List<VideoModel> videoList;
    private OnVideoClickListener listener;

    public VideoAdapter(Context context, List<VideoModel> videos) {
        super(context, 0, videos);
        this.context = context;
        this.videoList = videos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        }

        final VideoModel currentVideo = videoList.get(position);

        TextView tvNombre = listItem.findViewById(R.id.tvNombre);
        TextView tvFecha = listItem.findViewById(R.id.tvFecha);
        TextView tvDuracion = listItem.findViewById(R.id.tvDuracion);
        ImageView ivPlay = listItem.findViewById(R.id.ivPlay);

        tvNombre.setText(currentVideo.getName());
        tvFecha.setText(currentVideo.getDate());
        tvDuracion.setText(currentVideo.getDuracion());

        // Al hacer clic en el botón de reproducción, abrir el video
        ivPlay.setOnClickListener(v -> {
            File videoFile = new File(currentVideo.getPath());
            if (videoFile.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri videoUri = Uri.fromFile(videoFile);
                intent.setDataAndType(videoUri, "video/*");
                context.startActivity(intent);
            }
        });

        // Añadir listener para el clic en toda la fila
        listItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVideoClick(currentVideo);
            }
        });

        return listItem;
    }

    public interface OnVideoClickListener {
        void onVideoClick(VideoModel video);
    }

    public void setOnVideoClickListener(OnVideoClickListener listener) {
        this.listener = listener;
    }

}