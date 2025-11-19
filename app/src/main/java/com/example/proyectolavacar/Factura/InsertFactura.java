package com.example.proyectolavacar.Factura;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectolavacar.AdminBD;
import com.example.proyectolavacar.MainActivity;
import com.example.proyectolavacar.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InsertFactura extends AppCompatActivity {

    // Maestro
    EditText txtCedulaCliente, txtCedulaEmpleado, txtPlacaCarro;
    TextView tvNombreCliente, tvNombreEmpleado, tvInfoCarro;
    ImageButton btnBuscarCliente, btnBuscarEmpleado, btnBuscarCarro;

    // Detalle
    EditText txtIdServicio, txtCantidad, txtPrecio;
    TextView tvNombreServicio, tvSubtotal;
    ImageButton btnBuscarServicio;
    ListView listViewDetalles;
    ArrayAdapter<String> adapterDetalles;
    ArrayList<String> datosDetalles;
    int itemseleccionado = -1;

    // Totales en memoria
    double totalFactura = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insertfactura);

        // maestro
        txtCedulaCliente = findViewById(R.id.txtCedulaCliente);
        txtCedulaEmpleado = findViewById(R.id.txtCedulaEmpleado);
        txtPlacaCarro = findViewById(R.id.txtPlacaCarro);
        tvNombreCliente = findViewById(R.id.tvNombreCliente);
        tvNombreEmpleado = findViewById(R.id.tvNombreEmpleado);
        tvInfoCarro = findViewById(R.id.tvInfoCarro);

        btnBuscarCliente = findViewById(R.id.btnBuscarCliente);
        btnBuscarEmpleado = findViewById(R.id.btnBuscarEmpleado);
        btnBuscarCarro = findViewById(R.id.btnBuscarCarro);

        // detalle
        txtIdServicio = findViewById(R.id.txtIdServicio);
        txtCantidad = findViewById(R.id.txtCantidad);
        txtPrecio = findViewById(R.id.txtPrecio);
        tvNombreServicio = findViewById(R.id.tvNombreServicio);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        btnBuscarServicio = findViewById(R.id.btnBuscarServicio);

        listViewDetalles = findViewById(R.id.listViewDetalles);
        datosDetalles = new ArrayList<>();
        adapterDetalles = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datosDetalles);
        listViewDetalles.setAdapter(adapterDetalles);

        listViewDetalles.setOnItemClickListener((parent, view, position, id) -> {
            itemseleccionado = position;
            // quitar colores visibles (igual que tu patrón)
            for (int i = 0; i < listViewDetalles.getChildCount(); i++) {
                listViewDetalles.getChildAt(i).setBackgroundColor(0);
            }
            view.setBackgroundColor(0xFFDDDDDD);
        });

        // cuando cambia cantidad o precio, actualizar subtotal visual (no automático, queda manual en este estilo)
        // (seguí tu estilo: acciones a través de botones, no TextWatchers)
    }

    // ====== BÚSQUEDAS (muestran nombre/descr según código) ======

    public void BuscarCliente(View view) {
        String cedula = txtCedulaCliente.getText().toString();
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        if (cedula.isEmpty()) {
            Toast.makeText(this, "Ingrese cédula cliente", Toast.LENGTH_LONG).show();
            db.close();
            return;
        }

        Cursor fila = db.rawQuery("SELECT nombre, apellidos FROM Cliente WHERE cedula=?", new String[]{cedula});
        if (fila.moveToFirst()) {
            String nombre = fila.getString(0);
            String apellidos = fila.getString(1);
            tvNombreCliente.setText(nombre + " " + apellidos);
            Toast.makeText(this, "Cliente encontrado", Toast.LENGTH_SHORT).show();
        } else {
            tvNombreCliente.setText("Cliente no encontrado");
            Toast.makeText(this, "Cliente no existe", Toast.LENGTH_LONG).show();
        }
        fila.close();
        db.close();
    }

    public void BuscarEmpleado(View view) {
        String cedula = txtCedulaEmpleado.getText().toString();
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        if (cedula.isEmpty()) {
            Toast.makeText(this, "Ingrese cédula empleado", Toast.LENGTH_LONG).show();
            db.close();
            return;
        }

        Cursor fila = db.rawQuery("SELECT nombre, apellidos FROM Empleados WHERE cedula=?", new String[]{cedula});
        if (fila.moveToFirst()) {
            String nombre = fila.getString(0);
            String apellidos = fila.getString(1);
            tvNombreEmpleado.setText(nombre + " " + apellidos);
            Toast.makeText(this, "Empleado encontrado", Toast.LENGTH_SHORT).show();
        } else {
            tvNombreEmpleado.setText("Empleado no encontrado");
            Toast.makeText(this, "Empleado no existe", Toast.LENGTH_LONG).show();
        }
        fila.close();
        db.close();
    }

    public void BuscarCarro(View view) {
        String placa = txtPlacaCarro.getText().toString();
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        if (placa.isEmpty()) {
            Toast.makeText(this, "Ingrese placa", Toast.LENGTH_LONG).show();
            db.close();
            return;
        }

        Cursor fila = db.rawQuery("SELECT modelo, anio FROM Carro WHERE placa=?", new String[]{placa});
        if (fila.moveToFirst()) {
            String modelo = fila.getString(0);
            String anio = fila.getString(1);
            tvInfoCarro.setText(modelo + " - " + anio);
            Toast.makeText(this, "Carro encontrado", Toast.LENGTH_SHORT).show();
        } else {
            tvInfoCarro.setText("Carro no encontrado");
            Toast.makeText(this, "Carro no existe", Toast.LENGTH_LONG).show();
        }
        fila.close();
        db.close();
    }

    public void BuscarServicio(View view) {
        String idServicio = txtIdServicio.getText().toString();
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        if (idServicio.isEmpty()) {
            Toast.makeText(this, "Ingrese código servicio", Toast.LENGTH_LONG).show();
            db.close();
            return;
        }

        Cursor fila = db.rawQuery("SELECT nombre, precio FROM Servicio WHERE idServicio=?", new String[]{idServicio});
        if (fila.moveToFirst()) {
            String nombre = fila.getString(0);
            String precio = fila.getString(1);
            tvNombreServicio.setText(nombre);
            txtPrecio.setText(precio);
            Toast.makeText(this, "Servicio encontrado", Toast.LENGTH_SHORT).show();
            // actualizar subtotal visual si cantidad ya existe
            String cantidadStr = txtCantidad.getText().toString();
            double cant = 0;
            try { cant = Double.parseDouble(cantidadStr); } catch (Exception e) { cant = 0; }
            double prec = 0;
            try { prec = Double.parseDouble(precio); } catch (Exception e) { prec = 0; }
            double subtotal = cant * prec;
            tvSubtotal.setText("Subtotal: " + (int)subtotal);
        } else {
            tvNombreServicio.setText("Servicio no encontrado");
            txtPrecio.setText("");
            Toast.makeText(this, "Servicio no existe", Toast.LENGTH_LONG).show();
        }
        fila.close();
        db.close();
    }

    // ====== DETALLE: Agregar / Editar / Eliminar en ListView ======

    public void AgregarDetalle(View view) {
        String idServicio = txtIdServicio.getText().toString();
        String nombreServicio = tvNombreServicio.getText().toString();
        String cantidadStr = txtCantidad.getText().toString();
        String precioStr = txtPrecio.getText().toString();

        if (idServicio.isEmpty() || nombreServicio.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Complete código servicio y búsquelo antes de agregar", Toast.LENGTH_LONG).show();
            return;
        }

        int cantidad = 1;
        try { cantidad = Integer.parseInt(cantidadStr); } catch (Exception e) { cantidad = 1; }
        double precio = 0;
        try { precio = Double.parseDouble(precioStr); } catch (Exception e) { precio = 0; }
        double subtotal = cantidad * precio;

        // formato mostrado en el ListView (igual estilo al resto)
        String item = idServicio + " - " + nombreServicio + " x" + cantidad + " (subtotal: " + (int)subtotal + ")";
        adapterDetalles.add(item);

        totalFactura += subtotal;

        // limpiar campos de detalle (estilo simple)
        txtIdServicio.setText("");
        tvNombreServicio.setText("");
        txtCantidad.setText("");
        txtPrecio.setText("");
        tvSubtotal.setText("Subtotal: 0");

        Toast.makeText(this, "Detalle agregado", Toast.LENGTH_SHORT).show();
    }

    public void EditarDetalle(View view) {
        if (itemseleccionado >= 0) {
            String item = adapterDetalles.getItem(itemseleccionado);
            // parsear item con el mismo formato: "id - nombre xN (subtotal: S)"
            // simple split para rellenar campos
            try {
                String[] parts = item.split(" - ");
                String id = parts[0];
                String right = parts[1];
                String nombre = right.split(" x")[0];
                String afterX = right.split(" x")[1]; // "N (subtotal: S)"
                String cantidadStr = afterX.split(" ")[0];
                // precio no está en el string; requerimos buscar servicio para recuperar precio
                txtIdServicio.setText(id);
                txtCantidad.setText(cantidadStr);
                // buscar el servicio para llenar nombre y precio
                txtPrecio.setText("");
                tvNombreServicio.setText(nombre);
            } catch (Exception e) {
                // no hacer nada complejo; mantener minimal
            }
            // quitar color y resetear selección (igual estilo)
            listViewDetalles.getChildAt(itemseleccionado).setBackgroundColor(0);
            itemseleccionado = -1;
        } else {
            Toast.makeText(this, "Debe seleccionar un detalle", Toast.LENGTH_SHORT).show();
        }
    }

    public void EliminarDetalle(View view) {
        if (itemseleccionado >= 0) {
            String item = adapterDetalles.getItem(itemseleccionado);
            // quitar del adapter
            adapterDetalles.remove(item);
            listViewDetalles.getChildAt(itemseleccionado).setBackgroundColor(0);
            itemseleccionado = -1;
            Toast.makeText(this, "Detalle eliminado", Toast.LENGTH_SHORT).show();
            // recalcular total (simple: recalcular desde todos los items)
            recalcularTotalDesdeAdapter();
        } else {
            Toast.makeText(this, "Debe seleccionar un detalle", Toast.LENGTH_SHORT).show();
        }
    }

    private void recalcularTotalDesdeAdapter() {
        totalFactura = 0;
        for (int i = 0; i < adapterDetalles.getCount(); i++) {
            String item = adapterDetalles.getItem(i);
            // parsear subtotal: buscar "(subtotal: S)"
            try {
                String s = item;
                int idx = s.indexOf("(subtotal:");
                if (idx >= 0) {
                    String right = s.substring(idx + 10).replace(")", "").trim();
                    double sub = Double.parseDouble(right);
                    totalFactura += sub;
                }
            } catch (Exception e) { /* ignore */ }
        }
    }

    // ====== GUARDAR MAESTRO + DETALLE en BD ======

    public void GuardarFactura(View view) {

        // validaciones de maestro
        String cedulaCliente = txtCedulaCliente.getText().toString();
        String cedulaEmpleado = txtCedulaEmpleado.getText().toString();
        String placa = txtPlacaCarro.getText().toString();

        if (cedulaCliente.isEmpty() || cedulaEmpleado.isEmpty() || placa.isEmpty()) {
            Toast.makeText(this, "Complete los datos del encabezado", Toast.LENGTH_LONG).show();
            return;
        }

        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        // validar cliente
        Cursor cCliente = db.rawQuery("SELECT cedula FROM Cliente WHERE cedula=?", new String[]{cedulaCliente});
        if (!cCliente.moveToFirst()) {
            cCliente.close();
            db.close();
            Toast.makeText(this, "La cédula del cliente no existe", Toast.LENGTH_LONG).show();
            return;
        }
        cCliente.close();

        // validar empleado
        Cursor cEmpleado = db.rawQuery("SELECT cedula FROM Empleados WHERE cedula=?", new String[]{cedulaEmpleado});
        if (!cEmpleado.moveToFirst()) {
            cEmpleado.close();
            db.close();
            Toast.makeText(this, "La cédula del empleado no existe", Toast.LENGTH_LONG).show();
            return;
        }
        cEmpleado.close();

        // validar placa carro
        Cursor cCarro = db.rawQuery("SELECT placa FROM Carro WHERE placa=?", new String[]{placa});
        if (!cCarro.moveToFirst()) {
            cCarro.close();
            db.close();
            Toast.makeText(this, "La placa del carro no existe", Toast.LENGTH_LONG).show();
            return;
        }
        cCarro.close();

        // validar que exista al menos 1 detalle
        if (adapterDetalles.getCount() == 0) {
            db.close();
            Toast.makeText(this, "Agregue al menos un detalle", Toast.LENGTH_LONG).show();
            return;
        }

        // calcular total (ya en memoria pero recalcular por si acaso)
        recalcularTotalDesdeAdapter();

        // insertar EncabezadoFactura
        ContentValues regEnc = new ContentValues();
        // fecha automática del sistema
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        regEnc.put("fecha", fecha);
        regEnc.put("cedulaCliente", cedulaCliente);
        regEnc.put("placaCarro", placa);
        regEnc.put("cedulaEmpleado", cedulaEmpleado);
        regEnc.put("total", (int) totalFactura); // guardamos entero igual estilo anterior

        long idEnc = db.insert("EncabezadoFactura", null, regEnc);
        if (idEnc == -1) {
            db.close();
            Toast.makeText(this, "Error al insertar encabezado", Toast.LENGTH_LONG).show();
            return;
        }

        // insertar detalles: cada item del adapter guarda idServicio y subtotal (en campo precio de DetalleFactura)
        for (int i = 0; i < adapterDetalles.getCount(); i++) {
            String item = adapterDetalles.getItem(i);
            // parsear: "id - nombre xQ (subtotal: S)"
            try {
                String idPart = item.split(" - ")[0];
                String s = item;
                int idx = s.indexOf("(subtotal:");
                String subs = "0";
                if (idx >= 0) {
                    subs = s.substring(idx + 10).replace(")", "").trim();
                }
                ContentValues regDet = new ContentValues();
                regDet.put("idFactura", idEnc);
                regDet.put("idServicio", idPart);
                // guardamos el subtotal en precio porque así está la tabla DetalleFactura
                regDet.put("precio", (int) Double.parseDouble(subs));
                db.insert("DetalleFactura", null, regDet);
            } catch (Exception e) {
                // continuar con siguiente
            }
        }

        db.close();
        Toast.makeText(this, "Factura guardada correctamente", Toast.LENGTH_LONG).show();

        // limpiar todo y volver al main (misma costumbre en tu app)
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