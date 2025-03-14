package com.example.vidrecordapp;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.vidrecordapp.Config.DatabaseHelper;
import com.example.vidrecordapp.Controllers.VideoListActivity;
import com.example.vidrecordapp.Models.VideoModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int REQUEST_VIDEO_SELECT = 101;

    private VideoView videoView;
    private Button btnGrabar;
    private Button btnSalvar;

    private Uri videoUri;
    private String currentVideoPath;
    private DatabaseHelper databaseHelper;

    // Permisos necesarios
    private String[] requiredPermissions;

    // ActivityResultLauncher para la cámara
    private ActivityResultLauncher<Intent> videoCaptureLauncher;
    private ActivityResultLauncher<Intent> videoSelectLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        videoView = findViewById(R.id.videoView);
        btnGrabar = findViewById(R.id.btnGrabar);
        btnSalvar = findViewById(R.id.btnSalvar);

        // Inicializar helper de base de datos
        databaseHelper = new DatabaseHelper(this);

        // Configurar permisos según versión de Android
        setupPermissions();

        // Configurar launcher para captura de video
        videoCaptureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Video grabado con éxito
                        if (videoUri != null) {
                            // Configurar el VideoView correctamente
                            setupVideoView();
                            Toast.makeText(this, R.string.video_grabado_con_exito, Toast.LENGTH_SHORT).show();
                        }
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        // Grabación cancelada
                        Toast.makeText(this, R.string.grabacion_cancelada, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        videoSelectLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int videoId = data.getIntExtra("VIDEO_ID", -1);
                            String videoPath = data.getStringExtra("VIDEO_PATH");

                            if (videoId != -1 && videoPath != null) {
                                // Cargar el video desde la ruta
                                File videoFile = new File(videoPath);
                                if (videoFile.exists()) {
                                    // Actualizar la URI del video
                                    videoUri = FileProvider.getUriForFile(this,
                                            "com.example.vidrecordapp.fileprovider", videoFile);
                                    currentVideoPath = videoPath;

                                    // Reproducir el video
                                    setupVideoView();
                                } else {
                                    Toast.makeText(this, "El archivo de video no existe", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
        );

        // Configurar listeners de botones
        btnGrabar.setOnClickListener(v -> {
            if (checkPermissions()) {
                dispatchTakeVideoIntent();
            } else {
                requestPermissions();
            }
        });

        btnSalvar.setOnClickListener(v -> {
            if (videoUri != null) {
                saveVideoToDatabase();
            } else {
                Toast.makeText(this, R.string.no_hay_video, Toast.LENGTH_SHORT).show();
            }
        });

        Button btnVerVideos = findViewById(R.id.btnVerVideos);
        btnVerVideos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VideoListActivity.class);
            videoSelectLauncher.launch(intent);
        });
    }

    private void setupPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.RECORD_AUDIO);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            // Para Android 12 (S) y anteriores
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            // Para Android 13 y superiores
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO);
        }

        requiredPermissions = permissions.toArray(new String[0]);
    }

    private boolean checkPermissions() {
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                dispatchTakeVideoIntent();
            } else {
                Toast.makeText(this, R.string.error_permisos, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_SELECT && resultCode == RESULT_OK && data != null) {
            // Recibir el video seleccionado
            int videoId = data.getIntExtra("VIDEO_ID", -1);
            String videoPath = data.getStringExtra("VIDEO_PATH");

            if (videoId != -1 && videoPath != null) {
                // Cargar el video desde la ruta
                File videoFile = new File(videoPath);
                if (videoFile.exists()) {
                    // Actualizar la URI del video
                    videoUri = FileProvider.getUriForFile(this,
                            "com.example.vidrecordapp.fileprovider", videoFile);
                    currentVideoPath = videoPath;

                    // Reproducir el video
                    setupVideoView();
                } else {
                    Toast.makeText(this, "El archivo de video no existe", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // Verificar si hay una app de cámara disponible
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            // Mejorar la calidad del video
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // Alta calidad
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60); // Límite de 60 segundos (opcional)

            // Crear archivo para guardar el video
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error al crear archivo temporal: " + ex.getMessage());
                Toast.makeText(this, R.string.error_creando_archivo, Toast.LENGTH_SHORT).show();
                return;
            }

            // Si el archivo se creó correctamente
            if (videoFile != null) {
                try {
                    videoUri = FileProvider.getUriForFile(this,
                            "com.example.vidrecordapp.fileprovider",
                            videoFile);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                    videoCaptureLauncher.launch(takeVideoIntent);
                } catch (Exception e) {
                    Log.e(TAG, "Error al preparar URI para video: " + e.getMessage());
                    Toast.makeText(this, "Error al preparar la grabación", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, R.string.no_hay_camara, Toast.LENGTH_SHORT).show();
        }
    }

    private File createVideoFile() throws IOException {
        // Crear nombre de archivo único usando timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = "VIDEO_" + timeStamp;

        // En versiones modernas, usar el directorio de la aplicación
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File videoFile = new File(storageDir, videoFileName + ".mp4");
        currentVideoPath = videoFile.getAbsolutePath();
        return videoFile;
    }

    private void saveVideoToDatabase() {
        try {
            File videoFile = new File(currentVideoPath);
            if (!videoFile.exists()) {
                Toast.makeText(this, R.string.no_hay_video, Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener metadatos del video
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, videoUri);

            String duracion = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            // Convertir duración de milisegundos a formato "mm:ss"
            long duracionMs = Long.parseLong(duracion);
            String duracionFormateada = String.format(Locale.getDefault(),
                    "%02d:%02d",
                    (duracionMs / 1000) / 60,
                    (duracionMs / 1000) % 60);

            // Obtener tamaño del archivo en KB
            long tamanoBytes = videoFile.length();
            String tamanoFormateado = String.format(Locale.getDefault(), "%.2f KB", tamanoBytes / 1024.0);

            // Crear objeto VideoModel
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String nombre = videoFile.getName();

            VideoModel video = new VideoModel();
            video.setName(nombre);
            video.setPath(currentVideoPath);
            video.setDate(timeStamp);
            video.setDuracion(duracionFormateada);
            video.setTamano(tamanoFormateado);
            video.setDescripcion("Video grabado desde la aplicación");

            // Guardar en la base de datos
            long id = databaseHelper.guardarVideo(video);

            if (id > 0) {
                Toast.makeText(this, R.string.video_guardado_sqlite, Toast.LENGTH_SHORT).show();
                Toast.makeText(this,
                        String.format(getString(R.string.video_almacenado_en), currentVideoPath),
                        Toast.LENGTH_LONG).show();

                // Añadir el video a la galería para que sea visible
                addVideoToGallery();

                // Configurar el VideoView correctamente para mostrar el video
                setupVideoView();
            } else {
                Toast.makeText(this, R.string.error_guardando_bd, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al guardar video: " + e.getMessage());
            Toast.makeText(this, R.string.error_guardando_bd, Toast.LENGTH_SHORT).show();
        }
    }

    // Método para añadir el video a la galería
    // Método para añadir el video a la galería
    private void addVideoToGallery() {

        File videoFile = new File(currentVideoPath);
        if (!videoFile.exists()) {
            Log.e(TAG, "El archivo de video no existe");
            return;
        }

        try {
            // Para Android 10 (Q) y superior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Usar MediaStore para añadir el video a la galería
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.getName());
                values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/VidRecordApp");
                values.put(MediaStore.Video.Media.IS_PENDING, 1);

                ContentResolver resolver = getContentResolver();
                Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

                if (uri != null) {
                    try (OutputStream os = resolver.openOutputStream(uri);
                         FileInputStream fis = new FileInputStream(videoFile)) {

                        // Copiar el contenido del archivo
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }

                        // Marcar como no pendiente para que aparezca en la galería
                        values.clear();
                        values.put(MediaStore.Video.Media.IS_PENDING, 0);
                        resolver.update(uri, values, null, null);

                        Log.d(TAG, "Video copiado a la galería: " + uri);
                    }
                }
            } else {
                // Para Android 9 (Pie) y anteriores
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(videoFile);
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);

                // También usar MediaScannerConnection como respaldo
                MediaScannerConnection.scanFile(this,
                        new String[]{videoFile.getAbsolutePath()},
                        new String[]{"video/mp4"},
                        null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en addVideoToGallery: " + e.getMessage());
        }
    }

    // Método para configurar el VideoView correctamente
    private void setupVideoView() {
        if (videoUri != null) {
            try {
                // Reiniciar el VideoView
                videoView.stopPlayback();
                videoView.setVideoURI(null);

                Log.d(TAG, "Configurando VideoView con URI: " + videoUri);

                // Establecer un color de fondo para el VideoView
                videoView.setBackgroundColor(Color.BLACK);

                // Configurar el VideoView con la nueva URI
                videoView.setVideoURI(videoUri);

                // Añadir controles de reproducción
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);

                // Mostrar los controles de media inmediatamente
                mediaController.show(0);

                // Configurar listeners
                videoView.setOnPreparedListener(mp -> {
                    Log.d(TAG, "VideoView preparado, ancho: " + mp.getVideoWidth() + ", alto: " + mp.getVideoHeight());

                    // Probar diferentes modos de escalado
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);

                    // Establecer volumen
                    mp.setVolume(1.0f, 1.0f);

                    // Comenzar reproducción
                    videoView.start();
                });

                videoView.setOnInfoListener((mp, what, extra) -> {
                    Log.d(TAG, "Info VideoView: " + what);
                    return false;
                });

                videoView.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "Error en VideoView: " + what + ", " + extra);

                    // Intentar utilizar un reproductor externo como alternativa
                    if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(videoUri, "video/*");
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(TAG, "Error al abrir reproductor externo", e);
                        }
                    }

                    Toast.makeText(MainActivity.this, "Error al reproducir el video", Toast.LENGTH_SHORT).show();
                    return true;
                });

                // Forzar que el VideoView tenga foco
                videoView.requestFocus();

            } catch (Exception e) {
                Log.e(TAG, "Error en setupVideoView: " + e.getMessage(), e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Configurar el VideoView solo si hay un video y no está reproduciendo
        if (videoUri != null && !videoView.isPlaying()) {
            setupVideoView();
        }
    }
}