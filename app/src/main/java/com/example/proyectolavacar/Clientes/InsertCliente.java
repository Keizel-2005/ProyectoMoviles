package com.example.proyectolavacar.Clientes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectolavacar.AdminBD;
import com.example.proyectolavacar.Clientes.ClienteActivity;
import com.example.proyectolavacar.R;

public class InsertCliente extends AppCompatActivity {

    EditText txtCedula, txtNombre, txtApellidos, txtTelefono, txtCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertcliente);


        txtCedula = findViewById(R.id.txtCedulaCliente);
        txtNombre = findViewById(R.id.txtNombreCliente);
        txtApellidos = findViewById(R.id.txtApellidosCliente);
        txtTelefono = findViewById(R.id.txtTelefonoCliente);
        txtCorreo = findViewById(R.id.txtCorreoCliente);
    }

    // Método OnClick para Guardar
    public void GuardarCliente(View view) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        String cedula = txtCedula.getText().toString();
        String nombre = txtNombre.getText().toString();
        String apellidos = txtApellidos.getText().toString();
        String telefono = txtTelefono.getText().toString();
        String correo = txtCorreo.getText().toString();

        if (!cedula.isEmpty() && !nombre.isEmpty() && !apellidos.isEmpty()) {
            ContentValues registro = new ContentValues();
            registro.put("cedula", cedula);
            registro.put("nombre", nombre);
            registro.put("apellidos", apellidos);
            registro.put("telefono", telefono);
            registro.put("correo", correo);

            db.insert("Cliente", null, registro);
            db.close();

            Toast.makeText(this, "CLiente registrado correctamente", Toast.LENGTH_LONG).show();

            // Limpia los campos
            txtCedula.setText("");
            txtNombre.setText("");
            txtApellidos.setText("");
            txtTelefono.setText("");
            txtCorreo.setText("");
        } else {
            Toast.makeText(this, "Debe llenar al menos cédula, nombre y apellidos", Toast.LENGTH_LONG).show();
        }
    }

    // Método OnClick para Regresar
    public void RegresarListCliente(View view) {
        Intent intent = new Intent(this, ClienteActivity.class);
        startActivity(intent);
        finish();
    }
}
