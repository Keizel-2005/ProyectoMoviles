package com.example.proyectolavacar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CarroAdapter extends BaseAdapter {

    Activity activity;
    ArrayList<String> placas;
    ArrayList<String> modelos;
    ArrayList<String> anos;

    public CarroAdapter(Activity activity, ArrayList<String> placas, ArrayList<String> modelos, ArrayList<String> anos) {
        this.activity = activity;
        this.placas = placas;
        this.modelos = modelos;
        this.anos = anos;
    }

    @Override
    public int getCount() {
        return placas.size();
    }

    @Override
    public Object getItem(int position) {
        return placas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_carro, null);

        TextView txtPlaca = view.findViewById(R.id.txtItemPlaca);
        TextView txtModelo = view.findViewById(R.id.txtItemModelo);
        TextView txtAno = view.findViewById(R.id.txtItemAno);

        txtPlaca.setText("Placa: " + placas.get(position));
        txtModelo.setText("Modelo: " + modelos.get(position));
        txtAno.setText("Año: " + anos.get(position));

        return view;
    }
}