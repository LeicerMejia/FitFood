package com.example.fitfood;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BaseDeDatos extends SQLiteOpenHelper {
    private  static final String DB_NOM = "FitFood.sqlie";
    private static final int VERSION = 2;

    private static final String CREATE_TABLE = "create table usuarios(id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nombre TEXT, apellido TEXT, usuario TEXT, correo TEXT, clave TEXT, rol TEXT)";

    private static final String CREATE_TABLE2 = "create table dieta(id_dieta INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "id_usuario INTEGER, indicacion TEXT, tipo_comida TEXT, descripcion TEXT, " +
            "FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario))";

    private static final String CREATE_TABLE3 = "create table dietas_usuario(id_dietas_usuario INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "id_dieta INTEGER, id_usuario INTEGER, id_dietista INTEGER, FOREIGN KEY(id_dieta) REFERENCES dieta(id_dieta), " +
            "FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario), " +
            "FOREIGN KEY(id_dietista) REFERENCES dieta(id_usuario))";

    public BaseDeDatos(@Nullable Context context) {
        super(context, DB_NOM, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE2);
        sqLiteDatabase.execSQL(CREATE_TABLE3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists usuarios");
        sqLiteDatabase.execSQL("drop table if exists dieta");
        sqLiteDatabase.execSQL("drop table if exists dietas_usuario");
        onCreate(sqLiteDatabase);
    }
}
