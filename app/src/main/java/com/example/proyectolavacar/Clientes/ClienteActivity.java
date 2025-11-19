package com.example.proyectolavacar.Clientes;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectolavacar.AdminBD;
import com.example.proyectolavacar.MainActivity;
import com.example.proyectolavacar.R;

import java.util.ArrayList;

public class ClienteActivity extends AppCompatActivity {
    EditText txtBuscarCliente;
    ListView listViewClientes;

    ArrayAdapter<String> adapter;
    ArrayList<String> datos;
    int itemseleccionado = -1; // Posición seleccionada


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtBuscarCliente = findViewById(R.id.txtBuscarCliente);
        listViewClientes = findViewById(R.id.listViewClientes);

        // Inicializar lista y adaptador
        datos = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        listViewClientes.setAdapter(adapter);

        // Evento de selección en la lista
        listViewClientes.setOnItemClickListener((parent, view, position, id) -> {
            itemseleccionado = position;
            // Quita colores anteriores
            for (int i = 0; i < listViewClientes.getChildCount(); i++) {
                listViewClientes.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
            // Resalta el nuevo seleccionado
            view.setBackgroundColor(Color.LTGRAY);
        });
    }
    public void InsertarCliente(View view) {
        Intent intent = new Intent(this, InsertCliente.class);
        startActivity(intent);
    }
    public void UpdateCliente(View view) {
        if (itemseleccionado >= 0) {
            String item = adapter.getItem(itemseleccionado);
            String cedula = item.split(" - ")[0]; // obtenemos la cédula

            Intent intent = new Intent(this, UpdateCliente.class);
            intent.putExtra("cedula", cedula); // pasamos la cédula seleccionada
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Debe seleccionar un Cliente", Toast.LENGTH_SHORT).show();
        }
    }


    public void EliminarCliente(View view) {
        if (itemseleccionado >= 0) {
            String item = adapter.getItem(itemseleccionado);
            String cedula = item.split(" - ")[0];
            EliminarPorCedula(cedula);

            adapter.remove(item);
            View itemresaltado = listViewClientes.getChildAt(itemseleccionado);
            if (itemresaltado != null) {
                itemresaltado.setBackgroundColor(0);
            }
            itemseleccionado = -1;
        } else {
            Toast.makeText(getApplicationContext(), "Debe seleccionar un item", Toast.LENGTH_SHORT).show();
        }
    }

    public void EliminarPorCedula(String cedula) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        if (!cedula.isEmpty()) {
            int registrosEliminados = db.delete("Cliente", "cedula=?", new String[]{cedula});
            db.close();

            if (registrosEliminados > 0) {
                Toast.makeText(getApplicationContext(), "Cliente eliminado correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "No se encontró ningún cliente con esa cédula", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Falta la cédula para eliminar", Toast.LENGTH_LONG).show();
        }
    }

    public void BuscarCliente(View view) {
        String criterio = txtBuscarCliente.getText().toString();
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        datos.clear();
        Cursor fila = db.rawQuery("SELECT * FROM Cliente WHERE cedula=?", new String[]{criterio});
        if (fila.moveToFirst()) {
            do {
                String cedula = fila.getString(0);
                String nombre = fila.getString(1);
                String apellidos = fila.getString(2);

                String item = cedula + " - " + nombre + " " + apellidos;
                adapter.add(item);
            } while (fila.moveToNext());

            Toast.makeText(getApplicationContext(), "Cliente(s) encontrado(s)", Toast.LENGTH_LONG).show();
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
        Cursor fila = db.rawQuery("SELECT * FROM Cliente", null);
        if (fila.moveToFirst()) {
            do {
                String cedula = fila.getString(0);
                String nombre = fila.getString(1);
                String apellidos = fila.getString(2);
                String correo = fila.getString(3);
                String telefono = fila.getString(4);


                String item = cedula + " - " + nombre + " " + apellidos  + " - " + correo+ " - " + telefono ;
                adapter.add(item);
            } while (fila.moveToNext());

            Toast.makeText(getApplicationContext(), "Todos los clientes cargados", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No hay clientes registrados", Toast.LENGTH_LONG).show();
        }
        fila.close();
        db.close();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MostrarTodos(null); // recarga la lista automáticamente
    }


    public void regresarcliente(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    }
