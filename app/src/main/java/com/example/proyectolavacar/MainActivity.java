package com.example.proyectolavacar;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.proyectolavacar.Carros.CarroActivity;
import com.example.proyectolavacar.Clientes.ClienteActivity;
import com.example.proyectolavacar.Empleado.EmpleadoActivity;
import com.example.proyectolavacar.Servicios.ServicioActivity;


public class MainActivity extends AppCompatActivity {

    ListView lvMenu;

    String[] menuTitles = {
            "Clientes",
            "Empleados",
            "Carros",
            "Servicios",
            "Factura"
    };

    int[] menuIcons = {
            R.drawable.ic_cliente,
            R.drawable.ic_empleado,
            R.drawable.ic_carro,
            R.drawable.ic_servicio,
            R.drawable.ic_factura
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvMenu = findViewById(R.id.lvMenu);

        menuTitles = new String[]{
                getString(R.string.menus_customers),
                getString(R.string.menus_employees),
                getString(R.string.menus_cars),
                getString(R.string.menus_services),
                getString(R.string.menus_invoice)
        };

        MenuAdapter adapter = new MenuAdapter(this, menuTitles, menuIcons);
        lvMenu.setAdapter(adapter);

        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position) {
                    case 0:
                        startActivity(new Intent(MainActivity.this, ClienteActivity.class));
                        break;

                    case 1:
                        startActivity(new Intent(MainActivity.this, EmpleadoActivity.class));
                        break;

                    case 2:
                        startActivity(new Intent(MainActivity.this, CarroActivity.class));
                        break;

                    case 3:
                        startActivity(new Intent(MainActivity.this, ServicioActivity.class));
                        break;

                    case 4:
                        startActivity(new Intent(MainActivity.this, FacturaActivity.class));
                        break;
                }
            }
        });
    }
}