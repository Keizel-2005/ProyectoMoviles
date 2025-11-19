package com.example.proyectolavacar.Carros;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectolavacar.AdminBD;
import com.example.proyectolavacar.Carros.InsertCarro;
import com.example.proyectolavacar.Carros.UpdateCarro;
import com.example.proyectolavacar.MainActivity;
import com.example.proyectolavacar.R;

import java.util.ArrayList;

public class CarroActivity extends AppCompatActivity {

    EditText txtBuscarCarro;
    ListView listViewCarros;

    ArrayAdapter<String> adapter;
    ArrayList<String> datos;
    int itemseleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carro);

        txtBuscarCarro = findViewById(R.id.txtBuscarCarro);
        listViewCarros = findViewById(R.id.listViewCarros);

        // Inicializar lista y adaptador
        datos = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        listViewCarros.setAdapter(adapter);

        // Evento de selección
        listViewCarros.setOnItemClickListener((parent, view, position, id) -> {
            itemseleccionado = position;

            // Quitar colores anteriores
            for (int i = 0; i < listViewCarros.getChildCount(); i++) {
                listViewCarros.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }

            // Resaltar seleccionado
            view.setBackgroundColor(Color.LTGRAY);
        });
    }


    public void Insertar(View view) {
        Intent intent = new Intent(this, InsertCarro.class);
        startActivity(intent);
    }


    public void Update(View view) {
        if (itemseleccionado >= 0) {
            String item = adapter.getItem(itemseleccionado);
            String placa = item.split(" - ")[0]; // obtenemos la placa

            Intent intent = new Intent(this, UpdateCarro.class);
            intent.putExtra("placa", placa);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Debe seleccionar un carro", Toast.LENGTH_SHORT).show();
        }
    }


    public void Eliminar(View view) {
        if (itemseleccionado >= 0) {
            String item = adapter.getItem(itemseleccionado);
            String placa = item.split(" - ")[0];

            EliminarPorPlaca(placa);

            adapter.remove(item);

            View itemresaltado = listViewCarros.getChildAt(itemseleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }

            itemseleccionado = -1;

        } else {
            Toast.makeText(getApplicationContext(), "Debe seleccionar un carro", Toast.LENGTH_SHORT).show();
        }
    }

    public void EliminarPorPlaca(String placa) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        if (!placa.isEmpty()) {
            int registrosEliminados = db.delete("Carro", "placa=?", new String[]{placa});
            db.close();

            if (registrosEliminados > 0) {
                Toast.makeText(getApplicationContext(), "Carro eliminado correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "No se encontró ningún carro con esa placa", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Falta la placa para eliminar", Toast.LENGTH_LONG).show();
        }
    }

    public void Buscar(View view) {
        String criterio = txtBuscarCarro.getText().toString();
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        datos.clear();
        Cursor fila = db.rawQuery("SELECT * FROM Carro WHERE placa=?", new String[]{criterio});
        if (fila.moveToFirst()) {
            do {
                String placa = fila.getString(0);
                String marca = fila.getString(1);
                String modelo = fila.getString(2);
                String color = fila.getString(3);

                String item = placa + " - " + marca + " " + modelo + " (" + color + ")";
                adapter.add(item);

            } while (fila.moveToNext());

            Toast.makeText(getApplicationContext(), "Carro(s) encontrado(s)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No se encontraron registros", Toast.LENGTH_LONG).show();
        }

        fila.close();
        db.close();
    }

    public void MostrarTodos(View view) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        datos.clear();
        Cursor fila = db.rawQuery("SELECT * FROM Carro", null);

        if (fila.moveToFirst()) {
            do {
                String placa = fila.getString(0);
                String marca = fila.getString(1);
                String modelo = fila.getString(2);
                String color = fila.getString(3);

                String item = placa + " - " + marca + " " + modelo + " (" + color + ")";
                adapter.add(item);

            } while (fila.moveToNext());

            Toast.makeText(getApplicationContext(), "Todos los carros cargados", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No hay carros registrados", Toast.LENGTH_LONG).show();
        }

        fila.close();
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MostrarTodos(null);
    }

    public void regresar(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
