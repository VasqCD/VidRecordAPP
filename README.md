# VidRecordAPP

Una aplicación Android que permite grabar videos desde la cámara del dispositivo y almacenarlos localmente, con soporte para guardado de metadatos en SQLite.

## Características

- 📹 Grabación de videos utilizando la cámara del dispositivo
- 💾 Almacenamiento de videos en el sistema de archivos
- 🗃️ Registro de metadatos de videos en base de datos SQLite
- 🎬 Previsualización de videos grabados

## Requisitos

- Dispositivo Android con SDK 24 (Android 7.0 Nougat) o superior
- Permisos de cámara, micrófono y almacenamiento
- Mínimo 50MB de espacio de almacenamiento libre

## Tecnologías utilizadas

- Java para Android
- SQLite para almacenamiento de datos
- Android Media API para grabación de video
- FileProvider para gestión de archivos

## Permisos requeridos

La aplicación requiere los siguientes permisos:

- `CAMERA`: Para acceder a la cámara del dispositivo
- `RECORD_AUDIO`: Para grabar audio durante la captura de video
- `WRITE_EXTERNAL_STORAGE` (para Android < 13)
- `READ_EXTERNAL_STORAGE` (para Android < 13)
- `READ_MEDIA_VIDEO` (para Android 13+)

## Estructura del proyecto

```
com.example.vidrecordapp/
├── MainActivity.java          # Actividad principal para UI y lógica de grabación
├── DatabaseHelper.java        # Clase auxiliar para manejo de SQLite
├── VideoModel.java            # Modelo de datos para representar videos
└── res/
    ├── layout/
    │   └── activity_main.xml  # Interfaz de usuario principal
    ├── xml/
    │   └── file_paths.xml     # Configuración de rutas para FileProvider
    └── ...
```

## Instalación

1. Clone este repositorio:
   ```
   git clone https://github.com/yourusername/VidRecordAPP.git
   ```

2. Abra el proyecto en Android Studio

3. Sincronice el proyecto con los archivos Gradle

4. Ejecute la aplicación en un dispositivo o emulador

## Uso

1. Inicie la aplicación
2. Presione el botón "Grabar video" para iniciar la grabación
3. Use la aplicación de cámara del sistema para grabar su video
4. Después de grabar, verá la previsualización del video en la aplicación
5. Presione "Salvar" para guardar el video y sus metadatos en la base de datos SQLite

## Implementación de SQLite

La aplicación utiliza SQLite para almacenar los siguientes datos de cada video:
- ID (autogenerado)
- Ruta de archivo del video
- Nombre del archivo
- Fecha y hora de grabación

La tabla se crea con la siguiente estructura:

```sql
CREATE TABLE videos (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    path TEXT NOT NULL,
    name TEXT NOT NULL,
    date TEXT NOT NULL
);
```

## Mejoras futuras

- Mejorar la interfaz de usuario con controles de reproducción

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulte el archivo LICENSE para más detalles.
