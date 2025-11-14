package com.example.proyectolavacar;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {

    Activity activity;
    String[] titles;
    int[] icons;

    public MenuAdapter(Activity activity, String[] titles, int[] icons) {
        this.activity = activity;
        this.titles = titles;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.menu_item, null);

        ImageView img = view.findViewById(R.id.imgIcon);
        TextView txt = view.findViewById(R.id.tvTitle);

        img.setImageResource(icons[position]);
        txt.setText(titles[position]);

        return view;
    }
}
