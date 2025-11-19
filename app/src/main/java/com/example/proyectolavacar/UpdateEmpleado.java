package com.example.proyectolavacar;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateEmpleado extends AppCompatActivity {

    EditText txtCedula, txtNombre, txtApellidos, txtTelefono, txtCorreo, txtPuesto;
    String cedulaRecibida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_updateempleado);

        txtCedula = findViewById(R.id.txtCedula);
        txtNombre = findViewById(R.id.txtNombre);
        txtApellidos = findViewById(R.id.txtApellidos);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPuesto = findViewById(R.id.txtPuesto);

        // Recibir la cédula desde EmpleadoActivity
        cedulaRecibida = getIntent().getStringExtra("cedula");

        // Cargar datos del empleado
        cargarEmpleado();
    }

    private void cargarEmpleado() {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor fila = db.rawQuery("SELECT * FROM Empleados WHERE cedula=?", new String[]{cedulaRecibida});
        if (fila.moveToFirst()) {
            txtCedula.setText(fila.getString(0));
            txtNombre.setText(fila.getString(1));
            txtApellidos.setText(fila.getString(2));
            txtTelefono.setText(fila.getString(3));
            txtCorreo.setText(fila.getString(4));
            txtPuesto.setText(fila.getString(5));
        }
        fila.close();
        db.close();
    }

    // Método OnClick para Actualizar
    public void Actualizar(View view) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String cedula = txtCedula.getText().toString();
        String nombre = txtNombre.getText().toString();
        String apellidos = txtApellidos.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String correo = txtCorreo.getText().toString();
        String puesto = txtPuesto.getText().toString();

        if (!cedula.isEmpty() && !nombre.isEmpty() && !apellidos.isEmpty()) {
            ContentValues registro = new ContentValues();
            registro.put("nombre", nombre);
            registro.put("apellidos", apellidos);
            registro.put("telefono", telefono);
            registro.put("correo", correo);
            registro.put("puesto", puesto);

            int filasAfectadas = db.update("Empleados", registro, "cedula=?", new String[]{cedulaRecibida});
            db.close();

            if (filasAfectadas > 0) {
                Toast.makeText(this, "Empleado actualizado correctamente", Toast.LENGTH_LONG).show();
                finish(); // volver a la lista
            } else {
                Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Debe llenar al menos cédula, nombre y apellidos", Toast.LENGTH_LONG).show();
        }
    }

    // Método OnClick para Regresar
    public void REgresar(View view) {
        Intent intent = new Intent(this, EmpleadoActivity.class);
        startActivity(intent);
        finish();
    }
}
