package com.example.proyectolavacar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminBD extends SQLiteOpenHelper {

    public AdminBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys=ON");

        db.execSQL("CREATE TABLE Cliente (cedula TEXT PRIMARY KEY, nombre TEXT, apellidos TEXT, telefono TEXT, correo TEXT)");

        db.execSQL("CREATE TABLE Empleados (cedula TEXT PRIMARY KEY, nombre TEXT, apellidos TEXT, telefono TEXT, correo TEXT, puesto TEXT)");

        db.execSQL("CREATE TABLE Servicio (idServicio INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, precio INTEGER)");

        db.execSQL("CREATE TABLE Carro (placa TEXT PRIMARY KEY, modelo TEXT, anio INTEGER, cedulaCliente TEXT, cedulaEmpleado TEXT, FOREIGN KEY(cedulaCliente) REFERENCES Cliente(cedula), FOREIGN KEY(cedulaEmpleado) REFERENCES Empleados(cedula))");

        db.execSQL("CREATE TABLE EncabezadoFactura (idFactura INTEGER PRIMARY KEY AUTOINCREMENT, fecha TEXT, cedulaCliente TEXT, placaCarro INTEGER, cedulaEmpleado TEXT, total INTEGER, FOREIGN KEY(cedulaCliente) REFERENCES Cliente(cedula), FOREIGN KEY(placaCarro) REFERENCES Carro(placa), FOREIGN KEY(cedulaEmpleado) REFERENCES Empleados(cedula))");

        db.execSQL("CREATE TABLE DetalleFactura (idDetalle INTEGER PRIMARY KEY AUTOINCREMENT, idFactura INTEGER, idServicio INTEGER, cantidad INTEGER, precio DECIMAL, subtotal DECIMAL, FOREIGN KEY(idFactura) REFERENCES EncabezadoFactura(idFactura), FOREIGN KEY(idServicio) REFERENCES Servicio(idServicio))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DetalleFactura");
        db.execSQL("DROP TABLE IF EXISTS EncabezadoFactura");
        db.execSQL("DROP TABLE IF EXISTS Carro");
        db.execSQL("DROP TABLE IF EXISTS Servicio");
        db.execSQL("DROP TABLE IF EXISTS Empleados");
        db.execSQL("DROP TABLE IF EXISTS Cliente");
        onCreate(db);
    }
}
