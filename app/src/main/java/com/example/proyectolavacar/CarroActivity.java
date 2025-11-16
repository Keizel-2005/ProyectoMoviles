package com.example.proyectolavacar;

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

public class CarroActivity extends AppCompatActivity {

    EditText txtPlaca, txtModelo, txtAno;
    ListView listCarros;
    ImageButton btnAgregar, btnEditar, btnEliminar, btnBuscar, btnRegresar;

    ArrayList<String> placas = new ArrayList<>();
    ArrayList<String> modelos = new ArrayList<>();
    ArrayList<String> anos = new ArrayList<>();

    CarroAdapter adapter;

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

        listCarros = findViewById(R.id.listCarros);

        btnAgregar = findViewById(R.id.btnAgregar); btnEditar = findViewById(R.id.btnEditar); btnEliminar = findViewById(R.id.btnEliminar);
        btnBuscar = findViewById(R.id.btnBuscar); btnRegresar = findViewById(R.id.btnRegresar);

        adapter = new CarroAdapter(this, placas, modelos, anos);
        listCarros.setAdapter(adapter);

        btnAgregar.setOnClickListener(v -> {
            String placa = txtPlaca.getText().toString().trim();
            String modelo = txtModelo.getText().toString().trim();
            String ano = txtAno.getText().toString().trim();

            if (placa.isEmpty() || modelo.isEmpty() || ano.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (placas.contains(placa)) {
                Toast.makeText(this, "La placa ya existe", Toast.LENGTH_SHORT).show();
                return;
            }

            placas.add(placa); //Añadir los campos
            modelos.add(modelo);
            anos.add(ano);

            adapter.notifyDataSetChanged();

            txtPlaca.setText("");//limpiar los campos
            txtModelo.setText("");
            txtAno.setText("");

            Toast.makeText(this, "Carro agregado", Toast.LENGTH_SHORT).show();
        });

        btnRegresar.setOnClickListener(v -> finish());
    }
}
