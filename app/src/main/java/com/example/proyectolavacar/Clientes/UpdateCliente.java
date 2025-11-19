package com.example.proyectolavacar.Clientes;

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

public class UpdateCliente extends AppCompatActivity {

    EditText txtCedula, txtNombre, txtApellidos, txtTelefono, txtCorreo;
    String cedulaRecibida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatecliente); // 🔹 asegúrate que el XML se llame así

        txtCedula = findViewById(R.id.txtCedulaCliente);
        txtNombre = findViewById(R.id.txtNombreCliente);
        txtApellidos = findViewById(R.id.txtApellidosCliente);
        txtTelefono = findViewById(R.id.txtTelefonoCliente);
        txtCorreo = findViewById(R.id.txtCorreoCliente);

        // Recibir la cédula desde ClienteActivity
        cedulaRecibida = getIntent().getStringExtra("cedula");

        // Cargar datos del cliente
        cargarCliente();
    }

    private void cargarCliente() {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("SELECT * FROM Cliente WHERE cedula=?", new String[]{cedulaRecibida});
        if (fila.moveToFirst()) {
            txtCedula.setText(fila.getString(0));
            txtNombre.setText(fila.getString(1));
            txtApellidos.setText(fila.getString(2));
            txtTelefono.setText(fila.getString(3));
            txtCorreo.setText(fila.getString(4));
        }
        fila.close();
        db.close();
    }

    // Método OnClick para Actualizar
    public void ActualizarCliente(View view) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String cedula = txtCedula.getText().toString();
        String nombre = txtNombre.getText().toString();
        String apellidos = txtApellidos.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String correo = txtCorreo.getText().toString();

        if (!cedula.isEmpty() && !nombre.isEmpty() && !apellidos.isEmpty()) {
            ContentValues registro = new ContentValues();
            registro.put("nombre", nombre);
            registro.put("apellidos", apellidos);
            registro.put("telefono", telefono);
            registro.put("correo", correo);

            int filasAfectadas = db.update("Cliente", registro, "cedula=?", new String[]{cedulaRecibida});
            db.close();

            if (filasAfectadas > 0) {
                Toast.makeText(this, "Cliente actualizado correctamente", Toast.LENGTH_LONG).show();
                finish(); // volver a la lista
            } else {
                Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Debe llenar al menos cédula, nombre y apellidos", Toast.LENGTH_LONG).show();
        }
    }

    // Método OnClick para Regresar
    public void RegresarCliente(View view) {
        Intent intent = new Intent(this, ClienteActivity.class);
        startActivity(intent);
        finish();
    }
}
