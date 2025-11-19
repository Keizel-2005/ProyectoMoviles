package com.example.proyectolavacar.Empleado;

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

public class InsertEmpleado extends AppCompatActivity {

    EditText txtCedula, txtNombre, txtApellidos, txtTelefono, txtCorreo, txtPuesto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertempleado);


        txtCedula = findViewById(R.id.txtCedula);
        txtNombre = findViewById(R.id.txtNombre);
        txtApellidos = findViewById(R.id.txtApellidos);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPuesto = findViewById(R.id.txtPuesto);
    }

    public void Guardar(View view) {
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
            registro.put("cedula", cedula);
            registro.put("nombre", nombre);
            registro.put("apellidos", apellidos);
            registro.put("telefono", telefono);
            registro.put("correo", correo);
            registro.put("puesto", puesto);

            db.insert("Empleados", null, registro);
            db.close();

            Toast.makeText(this, "Empleado registrado correctamente", Toast.LENGTH_LONG).show();

            txtCedula.setText("");
            txtNombre.setText("");
            txtApellidos.setText("");
            txtTelefono.setText("");
            txtCorreo.setText("");
            txtPuesto.setText("");
        } else {
            Toast.makeText(this, "Debe llenar al menos cédula, nombre y apellidos", Toast.LENGTH_LONG).show();
        }
    }

    public void RegresarListEmpleado(View view) {
        Intent intent = new Intent(this, EmpleadoActivity.class);
        startActivity(intent);
        finish();
    }
}
