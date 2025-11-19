package com.example.proyectolavacar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class EmpleadoActivity extends AppCompatActivity {
    EditText txtBuscarEmpleado;
    ListView listViewEmpleados;

    ArrayAdapter<String> adapter;

    ArrayList<String> datos;

    int itemseleccionado = -1; // Posición seleccionada


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_empleado);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtBuscarEmpleado = findViewById(R.id.txtBuscarEmpleado);
        listViewEmpleados = findViewById(R.id.listViewEmpleados);

        // Inicializar lista y adaptador
        datos = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        listViewEmpleados.setAdapter(adapter);
        listViewEmpleados.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            itemseleccionado = position;
            // Quita colores anteriores
            for (int i = 0; i < listViewEmpleados.getChildCount(); i++) {
                listViewEmpleados.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
            // Resalta el nuevo seleccionado
            view.setBackgroundColor(Color.LTGRAY);
        });
        // Consultar(null);

    }

    public void Insertar(View view) {
        Intent intent = new Intent(this, InsertEmpleado.class);
        startActivity(intent);

    }

    public void Eliminar(View view) {
        if (itemseleccionado >= 0) {
            EliminarPorCedula(adapter.getItem(itemseleccionado));
            adapter.remove(adapter.getItem(itemseleccionado));
            // Restablece el color de fondo del elemento seleccionado
            View itemresaltado = listViewEmpleados.getChildAt(itemseleccionado);
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
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        if (!cedula.isEmpty()) {
            int registrosEliminados = BaseDatos.delete("Empleados", "cedula=?", new String[]{cedula});
            BaseDatos.close();

            if (registrosEliminados > 0) {
                Toast.makeText(getApplicationContext(), "Empleado eliminado correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "No se encontró ningún empleado con esa cédula", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Falta la cédula para eliminar", Toast.LENGTH_LONG).show();
        }

    }
    public void Buscar(View view) {
        String criterio = txtBuscarEmpleado.getText().toString();
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        datos.clear();
        Cursor fila = db.rawQuery("SELECT * FROM Empleados WHERE cedula=? OR nombre=?", new String[]{criterio, criterio});
        if (fila.moveToFirst()) {
            do {
                String cedula = fila.getString(0);
                String nombre = fila.getString(1);
                String apellidos = fila.getString(2);
                String puesto = fila.getString(5);

                String item = cedula + " - " + nombre + " " + apellidos + " (" + puesto + ")";
                adapter.add(item);
            } while (fila.moveToNext());

            Toast.makeText(getApplicationContext(), "Empleado(s) encontrado(s)", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No se encontraron registros", Toast.LENGTH_LONG).show();
        }
        fila.close();
        db.close();
    }
    public void regresar(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}


