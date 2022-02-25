package com.example.fitfood.interfaz_consumidor;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.MainActivity;
import com.example.fitfood.R;
import com.example.fitfood.interfaz_dietista.activity_principal_dietista;
import com.example.fitfood.interfaz_dietista.activity_ver_dietas;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class activity_principal_comsumidor extends AppCompatActivity {
    CardView editar_perfil, ver_dietas_guardadas, ver_dietas, calcular_imc, logout;
    TextView nombre, seguidores, transcripcion;
    BaseDeDatos base;
    String usuario_actual;
    Window window;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_comsumidor);

        ver_dietas_guardadas = (CardView) findViewById(R.id.ver_dietas_guardadas);
        editar_perfil = (CardView) findViewById(R.id.editar_perfil_consumidor);
        ver_dietas = (CardView) findViewById(R.id.ver_listado_de_dietas);
        calcular_imc = (CardView) findViewById(R.id.calcula_imc);

        logout = (CardView) findViewById(R.id.logout_consumidor);
        nombre = (TextView) findViewById(R.id.tv_nombre_consumidor);
        seguidores = (TextView) findViewById(R.id.tv_guardadas);
        transcripcion = (TextView) findViewById(R.id.tv_transcripcion);
        window = getWindow();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity_principal_comsumidor.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        base = new BaseDeDatos(activity_principal_comsumidor.this);
        usuario_actual = sharedPreferences.getString("usuario", null);

        obtenerNombre(usuario_actual);
        obtenerIMC();

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        ver_dietas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imc_string = sharedPreferences.getString("imc", null);
                if(imc_string != null){
                    Intent ver = new Intent(activity_principal_comsumidor.this, activity_listado_de_dietas.class);
                    startActivity(ver);
                    finish();
                } else {
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity_principal_comsumidor.this);
                    alert.setTitle("IMC sin calcular");
                    alert.setMessage("Para ver las dietas debes calcular primero tu IMC, así sabremos que " +
                            "dietas recomendarte");
                    alert.setPositiveButton("Calcular IMC", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            calcularIMC();
                        }
                    });
                    alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                }
            }
        });

        calcular_imc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularIMC();
            }
        });

        editar_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent perfil = new Intent(activity_principal_comsumidor.this, activity_editar_perfil.class);
                startActivity(perfil);
                finish();
            }
        });

        ver_dietas_guardadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent die = new Intent(activity_principal_comsumidor.this, activity_dietas_guardadas.class);
                startActivity(die);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(activity_principal_comsumidor.this);
                alertDialogBuilder.setMessage("¿Esta seguro que desea cerrar la sesión?");
                alertDialogBuilder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editor.remove("usuario");
                        editor.remove("clave");
                        editor.remove("rol");
                        editor.remove("imc");
                        editor.remove("transcripcion");
                        editor.apply();
                        Intent dietista = new Intent(activity_principal_comsumidor.this, MainActivity.class);
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

    }

    public void obtenerNombre(String usuario_activo){
        SQLiteDatabase sql = base.getWritableDatabase();
        Cursor datos = sql.rawQuery("SELECT nombre, apellido FROM usuarios WHERE usuario = '" + usuario_activo + "'", null);

        if(datos.moveToFirst() && datos != null){
            nombre.setText(datos.getString(0) + " " + datos.getString(1));
        }
    }

    public void obtenerIMC(){
        String imc_string = sharedPreferences.getString("imc", null);
        if(imc_string != null){
            float imc = Float.parseFloat(imc_string);
            seguidores.setText("IMC: " + String.valueOf(imc));

            if(imc < 20){
                transcripcion.setText("Bajo peso");
            } else if (imc >= 20 && imc <= 24.9){
                transcripcion.setText("Normal");
            } else if (imc >= 25 && imc <= 29.9){
                transcripcion.setText("Obesidad leve");
            } else if (imc >= 30.0 && imc <= 40){
                transcripcion.setText("Obesidad severa");
            } else if (imc > 40){
                transcripcion.setText("Obesidad mórbida");
            }
        } else {
            seguidores.setText("IMC: sin calcular");
            transcripcion.setText("");
        }
    }

    public void calcularIMC(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity_principal_comsumidor.this);
        bottomSheetDialog.setContentView(R.layout.activity_calcular_imc);
        EditText altura = bottomSheetDialog.findViewById(R.id.et_altura);
        EditText peso = bottomSheetDialog.findViewById(R.id.et_peso);
        Button btn_calcular = bottomSheetDialog.findViewById(R.id.btn_calcularImc);

        btn_calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String transcripcion_ = "";

                if(altura.getText().toString().trim().equals("") || peso.getText().toString().trim().equals("")){
                    bottomSheetDialog.dismiss();
                    Toast.makeText(activity_principal_comsumidor.this, "Debe llenar todos los campos",
                            Toast.LENGTH_SHORT).show();
                } else {
                    double talla = Double.parseDouble(altura.getText().toString().trim());
                    double masa = Double.parseDouble(peso.getText().toString().trim());

                    double imc_calculado = ((masa)/(talla*talla));
                    SharedPreferences.Editor edit = sharedPreferences.edit();

                    bottomSheetDialog.dismiss();

                    if(imc_calculado < 20){
                        transcripcion_ = "Bajo peso";
                    } else if (imc_calculado >= 20 && imc_calculado <= 24.9){
                        transcripcion_ = "Normal";
                    } else if (imc_calculado >= 25 && imc_calculado <= 29.9){
                        transcripcion_ = "Obesidad leve";
                    } else if (imc_calculado >= 30.0 && imc_calculado <= 40){
                        transcripcion_ = "Obesidad severa";
                    } else if (imc_calculado > 40){
                        transcripcion_ = "Obesidad mórbida";
                    }

                    edit.putString("imc", String.valueOf(imc_calculado));
                    edit.putString("transcripcion", transcripcion_);
                    edit.commit();

                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity_principal_comsumidor.this);
                    alert.setTitle("IMC");
                    alert.setMessage("A continuación se muestran los resultados \n\n" +
                            "IMC: " + imc_calculado + "\n" +
                            "Transcripción: " + transcripcion_ + "\n\n" +
                            "Ya puede ver las dietas recomendadas para su IMC");
                    alert.setPositiveButton("Ver dietas", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent ver = new Intent(activity_principal_comsumidor.this, activity_listado_de_dietas.class);
                            startActivity(ver);
                            finish();
                        }
                    });
                    alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(getIntent());
                            finish();
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                }
            }
        });
        bottomSheetDialog.show();
    }
}