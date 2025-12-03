package com.example.proyectolavacar.Clientes;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectolavacar.AdminBD;
import com.example.proyectolavacar.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateCliente extends AppCompatActivity {

    EditText txtCedula, txtNombre, txtApellidos, txtTelefono, txtCorreo;
    EditText txtLatitud, txtLongitud;
    ImageView imageViewFoto;
    Button btnTomarFoto;

    MapView mapView;
    Marker marker;

    private ActivityResultLauncher<Intent> lanzadorTomarFoto;
    private Bitmap imagenBitmap;

    private AdminBD admin;
    private SQLiteDatabase db;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String outputFile;

    private static final int REQUEST_PERMISSION_CODE = 1000;

    private String cedulaRecibida;
    private double latitudCliente, longitudCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatecliente);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar views
        txtCedula = findViewById(R.id.txtCedulaCliente);
        txtNombre = findViewById(R.id.txtNombreCliente);
        txtApellidos = findViewById(R.id.txtApellidosCliente);
        txtTelefono = findViewById(R.id.txtTelefonoCliente);
        txtCorreo = findViewById(R.id.txtCorreoCliente);
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        imageViewFoto = findViewById(R.id.imageViewFoto1);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        mapView = findViewById(R.id.mapCliente);

        txtCedula.setEnabled(false);

        // Recibir cédula
        cedulaRecibida = getIntent().getStringExtra("cedula");
        if (cedulaRecibida == null) {
            Toast.makeText(this, "Error: No se recibió cédula", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Base de datos
        admin = new AdminBD(this, "lavacar", null, 2);
        db = admin.getWritableDatabase();

        // Permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION_CODE);
        }

        // Lanzador de foto
        lanzadorTomarFoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                        Bitmap foto = (Bitmap) resultado.getData().getExtras().get("data");
                        if (foto != null) {
                            imagenBitmap = foto;
                            imageViewFoto.setImageBitmap(foto);
                        }
                    }
                }
        );

        // Setup audio
        outputFile = getExternalFilesDir(null).getAbsolutePath() + "/temp_edit.3gp";
        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();

        // Cargar cliente desde BD
        cargarCliente();

        // Preparar mapa
        prepararMapa();
    }

    private void cargarCliente() {
        SQLiteDatabase dbRead = admin.getReadableDatabase();
        Cursor fila = dbRead.rawQuery("SELECT * FROM Cliente WHERE cedula=?", new String[]{cedulaRecibida});

        if (fila.moveToFirst()) {
            txtCedula.setText(fila.getString(0));
            txtNombre.setText(fila.getString(1));
            txtApellidos.setText(fila.getString(2));
            txtTelefono.setText(fila.getString(3));
            txtCorreo.setText(fila.getString(4));

            // Foto
            byte[] fotoBytes = fila.getBlob(5);
            if (fotoBytes != null) {
                imagenBitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                imageViewFoto.setImageBitmap(imagenBitmap);
            }

            // Audio
            byte[] audioBytes = fila.getBlob(6);
            if (audioBytes != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    fos.write(audioBytes);
                    fos.close();
                } catch (IOException ignored) {}
            }

            // Lat/Lon
            latitudCliente = fila.getDouble(7);
            longitudCliente = fila.getDouble(8);
            txtLatitud.setText(String.valueOf(latitudCliente));
            txtLongitud.setText(String.valueOf(longitudCliente));
        }
        fila.close();
        dbRead.close();
    }

    public void tomarFoto(View view) {
        Intent intentTomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        lanzadorTomarFoto.launch(intentTomarFoto);
    }

    public void iniciarGrabacion(View view) {
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(outputFile);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Grabación iniciada", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void detenerGrabacion(View view) {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                Toast.makeText(this, "Grabación finalizada", Toast.LENGTH_SHORT).show();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public void iniciarReproduccion(View view) {
        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void detenerReproduccion(View view) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    private void prepararMapa() {
        GeoPoint punto = new GeoPoint(latitudCliente, longitudCliente);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(18.0);
        mapView.getController().setCenter(punto);

        marker = new Marker(mapView);
        marker.setPosition(punto);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                marker.setPosition(p);
                mapView.invalidate();
                txtLatitud.setText(String.valueOf(p.getLatitude()));
                txtLongitud.setText(String.valueOf(p.getLongitude()));
                latitudCliente = p.getLatitude();
                longitudCliente = p.getLongitude();
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        mapView.getOverlays().add(new MapEventsOverlay(mapEventsReceiver));
    }

    public void ActualizarCliente(View view) {
        admin = new AdminBD(this, "lavacar", null, 2);
        db = admin.getWritableDatabase();

        String nombre = txtNombre.getText().toString().trim();
        String apellidos = txtApellidos.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();

        if (nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues registro = new ContentValues();
        registro.put("nombre", nombre);
        registro.put("apellidos", apellidos);
        registro.put("telefono", telefono);
        registro.put("correo", correo);
        registro.put("latitud", latitudCliente);
        registro.put("longitud", longitudCliente);

        // AUDIO
        File audioFile = new File(outputFile);
        if (audioFile.exists() && audioFile.length() > 0) {
            try {
                byte[] audioData = new byte[(int) audioFile.length()];
                FileInputStream fis = new FileInputStream(audioFile);
                fis.read(audioData);
                fis.close();
                registro.put("audio", audioData);
            } catch (IOException e) { e.printStackTrace(); }
        }

        // FOTO
        if (imageViewFoto.getDrawable() instanceof BitmapDrawable) {
            Bitmap fotoAGuardar = ((BitmapDrawable) imageViewFoto.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            fotoAGuardar.compress(Bitmap.CompressFormat.PNG, 100, stream);
            registro.put("foto", stream.toByteArray());
        }

        // UPDATE
        int filas = db.update("Cliente", registro, "cedula=?", new String[]{cedulaRecibida});

        if (filas > 0) {
            Toast.makeText(this, "Cliente actualizado correctamente", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }


    public void RegresarCliente(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) mediaRecorder.release();
        if (mediaPlayer != null) mediaPlayer.release();
        if (db != null && db.isOpen()) db.close();
    }
}
