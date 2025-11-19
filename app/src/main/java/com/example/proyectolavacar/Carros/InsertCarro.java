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

public class InsertCarro extends AppCompatActivity {

    EditText txtPlaca, txtModelo, txtAno, txtCedulaCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertcarro);

        txtPlaca = findViewById(R.id.txtPlaca);
        txtModelo = findViewById(R.id.txtModelo);
        txtAno = findViewById(R.id.txtAnio);
        txtCedulaCliente = findViewById(R.id.txtCedulaCliente);
    }

    // Método OnClick para Guardar
    public void Guardar(View view) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String placa = txtPlaca.getText().toString();
        String modelo = txtModelo.getText().toString();
        String anio = txtAno.getText().toString();
        String cedulaCliente = txtCedulaCliente.getText().toString();

        // Validación de campos requeridos
        if (!placa.isEmpty() && !modelo.isEmpty() && !cedulaCliente.isEmpty()) {

            // Validar que exista el cliente
            Cursor fila = db.rawQuery(
                    "SELECT cedula FROM Cliente WHERE cedula=?",
                    new String[]{cedulaCliente}
            );

            if (!fila.moveToFirst()) {
                Toast.makeText(this, "El cliente no existe", Toast.LENGTH_LONG).show();
                fila.close();
                db.close();
                return;
            }

            fila.close();

            ContentValues registro = new ContentValues();
            registro.put("placa", placa);
            registro.put("modelo", modelo);
            registro.put("anio", anio);
            registro.put("cedulaCliente", cedulaCliente);

            db.insert("Carro", null, registro);
            db.close();

            Toast.makeText(this, "Carro registrado correctamente", Toast.LENGTH_LONG).show();

            // Limpiar campos
            txtPlaca.setText("");
            txtModelo.setText("");
            txtAno.setText("");
            txtCedulaCliente.setText("");

        } else {
            Toast.makeText(this, "Debe llenar placa, modelo y cédula del cliente", Toast.LENGTH_LONG).show();
        }
    }

    // Método OnClick para Regresar
    public void Regresar(View view) {
        Intent intent = new Intent(this, com.example.proyectolavacar.Carros.CarroActivity.class);
        startActivity(intent);
        finish();
    }
}
