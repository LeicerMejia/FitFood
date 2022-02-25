package com.example.fitfood.interfaz_consumidor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.R;
import com.example.fitfood.interfaz_consumidor.clases.adaptador_ver_dietas_consumidor;
import com.example.fitfood.interfaz_consumidor.clases.items_ver_dietas_consumidor;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class activity_listado_de_dietas extends AppCompatActivity {
    RecyclerView recicler;
    adaptador_ver_dietas_consumidor avdc;
    items_ver_dietas_consumidor ivdc;
    ImageButton volver;
    Button calcular_imc;
    LinearLayout vacio;
    TextView imc, transcripcion;
    BaseDeDatos bd;
    SharedPreferences preferences;
    Window window;
    ArrayList<items_ver_dietas_consumidor> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_de_dietas);

        recicler = (RecyclerView) findViewById(R.id.recicler_lista_dietas_total);
        arrayList = new ArrayList<>();
        avdc = new adaptador_ver_dietas_consumidor(arrayList, activity_listado_de_dietas.this);
        volver = (ImageButton) findViewById(R.id.ib_volver_dieta_list);
        calcular_imc = (Button) findViewById(R.id.btn_volver_a_calcular);
        vacio = (LinearLayout) findViewById(R.id.linear_no_pub);
        imc = (TextView) findViewById(R.id.tv_imc_lista_dieta);
        transcripcion = (TextView) findViewById(R.id.tv_transcripcion_lista_dieta);
        bd = new BaseDeDatos(activity_listado_de_dietas.this);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity_listado_de_dietas.this);
        window = getWindow();

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        recicler.setLayoutManager(new LinearLayoutManager(activity_listado_de_dietas.this));
        recicler.setAdapter(avdc);

        mostrarDatos();
        mostrarLista();

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent volver = new Intent(activity_listado_de_dietas.this, activity_principal_comsumidor.class);
                startActivity(volver);
                finish();
            }
        });
        calcular_imc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity_listado_de_dietas.this);
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
                            Toast.makeText(activity_listado_de_dietas.this, "Debe llenar todos los campos",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            double talla = Double.parseDouble(altura.getText().toString().trim());
                            double masa = Double.parseDouble(peso.getText().toString().trim());

                            double imc_calculado = ((masa)/(talla*talla));
                            SharedPreferences.Editor edit = preferences.edit();

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

                            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity_listado_de_dietas.this);
                            alert.setTitle("IMC");
                            alert.setMessage("A continuación se muestran los resultados \n\n" +
                                    "IMC: " + imc_calculado + "\n" +
                                    "Transcripción: " + transcripcion_);
                            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
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
        });
    }

    public void mostrarDatos(){
        String imc_ = preferences.getString("imc", null);
        String transcripcion_ = preferences.getString("transcripcion", null);

        imc.setText("IMC: " + imc_);
        transcripcion.setText("Transcripción: " + transcripcion_);
    }

    public void mostrarLista(){
        String transcripcion_s = preferences.getString("transcripcion", null);
        SQLiteDatabase sql = bd.getReadableDatabase();
        Cursor datos = sql.rawQuery("SELECT d.id_dieta, d.indicacion, d.tipo_comida, d.descripcion, u.nombre, " +
                "u.apellido FROM dieta AS d INNER JOIN usuarios AS u ON u.rol = 'Dietista' AND " +
                "u.id_usuario = d.id_usuario WHERE d.indicacion = '" + transcripcion_s + "'" , null);
        if(datos.getCount() > 0){
            recicler.setVisibility(View.VISIBLE);
            vacio.setVisibility(View.GONE);

            while (datos.moveToNext()){
                ivdc = new items_ver_dietas_consumidor();
                ivdc.setId(datos.getInt(0));
                ivdc.setTipo_imc(datos.getString(1));
                ivdc.setTipo_comida(datos.getString(2));
                ivdc.setDescripcion(datos.getString(3));
                ivdc.setNombre(datos.getString(4) + " " + datos.getString(5));
                switch (datos.getString(2)){
                    case "":
                        ivdc.setImagen(R.drawable.photo);
                        break;
                    case "Desayuno":
                        ivdc.setImagen(R.drawable.breakfast);
                        break;
                    case "Merienda mañana":
                        ivdc.setImagen(R.drawable.snacks2);
                        break;
                    case "Almuerzo":
                        ivdc.setImagen(R.drawable.lunch);
                        break;
                    case "Merienda tarde":
                        ivdc.setImagen(R.drawable.snacks);
                        break;
                    case "Cena":
                        ivdc.setImagen(R.drawable.dinner);
                        break;
                }
                arrayList.add(ivdc);
            }

        } else {
            recicler.setVisibility(View.GONE);
            vacio.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed(){
        Intent volver = new Intent(activity_listado_de_dietas.this, activity_principal_comsumidor.class);
        startActivity(volver);
        finish();
    }
}