package com.example.proyectolavacar.Clientes;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.proyectolavacar.Clientes.ClienteActivity;
import com.example.proyectolavacar.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.events.MapEventsReceiver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class InsertCliente extends AppCompatActivity {

    EditText txtCedula, txtNombre, txtApellidos, txtTelefono, txtCorreo;
    EditText txtLatitud, txtLongitud;
    ImageView imageViewFoto;
    Button btnTomarFoto, btnIniciarGrabacion, btnDetenerGrabacion, btnIniciarReproduccion, btnDetenerReproduccion;

    // ★★★ MAPA ★★★
    MapView mapView;
    Marker markerSeleccionado;

    private ActivityResultLauncher<Intent> lanzadorTomarFoto;
    private Bitmap imagenBitmap;

    private AdminBD admin;
    private SQLiteDatabase db;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String outputFile;

    private static final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Config Osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_insertcliente);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCedula = findViewById(R.id.txtCedulaCliente);
        txtNombre = findViewById(R.id.txtNombreCliente);
        txtApellidos = findViewById(R.id.txtApellidosCliente);
        txtTelefono = findViewById(R.id.txtTelefonoCliente);
        txtCorreo = findViewById(R.id.txtCorreoCliente);
        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);

        imageViewFoto = findViewById(R.id.imageViewFoto1);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnIniciarGrabacion = findViewById(R.id.btnIniciarGrabacion);
        btnDetenerGrabacion = findViewById(R.id.btnDetenerGrabacion);
        btnIniciarReproduccion = findViewById(R.id.btnIniciarReproduccion);
        btnDetenerReproduccion = findViewById(R.id.btnDetenerReproduccion);

        admin = new AdminBD(this, "lavacar", null, 2);
        db = admin.getWritableDatabase();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        }

        lanzadorTomarFoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                        imagenBitmap = (Bitmap) resultado.getData().getExtras().get("data");
                        imageViewFoto.setImageBitmap(imagenBitmap);
                    }
                }
        );

        // Configurar grabación de audio
        outputFile = getExternalFilesDir(null).getAbsolutePath() + "/Grabacion.3gp";
        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();

        mapView = findViewById(R.id.mapCliente);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.0);

        GeoPoint startPoint = new GeoPoint(9.935, -84.091);
        mapView.getController().setCenter(startPoint);

        // Evento de toque en el mapa
        MapEventsReceiver receiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if (markerSeleccionado != null) {
                    mapView.getOverlays().remove(markerSeleccionado);
                }
                markerSeleccionado = new Marker(mapView);
                markerSeleccionado.setPosition(p);
                markerSeleccionado.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                markerSeleccionado.setTitle("Ubicación seleccionada");
                mapView.getOverlays().add(markerSeleccionado);
                mapView.invalidate();

                txtLatitud.setText(String.valueOf(p.getLatitude()));
                txtLongitud.setText(String.valueOf(p.getLongitude()));
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        MapEventsOverlay overlay = new MapEventsOverlay(receiver);
        mapView.getOverlays().add(overlay);
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
            Toast.makeText(this, "La grabación comenzó", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void detenerGrabacion(View view) {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                Toast.makeText(this, "El audio se grabó con éxito", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Reproducción de audio", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void detenerReproduccion(View view) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            Toast.makeText(this, "Reproducción de audio detenida", Toast.LENGTH_SHORT).show();
        }
    }

    public void GuardarCliente(View view) {

        String cedula = txtCedula.getText().toString().trim();
        String nombre = txtNombre.getText().toString().trim();
        String apellidos = txtApellidos.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String latitudStr = txtLatitud.getText().toString().trim();
        String longitudStr = txtLongitud.getText().toString().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Debe llenar al menos cédula, nombre, apellidos, teléfono y correo", Toast.LENGTH_LONG).show();
            return;
        }

        if (latitudStr.isEmpty() || longitudStr.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar la ubicación en el mapa", Toast.LENGTH_LONG).show();
            return;
        }

        double latitud = Double.parseDouble(latitudStr);
        double longitud = Double.parseDouble(longitudStr);

        ContentValues registro = new ContentValues();
        registro.put("cedula", cedula);
        registro.put("nombre", nombre);
        registro.put("apellidos", apellidos);
        registro.put("telefono", telefono);
        registro.put("correo", correo);
        registro.put("latitud", latitud);
        registro.put("longitud", longitud);

        if (imagenBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            registro.put("foto", stream.toByteArray());
        }

        File audioFile = new File(outputFile);
        if (audioFile.exists() && audioFile.length() > 0) {
            byte[] audioData = new byte[(int) audioFile.length()];
            try {
                FileInputStream fileInputStream = new FileInputStream(audioFile);
                fileInputStream.read(audioData);
                fileInputStream.close();
                registro.put("audio", audioData);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al leer el archivo de audio", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        long resultado = db.insert("Cliente", null, registro);
        if (resultado != -1) {
            Toast.makeText(this, "Cliente registrado correctamente", Toast.LENGTH_LONG).show();
            txtCedula.setText("");
            txtNombre.setText("");
            txtApellidos.setText("");
            txtTelefono.setText("");
            txtCorreo.setText("");
            txtLatitud.setText("");
            txtLongitud.setText("");
            imageViewFoto.setImageResource(android.R.drawable.ic_menu_camera);
            audioFile.delete();
        } else {
            Toast.makeText(this, "Error al registrar cliente", Toast.LENGTH_LONG).show();
        }
    }

    public void RegresarListCliente(View view) {
        Intent intent = new Intent(this, ClienteActivity.class);
        startActivity(intent);
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
