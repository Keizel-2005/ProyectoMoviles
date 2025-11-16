package com.example.proyectolavacar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ServicioActivity extends AppCompatActivity {
    EditText txtIdServicio, txtNombreServicio, txtPrecioServicio;
    ListView listServicios;
    ImageButton  btnAgregarServicio, btnEditarServicio, btnEliminarServicio, btnRegresarServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_servicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtIdServicio = findViewById(R.id.txtIdServicio);
        txtNombreServicio = findViewById(R.id.txtNombreServicio);
        txtPrecioServicio = findViewById(R.id.txtPrecioServicio);

        btnAgregarServicio = findViewById(R.id.btnAgregarServicio); btnEditarServicio = findViewById(R.id.btnEditarServicio); btnEliminarServicio = findViewById(R.id.btnEliminarServicio);
        btnRegresarServicio = findViewById(R.id.btnRegresarServicio);
    }

}