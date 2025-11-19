package com.example.proyectolavacar.Servicios;


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
import com.example.proyectolavacar.MainActivity;
import com.example.proyectolavacar.R;

import java.util.ArrayList;

public class ServicioActivity extends AppCompatActivity {
    EditText txtBuscarServicio;
    ListView listViewServicios;

    ArrayAdapter<String> adapter;
    ArrayList<String> datos;
    int itemseleccionado = -1; // Posición seleccionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_servicio);

        txtBuscarServicio = findViewById(R.id.txtBuscarServicio);
        listViewServicios = findViewById(R.id.listViewServicios);

        // Inicializar lista y adaptador
        datos = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        listViewServicios.setAdapter(adapter);

        // Evento de selección en la lista
        listViewServicios.setOnItemClickListener((parent, view, position, id) -> {
            itemseleccionado = position;
            // Quita colores anteriores
            for (int i = 0; i < listViewServicios.getChildCount(); i++) {
                listViewServicios.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
            // Resalta el nuevo seleccionado
            view.setBackgroundColor(Color.LTGRAY);
        });
    }

    // Botón Agregar
    public void InsertarServicio(View view) {
        Intent intent = new Intent(this, InsertServicio.class);
        startActivity(intent);
    }

    // Botón Update
    public void UpdateServicio(View view) {
        if (itemseleccionado >= 0) {
            String item = adapter.getItem(itemseleccionado);
            String idServicio = item.split(" - ")[0]; // obtenemos el id

            Intent intent = new Intent(this, UpdateServicio.class);
            intent.putExtra("idServicio", idServicio); // pasamos el id seleccionado
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Debe seleccionar un servicio", Toast.LENGTH_SHORT).show();
        }
    }

    // Botón Eliminar
    public void EliminarServicio(View view) {
        if (itemseleccionado >= 0) {
            String item = adapter.getItem(itemseleccionado);
            String idServicio = item.split(" - ")[0]; // suponiendo formato "id - nombre"
            EliminarPorId(idServicio);

            adapter.remove(item);
            View itemresaltado = listViewServicios.getChildAt(itemseleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }
            itemseleccionado = -1;
        } else {
            Toast.makeText(getApplicationContext(), "Debe seleccionar un item", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para eliminar por id
    public void EliminarPorId(String idServicio) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        if (!idServicio.isEmpty()) {
            int registrosEliminados = db.delete("Servicio", "idServicio=?", new String[]{idServicio});
            db.close();

            if (registrosEliminados > 0) {
                Toast.makeText(getApplicationContext(), "Servicio eliminado correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "No se encontró ningún servicio con ese ID", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Falta el ID para eliminar", Toast.LENGTH_LONG).show();
        }
    }

    // Método Buscar (filtra por idServicio o nombre)
    public void BuscarServicio(View view) {
        String criterio = txtBuscarServicio.getText().toString();
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        datos.clear();
        Cursor fila = db.rawQuery("SELECT * FROM Servicio WHERE idServicio=? OR nombre=?", new String[]{criterio, criterio});
        if (fila.moveToFirst()) {
            do {
                String idServicio = fila.getString(0);
                String nombre = fila.getString(1);
                String precio = fila.getString(2);

                String item = idServicio + " - " + nombre + " (₡" + precio + ")";
                adapter.add(item);
            } while (fila.moveToNext());

            Toast.makeText(getApplicationContext(), "Servicio(s) encontrado(s)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No se encontraron registros", Toast.LENGTH_LONG).show();
        }
        fila.close();
        db.close();
    }

    // Método MostrarTodos
    public void MostrarTodos(View view) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        datos.clear();
        Cursor fila = db.rawQuery("SELECT * FROM Servicio", null);
        if (fila.moveToFirst()) {
            do {
                String idServicio = fila.getString(0);
                String nombre = fila.getString(1);
                String precio = fila.getString(2);

                String item = idServicio + " - " + nombre + " (₡" + precio + ")";
                adapter.add(item);
            } while (fila.moveToNext());

            Toast.makeText(getApplicationContext(), "Todos los servicios cargados", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No hay servicios registrados", Toast.LENGTH_LONG).show();
        }
        fila.close();
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MostrarTodos(null); // recarga la lista automáticamente
    }

    // Botón regresar
    public void regresarServicio(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

