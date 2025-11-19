package com.example.proyectolavacar.Servicios;



import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectolavacar.AdminBD;
import com.example.proyectolavacar.R;

public class InsertServicio extends AppCompatActivity {

    EditText txtNombre, txtPrecio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertservicio);

        txtNombre = findViewById(R.id.txtNombreServicio);
        txtPrecio = findViewById(R.id.txtPrecioServicio);
    }

    // Método OnClick para Guardar
    public void GuardarServicio(View view) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String nombre = txtNombre.getText().toString();
        String precio = txtPrecio.getText().toString();

        if (!nombre.isEmpty() && !precio.isEmpty()) {
            ContentValues registro = new ContentValues();
            registro.put("nombre", nombre);
            registro.put("precio", precio);

            db.insert("Servicio", null, registro);
            db.close();

            Toast.makeText(this, "Servicio registrado correctamente", Toast.LENGTH_LONG).show();

            // Limpia los campos
            txtNombre.setText("");
            txtPrecio.setText("");
        } else {
            Toast.makeText(this, "Debe llenar nombre y precio", Toast.LENGTH_LONG).show();
        }
    }

    // Método OnClick para Regresar
    public void RegresarListServicio(View view) {
        Intent intent = new Intent(this, ServicioActivity.class);
        startActivity(intent);
        finish();
    }
}
