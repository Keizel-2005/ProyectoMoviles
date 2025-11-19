package com.example.proyectolavacar.Factura;

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

public class FacturaActivity extends AppCompatActivity {

    EditText txtBuscarFactura;
    ListView listViewFacturas;

    ArrayAdapter<String> adapter;
    ArrayList<String> datos;
    int itemseleccionado = -1; // posición seleccionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_factura);

        txtBuscarFactura = findViewById(R.id.txtBuscarFactura);
        listViewFacturas = findViewById(R.id.listViewFacturas);

        datos = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        listViewFacturas.setAdapter(adapter);

        listViewFacturas.setOnItemClickListener((parent, view, position, id) -> {
            itemseleccionado = position;

            // Quita colores anteriores (igual que en tus otros Activities)
            for (int i = 0; i < listViewFacturas.getChildCount(); i++) {
                listViewFacturas.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
            // Resalta el nuevo seleccionado
            view.setBackgroundColor(Color.LTGRAY);
        });
    }

    // Botón Agregar - abrir InsertFactura
    public void Insertar(View view) {
        Intent intent = new Intent(this, InsertFactura.class);
        startActivity(intent);
    }


    // Botón Eliminar
    public void Eliminar(View view) {
        if (itemseleccionado >= 0) {
            String item = adapter.getItem(itemseleccionado);
            String idFactura = item.split(" - ")[0];

            EliminarPorId(idFactura);

            adapter.remove(item);

            // restablecer color del item resaltado (si está visible)
            View itemresaltado = listViewFacturas.getChildAt(itemseleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }
            itemseleccionado = -1;
        } else {
            Toast.makeText(getApplicationContext(), "Debe seleccionar un item", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para eliminar por idFactura
    public void EliminarPorId(String idFactura) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        if (!idFactura.isEmpty()) {
            int registrosEliminados = db.delete("EncabezadoFactura", "idFactura=?", new String[]{idFactura});
            db.close();

            if (registrosEliminados > 0) {
                Toast.makeText(getApplicationContext(), "Factura eliminada correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "No se encontró ninguna factura con ese id", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Falta el id para eliminar", Toast.LENGTH_LONG).show();
        }
    }

    // Botón Buscar (por idFactura)
    public void Buscar(View view) {
        String criterio = txtBuscarFactura.getText().toString();
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        datos.clear();
        Cursor fila = db.rawQuery("SELECT * FROM EncabezadoFactura WHERE idFactura=?", new String[]{criterio});
        if (fila.moveToFirst()) {
            do {
                String id = fila.getString(0);        // idFactura
                String fecha = fila.getString(1);    // fecha
                String cedulaCli = fila.getString(2); // cedulaCliente
                String placa = fila.getString(3);    // placaCarro
                String total = fila.getString(5);    // total

                String item = id + " - " + fecha + " - " + cedulaCli + " - " + placa + " (" + total + ")";
                adapter.add(item);
            } while (fila.moveToNext());

            Toast.makeText(getApplicationContext(), "Factura(s) encontrado(s)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No se encontraron registros", Toast.LENGTH_LONG).show();
        }
        fila.close();
        db.close();
    }

    // Mostrar todos los encabezados
    public void MostrarTodos(View view) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        datos.clear();
        Cursor fila = db.rawQuery("SELECT * FROM EncabezadoFactura ORDER BY fecha DESC", null);
        if (fila.moveToFirst()) {
            do {
                String id = fila.getString(0);        // idFactura
                String fecha = fila.getString(1);    // fecha
                String cedulaCli = fila.getString(2); // cedulaCliente
                String placa = fila.getString(3);    // placaCarro
                String total = fila.getString(5);    // total

                String item = id + " - " + fecha + " - " + cedulaCli + " - " + placa + " (" + total + ")";
                adapter.add(item);
            } while (fila.moveToNext());

            Toast.makeText(getApplicationContext(), "Todas las facturas cargadas", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No hay facturas registradas", Toast.LENGTH_LONG).show();
        }
        fila.close();
        db.close();
    }
    public void MostrarDetalleFactura(View view) {
        if (itemseleccionado >= 0) {
            String item = adapter.getItem(itemseleccionado);
            String idFactura = item.split(" - ")[0];

            Intent intent = new Intent(this, DetalleFacturaActivity.class);
            intent.putExtra("idFactura", idFactura);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Debe seleccionar una factura", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MostrarTodos(null); // recarga la lista automáticamente
    }

    // Botón regresar
    public void regresar(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}