package com.example.fitfood.interfaz_dietista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.MainActivity;
import com.example.fitfood.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class activity_principal_dietista extends AppCompatActivity {
    CardView editar_perfil, subir_dietas, ver_dietas, ver_seguidores, logout;
    TextView nombre, seguidores;
    BaseDeDatos base;
    String usuario_actual;
    Window window;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_dietista);

        subir_dietas = (CardView) findViewById(R.id.subir_dieta);
        editar_perfil = (CardView) findViewById(R.id.editar_perfil);
        ver_dietas = (CardView) findViewById(R.id.ver_dietas);
        ver_seguidores = (CardView) findViewById(R.id.ver_seguidores);
        nombre = (TextView) findViewById(R.id.tv_nombre_dietista);
        seguidores = (TextView) findViewById(R.id.tv_seguidores);
        logout = (CardView) findViewById(R.id.logout);
        window = getWindow();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity_principal_dietista.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        base = new BaseDeDatos(activity_principal_dietista.this);
        usuario_actual = sharedPreferences.getString("usuario", null);

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        obtenerNombre(usuario_actual);
        obtenerSeguidores(usuario_actual);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity_principal_dietista.this);
                alertDialogBuilder.setMessage("¿Esta seguro que desea cerrar la sesión?");
                alertDialogBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editor.remove("usuario");
                        editor.remove("clave");
                        editor.remove("rol");
                        editor.apply();
                        Intent dietista = new Intent(activity_principal_dietista.this, MainActivity.class);
                        startActivity(dietista);
                        finish();
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialogBuilder.show();
            }
        });

        ver_seguidores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent seguidores = new Intent(activity_principal_dietista.this, activity_ver_seguidores.class);
                startActivity(seguidores);
                finish();
            }
        });

        subir_dietas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent subir = new Intent(activity_principal_dietista.this, activity_subir_dieta.class);
                startActivity(subir);
                finish();
            }
        });

        editar_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editar = new Intent(activity_principal_dietista.this, activity_editar_perfil.class);
                startActivity(editar);
                finish();
            }
        });

        ver_dietas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ver = new Intent(activity_principal_dietista.this, activity_ver_dietas.class);
                startActivity(ver);
                finish();
            }
        });
    }

    public void obtenerNombre(String usuario_activo){
        SQLiteDatabase sql = base.getWritableDatabase();
        Cursor datos = sql.rawQuery("SELECT nombre, apellido FROM usuarios WHERE usuario = '" + usuario_activo + "'", null);

        if(datos.moveToFirst() && datos != null){
            nombre.setText(datos.getString(0) + " " + datos.getString(1));
        }
    }

    public void obtenerSeguidores(String usuarioActivo){
        SQLiteDatabase sql = base.getWritableDatabase();
        Cursor datos = sql.rawQuery("SELECT COUNT(*) FROM dieta AS d INNER JOIN dietas_usuario AS du ON" +
                " d.id_dieta = du.id_dieta INNER JOIN usuarios AS u ON u.id_usuario = d.id_usuario " +
                "WHERE u.usuario = '" + usuarioActivo + "'", null);

        if(datos.moveToFirst() && datos != null){
            seguidores.setText(String.valueOf(datos.getInt(0)) + " dietas seguidas");
        } else {
            seguidores.setText("0 dietas seguidas");
        }
    }
}