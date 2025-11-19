package com.example.proyectolavacar.Servicios;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectolavacar.AdminBD;
import com.example.proyectolavacar.R;

public class UpdateServicio extends AppCompatActivity {

    EditText txtIdServicio, txtNombre, txtPrecio;
    String idServicioRecibido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateservicio);

        txtIdServicio = findViewById(R.id.txtIdServicio);
        txtNombre = findViewById(R.id.txtNombreServicio);
        txtPrecio = findViewById(R.id.txtPrecioServicio);

        // Recibir el idServicio desde ServicioActivity
        idServicioRecibido = getIntent().getStringExtra("idServicio");

        // Cargar datos del servicio
        cargarServicio();
    }

    private void cargarServicio() {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("SELECT * FROM Servicio WHERE idServicio=?", new String[]{idServicioRecibido});
        if (fila.moveToFirst()) {
            txtIdServicio.setText(fila.getString(0));
            txtNombre.setText(fila.getString(1));
            txtPrecio.setText(fila.getString(2));
        }
        fila.close();
        db.close();
    }

    // Método OnClick para Actualizar
    public void ActualizarServicio(View view) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String nombre = txtNombre.getText().toString();
        String precio = txtPrecio.getText().toString();

        if (!nombre.isEmpty() && !precio.isEmpty()) {
            ContentValues registro = new ContentValues();
            registro.put("nombre", nombre);
            registro.put("precio", precio);

            int filasAfectadas = db.update("Servicio", registro, "idServicio=?", new String[]{idServicioRecibido});
            db.close();

            if (filasAfectadas > 0) {
                Toast.makeText(this, "Servicio actualizado correctamente", Toast.LENGTH_LONG).show();
                finish(); // volver a la lista
            } else {
                Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Debe llenar nombre y precio", Toast.LENGTH_LONG).show();
        }
    }

    // Método OnClick para Regresar
    public void RegresarServicio(View view) {
        Intent intent = new Intent(this, ServicioActivity.class);
        startActivity(intent);
        finish();
    }
}
