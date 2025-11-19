package com.example.proyectolavacar.Carros;

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

public class UpdateCarro extends AppCompatActivity {

    EditText txtPlaca, txtModelo, txtAnio, txtCedulaCliente;
    String placaRecibida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatecarro);

        txtPlaca = findViewById(R.id.txtPlaca);
        txtModelo = findViewById(R.id.txtModelo);
        txtAnio = findViewById(R.id.txtAnio);
        txtCedulaCliente = findViewById(R.id.txtCedulaCliente);

        placaRecibida = getIntent().getStringExtra("placa");

        cargarCarro();
    }

    private void cargarCarro() {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("SELECT * FROM Carro WHERE placa=?", new String[]{placaRecibida});
        if (fila.moveToFirst()) {
            txtPlaca.setText(fila.getString(0));
            txtModelo.setText(fila.getString(1));
            txtAnio.setText(fila.getString(2));
            txtCedulaCliente.setText(fila.getString(3));
        }
        fila.close();
        db.close();
    }

    public void Actualizar(View view) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String placa = txtPlaca.getText().toString();
        String modelo = txtModelo.getText().toString();
        String anio = txtAnio.getText().toString();
        String cedulaCliente = txtCedulaCliente.getText().toString();

        if (!placa.isEmpty() && !modelo.isEmpty() && !anio.isEmpty()) {

            // Validar que el cliente exista
            Cursor cursorCliente = db.rawQuery(
                    "SELECT cedula FROM Cliente WHERE cedula=?",
                    new String[]{cedulaCliente}
            );

            if (!cursorCliente.moveToFirst()) {
                cursorCliente.close();
                db.close();
                Toast.makeText(this, "La cédula del cliente no existe", Toast.LENGTH_LONG).show();
                return;
            }
            cursorCliente.close();

            ContentValues registro = new ContentValues();
            registro.put("modelo", modelo);
            registro.put("anio", anio);
            registro.put("cedulaCliente", cedulaCliente);

            int filasAfectadas = db.update("Carro", registro, "placa=?", new String[]{placaRecibida});
            db.close();

            if (filasAfectadas > 0) {
                Toast.makeText(this, "Carro actualizado correctamente", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Debe llenar placa, modelo y año", Toast.LENGTH_LONG).show();
        }
    }

    public void Regresar(View view) {
        Intent intent = new Intent(this, com.example.proyectolavacar.Carros.CarroActivity.class);
        startActivity(intent);
        finish();
    }
}
