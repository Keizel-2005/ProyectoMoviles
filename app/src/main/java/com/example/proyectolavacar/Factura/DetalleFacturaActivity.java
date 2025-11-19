package com.example.proyectolavacar.Factura;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectolavacar.AdminBD;
import com.example.proyectolavacar.MainActivity;
import com.example.proyectolavacar.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DetalleFacturaActivity extends AppCompatActivity {

    TextView tvIdFactura, tvFecha, tvCliente, tvEmpleado, tvPlaca, tvTotal;
    ListView listViewDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_factura);

        tvIdFactura = findViewById(R.id.tvIdFactura);
        tvFecha = findViewById(R.id.tvFecha);
        tvCliente = findViewById(R.id.tvCliente);
        tvEmpleado = findViewById(R.id.tvEmpleado);
        tvPlaca = findViewById(R.id.tvPlaca);
        tvTotal = findViewById(R.id.tvTotal);
        listViewDetalle = findViewById(R.id.listViewDetalle);

        String idFactura = getIntent().getStringExtra("idFactura");
        if (idFactura == null) {
            Toast.makeText(this, "No se recibió idFactura", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        cargarDatosFactura(idFactura);
    }

    private void cargarDatosFactura(String idFactura) {
        AdminBD admin = new AdminBD(this, "lavacar", null, 1);
        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor cEnc = db.rawQuery("SELECT * FROM EncabezadoFactura WHERE idFactura=?", new String[]{idFactura});
        if (cEnc.moveToFirst()) {
            String fecha = cEnc.getString(1);
            String cedulaCliente = cEnc.getString(2);
            String placa = cEnc.getString(3);
            String cedulaEmpleado = cEnc.getString(4);
            String total = cEnc.getString(5);

            tvIdFactura.setText("Factura: " + idFactura);
            tvFecha.setText("Fecha: " + fecha);
            tvCliente.setText("Cliente: " + cedulaCliente);
            tvEmpleado.setText("Empleado: " + cedulaEmpleado);
            tvPlaca.setText("Placa: " + placa);
            tvTotal.setText("Total: " + total);
        }
        cEnc.close();

        Cursor cDet = db.rawQuery(
                "SELECT d.idServicio, s.nombre, d.precio FROM DetalleFactura d INNER JOIN Servicio s ON d.idServicio=s.idServicio WHERE d.idFactura=?",
                new String[]{idFactura});

        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        if (cDet.moveToFirst()) {
            do {
                HashMap<String, String> item = new HashMap<>();
                item.put("idServicio", cDet.getString(0));
                item.put("nombre", cDet.getString(1));
                item.put("precio", cDet.getString(2));
                lista.add(item);
            } while (cDet.moveToNext());
        }
        cDet.close();
        db.close();

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                lista,
                android.R.layout.simple_list_item_2,
                new String[]{"nombre", "precio"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        listViewDetalle.setAdapter(adapter);
    }
    public void regresardetalle(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
