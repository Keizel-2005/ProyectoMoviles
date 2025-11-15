package com.example.proyectolavacar;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CarroActivity extends AppCompatActivity {
    EditText txtPlaca, txtModelo, txtAno;
    ListView listCarros;
    ImageButton btnAgregar, btnEditar, btnEliminar, btnBuscar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtPlaca = findViewById(R.id.txtPlaca); txtModelo = findViewById(R.id.txtModelo); txtAno = findViewById(R.id.txtAno);
        listCarros = findViewById(R.id.listCarros); btnAgregar = findViewById(R.id.btnAgregar); btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar); btnBuscar = findViewById(R.id.btnBuscar);
    }
}