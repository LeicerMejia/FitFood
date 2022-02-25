package com.example.fitfood;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.EditText;
import com.example.fitfood.interfaz_dietista.activity_principal_dietista;
import com.example.fitfood.interfaz_consumidor.activity_principal_comsumidor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
    Button btn_login, btn_registrar, btn_olvidar;
    EditText et_usuario, et_password;
    Window window;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_registrar = (Button) findViewById(R.id.btn_registrar);
        btn_olvidar = (Button) findViewById(R.id.btn_olvidar);
        et_usuario = (EditText) findViewById(R.id.et_usuario);
        et_password = (EditText) findViewById(R.id.et_password);
        window = getWindow();
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));
        loginPreferencias();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario = et_usuario.getText().toString().trim();
                String clave = et_password.getText().toString().trim();
                int validar = 0;

                if(usuario.equals("")){
                    et_usuario.setError("Debe ingresar un usuario");
                    validar++;
                }
                if(clave.equals("")){
                    et_password.setError("Debe ingresar una contraseña");
                    validar++;
                }

                if(validar == 0){
                    BaseDeDatos baseDeDatos = new BaseDeDatos(MainActivity.this);
                    SQLiteDatabase sqLiteDatabase = baseDeDatos.getReadableDatabase();
                    Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM usuarios WHERE usuario = '" + usuario +"' AND " +
                            "clave = '" + clave + "'", null);

                    if(cursor.moveToFirst() && cursor != null){
                        String rol = cursor.getString(6);

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("usuario", usuario);
                        editor.putString("clave", clave);
                        editor.putString("rol", rol);
                        editor.commit();

                        switch (rol){
                            case "Consumidor":
                                Intent consumidor = new Intent(MainActivity.this, activity_principal_comsumidor.class);
                                startActivity(consumidor);
                                finish();
                                break;
                            case "Dietista":
                                Intent dietista = new Intent(MainActivity.this, activity_principal_dietista.class);
                                startActivity(dietista);
                                finish();
                                break;
                        }

                    } else {
                        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(MainActivity.this);
                        alert.setMessage("Usuario o contraseña incorrecta, verifique sus datos e intente nuevamente");
                        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                btn_olvidar.setVisibility(View.VISIBLE);
                            }
                        });
                        alert.show();
                    }

                }

            }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrar = new Intent(MainActivity.this, activity_registro.class);
                startActivity(registrar);
                finish();
            }
        });

        btn_olvidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent olvidar = new Intent(MainActivity.this, activity_recuperacion.class);
                startActivity(olvidar);
                finish();
            }
        });
    }

    public void loginPreferencias(){
        String usuario_preferences = preferences.getString("usuario", null);
        String password_preferences = preferences.getString("clave", null);
        String rol_preferences = preferences.getString("rol", null);

        if (usuario_preferences != null && password_preferences != null && rol_preferences != null){

            switch (rol_preferences){
                case "Consumidor":
                    Intent consumidor = new Intent(MainActivity.this, activity_principal_comsumidor.class);
                    startActivity(consumidor);
                    finish();
                    break;
                case "Dietista":
                    Intent dietista = new Intent(MainActivity.this, activity_principal_dietista.class);
                    startActivity(dietista);
                    finish();
                    break;
            }
        }
    }
}