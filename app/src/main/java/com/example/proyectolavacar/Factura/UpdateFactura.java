package com.example.proyectolavacar.Factura;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectolavacar.AdminBD;
import com.example.proyectolavacar.MainActivity;
import com.example.proyectolavacar.R;

import java.util.ArrayList;

public class UpdateFactura extends AppCompatActivity {

    // Maestro
    EditText txtCedulaCliente, txtCedulaEmpleado, txtPlacaCarro;
    TextView tvNombreCliente, tvNombreEmpleado, tvInfoCarro;

    // Detalle
    EditText txtIdServicio, txtCantidad, txtPrecio;
    TextView tvNombreServicio, tvSubtotal;
    ListView listViewDetalles;
    ArrayAdapter<String> adapterDetalles;
    ArrayList<String> datosDetalles;
    int itemseleccionado = -1;

    String idFacturaRecibida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_updatefactura);

        // enlazar views (mismos ids que en insert)
        txtCedulaCliente = findViewById(R.id.txtCedulaCliente);
        txtCedulaEmpleado = findViewById(R.id.txtCedulaEmpleado);
        txtPlacaCarro = findViewById(R.id.txtPlacaCarro);
        tvNombreCliente = findViewById(R.id.tvNombreCliente);
        tvNombreEmpleado = findViewById(R.id.tvNombreEmpleado);
        tvInfoCarro = findViewById(R.id.tvInfoCarro);

        txtIdServicio = findViewById(R.id.txtIdServicio);
        txtPrecio = findViewById(R.id.txtPrecio);
        tvNombreServicio = findViewById(R.id.tvNombreServicio);


        listViewDetalles = findViewById(R.id.listViewDetalles);
        datosDetalles = new ArrayList<>();
        adapterDetalles = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datosDetalles);
        listViewDetalles.setAdapter(adapterDetalles);

        listViewDetalles.setOnItemClickListener((parent, view, position, id) -> {
            itemseleccionado = position;
            for (int i = 0; i < listViewDetalles.getChildCount(); i++) {
                listViewDetalles.getChildAt(i).setBackgroundColor(0);
            }
            view.setBackgroundColor(0xFFDDDDDD);
        });

        // recibir idFactura
        idFacturaRecibida = getIntent().getStringExtra("idFactura");
        if (idFacturaRecibida != null && !idFacturaRecibida.isEmpty()) {
            cargarFactura();
        }
    }

    private void cargarFactura() {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        // cargar encabezado
        Cursor f = db.rawQuery("SELECT fecha, cedulaCliente, placaCarro, cedulaEmpleado, total FROM EncabezadoFactura WHERE idFactura=?", new String[]{idFacturaRecibida});
        if (f.moveToFirst()) {
            txtCedulaCliente.setText(f.getString(1));
            txtPlacaCarro.setText(f.getString(2));
            txtCedulaEmpleado.setText(f.getString(3));
            // mostrar nombres mediante consultas simples (igual estilo)
            Cursor cCli = db.rawQuery("SELECT nombre, apellidos FROM Cliente WHERE cedula=?", new String[]{f.getString(1)});
            if (cCli.moveToFirst()) { tvNombreCliente.setText(cCli.getString(0) + " " + cCli.getString(1)); }
            cCli.close();
            Cursor cEmp = db.rawQuery("SELECT nombre, apellidos FROM Empleados WHERE cedula=?", new String[]{f.getString(3)});
            if (cEmp.moveToFirst()) { tvNombreEmpleado.setText(cEmp.getString(0) + " " + cEmp.getString(1)); }
            cEmp.close();
            // carro info
            Cursor cCar = db.rawQuery("SELECT modelo, anio FROM Carro WHERE placa=?", new String[]{f.getString(2)});
            if (cCar.moveToFirst()) { tvInfoCarro.setText(cCar.getString(0)+" - "+cCar.getString(1)); }
            cCar.close();
        }
        f.close();

        // cargar detalles
        Cursor fd = db.rawQuery("SELECT idDetalle, idServicio, precio FROM DetalleFactura WHERE idFactura=?", new String[]{idFacturaRecibida});
        if (fd.moveToFirst()) {
            do {
                String idServicio = fd.getString(1);
                String precio = fd.getString(2); // en DB guardamos subtotal en precio
                // buscar nombre servicio
                String nombre = "";
                Cursor cs = db.rawQuery("SELECT nombre FROM Servicio WHERE idServicio=?", new String[]{idServicio});
                if (cs.moveToFirst()) { nombre = cs.getString(0); }
                cs.close();
                // no guardamos cantidad en BD; lo mostramos como 1 por defecto en la lista (estilo coherente con DB)
                String item = idServicio + " - " + nombre + " x1 (subtotal: " + precio + ")";
                adapterDetalles.add(item);
            } while (fd.moveToNext());
        }
        fd.close();
        db.close();
    }

    // Método Actualizar: actualiza Encabezado y reemplaza Detalles (estrategia simple: borrar detalles previos e insertar los actuales)
    public void ActualizarFactura(View view) {
        String cedulaCliente = txtCedulaCliente.getText().toString();
        String cedulaEmpleado = txtCedulaEmpleado.getText().toString();
        String placa = txtPlacaCarro.getText().toString();

        if (cedulaCliente.isEmpty() || cedulaEmpleado.isEmpty() || placa.isEmpty()) {
            Toast.makeText(this, "Complete los datos del encabezado", Toast.LENGTH_LONG).show();
            return;
        }

        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // validar llaves foráneas
        Cursor cCli = db.rawQuery("SELECT cedula FROM Cliente WHERE cedula=?", new String[]{cedulaCliente});
        if (!cCli.moveToFirst()) { cCli.close(); db.close(); Toast.makeText(this, "Cliente no existe", Toast.LENGTH_LONG).show(); return; }
        cCli.close();
        Cursor cEmp = db.rawQuery("SELECT cedula FROM Empleados WHERE cedula=?", new String[]{cedulaEmpleado});
        if (!cEmp.moveToFirst()) { cEmp.close(); db.close(); Toast.makeText(this, "Empleado no existe", Toast.LENGTH_LONG).show(); return; }
        cEmp.close();
        Cursor cCar = db.rawQuery("SELECT placa FROM Carro WHERE placa=?", new String[]{placa});
        if (!cCar.moveToFirst()) { cCar.close(); db.close(); Toast.makeText(this, "Carro no existe", Toast.LENGTH_LONG).show(); return; }
        cCar.close();

        // actualizar encabezado
        ContentValues regEnc = new ContentValues();
        regEnc.put("cedulaCliente", cedulaCliente);
        regEnc.put("placaCarro", placa);
        regEnc.put("cedulaEmpleado", cedulaEmpleado);

        // recalcular total desde adapter
        int total = 0;
        for (int i = 0; i < adapterDetalles.getCount(); i++) {
            String item = adapterDetalles.getItem(i);
            try {
                int idx = item.indexOf("(subtotal:");
                if (idx >= 0) {
                    String subs = item.substring(idx + 10).replace(")", "").trim();
                    total += Integer.parseInt(subs);
                }
            } catch (Exception e) { }
        }
        regEnc.put("total", total);

        int filas = db.update("EncabezadoFactura", regEnc, "idFactura=?", new String[]{idFacturaRecibida});
        if (filas <= 0) {
            db.close();
            Toast.makeText(this, "No se pudo actualizar encabezado", Toast.LENGTH_LONG).show();
            return;
        }

        // borrar detalles antiguos
        db.delete("DetalleFactura", "idFactura=?", new String[]{idFacturaRecibida});

        // insertar detalles actuales
        for (int i = 0; i < adapterDetalles.getCount(); i++) {
            String item = adapterDetalles.getItem(i);
            try {
                String idServ = item.split(" - ")[0];
                int idx = item.indexOf("(subtotal:");
                String subs = "0";
                if (idx >= 0) subs = item.substring(idx + 10).replace(")", "").trim();
                ContentValues regDet = new ContentValues();
                regDet.put("idFactura", idFacturaRecibida);
                regDet.put("idServicio", idServ);
                regDet.put("precio", Integer.parseInt(subs)); // subtotal guardado en precio
                db.insert("DetalleFactura", null, regDet);
            } catch (Exception e) { }
        }

        db.close();
        Toast.makeText(this, "Factura actualizada correctamente", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void Salir(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}