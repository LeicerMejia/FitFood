package com.example.fitfood.interfaz_dietista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.MainActivity;
import com.example.fitfood.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class activity_editar_perfil extends AppCompatActivity {
    EditText nombre, apellido, usuario_et, clave;
    TextView tv_nombre, tv_apellido, tv_dietas_total, tv_dietas_seguidas, tv_email;
    Toolbar toolbar;
    SharedPreferences preferences;
    BaseDeDatos bd;
    Window window;
    Button actualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        nombre = (EditText) findViewById(R.id.et_nombre_actualizar);
        apellido = (EditText) findViewById(R.id.et_apellido_actualizar);
        usuario_et = (EditText) findViewById(R.id.et_usuario_actualizar);
        clave = (EditText) findViewById(R.id.et_clave_actualizar);
        tv_nombre = (TextView) findViewById(R.id.tv_nombre_actualizar);
        tv_apellido = (TextView) findViewById(R.id.tv_apellido_actualizar);
        tv_dietas_total = (TextView) findViewById(R.id.tv_dietas_subidas_up);
        tv_dietas_seguidas = (TextView) findViewById(R.id.tv_dietas_seguidas_up);
        tv_email = (TextView) findViewById(R.id.tv_correo_actualizar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_editar_perfil);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity_editar_perfil.this);
        window = getWindow();
        actualizar = (Button) findViewById(R.id.btn_actualizar_perfil);
        bd = new BaseDeDatos(activity_editar_perfil.this);
        String user_active = preferences.getString("usuario", null);

        mostrarDatos(user_active);
        obtenerSeguidores(user_active);
        obtener_dietas(user_active);

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase sql = bd.getWritableDatabase();
                Cursor usuario_ = sql.rawQuery("SELECT id_usuario FROM usuarios WHERE usuario = '" + user_active + "'", null);

                if(usuario_.moveToFirst() && usuario_ != null) {
                    int id_actual = usuario_.getInt(0);
                    actualizarUsuario(id_actual, user_active);
                }
            }
        });

        nombre.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent Event) {
                if (Event.getAction() == KeyEvent.ACTION_UP) {
                    tv_nombre.setText(nombre.getText());
                }
                return false;
            }
        });
        apellido.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent Event) {
                if (Event.getAction() == KeyEvent.ACTION_UP) {
                    tv_apellido.setText(apellido.getText());
                }
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                home();
            }
        });
    }

    public void mostrarDatos(String usuario_actual){
        SQLiteDatabase sql = bd.getWritableDatabase();
        Cursor usuario = sql.rawQuery("SELECT * FROM usuarios WHERE usuario = '" + usuario_actual + "'", null);

        if(usuario.moveToFirst() && usuario != null){
            nombre.setText(usuario.getString(1));
            tv_nombre.setText(usuario.getString(1));
            apellido.setText(usuario.getString(2));
            tv_apellido.setText(usuario.getString(2));
            usuario_et.setText(usuario.getString(3));
            tv_email.setText(usuario.getString(4));
            clave.setText(usuario.getString(5));
        } else {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity_editar_perfil.this);
            alertDialogBuilder.setMessage("Se ha producido un error al intentar mostrar el usuario" +
                    " por favor intente nuevamente más tarde");
            alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    home();
                }
            });
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.show();
        }
    }

    public void obtenerSeguidores(String usuarioActivo){
        SQLiteDatabase sql = bd.getWritableDatabase();
        Cursor datos = sql.rawQuery("SELECT COUNT(*) FROM dieta AS d INNER JOIN dietas_usuario AS du ON" +
                " d.id_dieta = du.id_dieta INNER JOIN usuarios AS u ON u.id_usuario = d.id_usuario " +
                "WHERE u.usuario = '" + usuarioActivo + "'", null);

        if(datos.moveToFirst() && datos != null){
            tv_dietas_seguidas.setText(String.valueOf(datos.getInt(0)));
        } else {
            tv_dietas_seguidas.setText("0");
        }
    }

    public void obtener_dietas(String user_actual){
        SQLiteDatabase sql = bd.getWritableDatabase();
        Cursor usuario_ = sql.rawQuery("SELECT id_usuario FROM usuarios WHERE usuario = '" + user_actual + "'", null);

        if(usuario_.moveToFirst() && usuario_ != null) {
           int id_actual = usuario_.getInt(0);
            Cursor datos = sql.rawQuery("SELECT COUNT(*) FROM dieta WHERE id_usuario = '" + id_actual + "'", null);

            if(datos.moveToFirst() && datos != null){
                tv_dietas_total.setText(String.valueOf(datos.getInt(0)));
                Log.i("filas", String.valueOf(datos.getInt(0)));
            } else {
                tv_dietas_total.setText("0");
            }
        }
    }

    public void actualizarUsuario(int id_usuario_actual, String usuario_actual_){
        SQLiteDatabase sql = bd.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor usuario_dato = sql.rawQuery("SELECT * FROM usuarios WHERE usuario = '" + usuario_actual_ + "' " +
                "AND NOT id_usuario = '" + id_usuario_actual + "'", null);

        if(usuario_dato.moveToFirst() && usuario_dato != null){
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity_editar_perfil.this);
            alertDialogBuilder.setMessage("Oops parece que este usuario ya se encuentra en uso");
            alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.show();
            usuario_et.setText("Usuario en uso");
        } else {
            contentValues.put("nombre", nombre.getText().toString().trim());
            contentValues.put("apellido", apellido.getText().toString().trim());
            contentValues.put("usuario", usuario_et.getText().toString().trim());
            contentValues.put("clave", clave.getText().toString().trim());
            int actualizar = sql.update("usuarios", contentValues, "id_usuario = '" + id_usuario_actual + "'", null);

            if(actualizar > 0){
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity_editar_perfil.this);
                alertDialogBuilder.setMessage("El usuario se actualizo exitosamente, a continuación se cerrará la sesión por seguridad");
                alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove("usuario");
                        editor.remove("clave");
                        editor.remove("rol");
                        editor.apply();
                        Intent dietista = new Intent(activity_editar_perfil.this, MainActivity.class);
                        startActivity(dietista);
                        finish();
                    }
                });
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.show();
            } else {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity_editar_perfil.this);
                alertDialogBuilder.setMessage("Se ha producido un error al intentar actualizar, por favor intente" +
                        " nuevamente más tarde");
                alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.show();
            }
        }
    }

    public void home(){
        Intent volver = new Intent(activity_editar_perfil.this, activity_principal_dietista.class);
        startActivity(volver);
        finish();
    }

    @Override
    public void onBackPressed(){
        home();
    }

}