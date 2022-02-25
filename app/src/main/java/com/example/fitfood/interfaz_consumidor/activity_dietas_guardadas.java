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
import com.example.fitfood.interfaz_consumidor.clases.Adaptador_dietas_guardadas;
import com.example.fitfood.interfaz_consumidor.clases.adaptador_ver_dietas_consumidor;
import com.example.fitfood.interfaz_consumidor.clases.items_dietas_guardadas;
import com.example.fitfood.interfaz_dietista.clases.items_ver_dieta;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;


public class activity_dietas_guardadas extends AppCompatActivity {
    RecyclerView recicler;
    Adaptador_dietas_guardadas avdc;
    items_dietas_guardadas items_lista;
    ImageButton volver;
    LinearLayout vacio;
    TextView imc, transcripcion;
    BaseDeDatos bd;
    SharedPreferences preferences;
    Window window;
    ArrayList<items_dietas_guardadas> arrayList;
    Button primera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietas_guardadas);

        recicler = (RecyclerView) findViewById(R.id.recicler_lista_dietas_guardadas);
        arrayList = new ArrayList<>();
        avdc = new Adaptador_dietas_guardadas(arrayList, activity_dietas_guardadas.this);
        volver = (ImageButton) findViewById(R.id.ib_volver_dieta_list_dr);
        vacio = (LinearLayout) findViewById(R.id.linear_no_pub_dr);
        imc = (TextView) findViewById(R.id.tv_imc_lista_dieta_dr);
        transcripcion = (TextView) findViewById(R.id.tv_transcripcion_lista_dieta_dr);
        bd = new BaseDeDatos(activity_dietas_guardadas.this);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity_dietas_guardadas.this);
        window = getWindow();
        primera = (Button) findViewById(R.id.btn_guardar_primera_dieta);

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        recicler.setLayoutManager(new LinearLayoutManager(activity_dietas_guardadas.this));
        recicler.setAdapter(avdc);

        primera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imc_string = preferences.getString("imc", null);
                if(imc_string != null){
                    Intent ver = new Intent(activity_dietas_guardadas.this, activity_listado_de_dietas.class);
                    startActivity(ver);
                    finish();
                } else {
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity_dietas_guardadas.this);
                    alert.setTitle("IMC sin calcular");
                    alert.setMessage("Para guardar tu primera dieta debes calcular tu IMC, así sabremos que " +
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
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent volver = new Intent(activity_dietas_guardadas.this, activity_principal_comsumidor.class);
                startActivity(volver);
                finish();
            }
        });

        mostrarDatos();
        listar();

    }

    @Override
    public void onBackPressed(){
        Intent volver = new Intent(activity_dietas_guardadas.this, activity_principal_comsumidor.class);
        startActivity(volver);
        finish();
    }

    public void mostrarDatos(){
        String imc_ = preferences.getString("imc", null);
        String transcripcion_ = preferences.getString("transcripcion", null);

        if(imc_ != null && transcripcion_ != null){
            imc.setText("IMC: " + imc_);
            transcripcion.setText("Transcripción: " + transcripcion_);
        } else {
            imc.setText("IMC: sin calcular");
            transcripcion.setText("Transcripción: sin calcular");
        }
    }

    public void listar(){
        SQLiteDatabase sql = bd.getReadableDatabase();
        Cursor dato_usuario = sql.rawQuery("SELECT id_usuario FROM usuarios WHERE usuario = '"
                + preferences.getString("usuario", null) + "'", null);
        if(dato_usuario.moveToFirst() && dato_usuario != null){
            String id_usuario = String.valueOf(dato_usuario.getInt(0));

            Cursor cursor = sql.rawQuery("SELECT d.id_dieta, d.indicacion, d.tipo_comida, d.descripcion, u.nombre, " +
                    "u.apellido FROM dieta AS d INNER JOIN dietas_usuario AS du ON d.id_dieta = du.id_dieta INNER JOIN " +
                    "usuarios AS u ON du.id_dietista = u.id_usuario WHERE du.id_usuario = '" +
                    id_usuario + "'" , null);

            if(cursor.getCount() > 0){
                vacio.setVisibility(View.GONE);
                recicler.setVisibility(View.VISIBLE);

                while (cursor.moveToNext()){
                    items_lista = new items_dietas_guardadas();
                    switch (cursor.getString(2)){
                        case "":
                            items_lista.setImagen(R.drawable.photo);
                            break;
                        case "Desayuno":
                            items_lista.setImagen(R.drawable.breakfast);
                            break;
                        case "Merienda mañana":
                            items_lista.setImagen(R.drawable.snacks2);
                            break;
                        case "Almuerzo":
                            items_lista.setImagen(R.drawable.lunch);
                            break;
                        case "Merienda tarde":
                            items_lista.setImagen(R.drawable.snacks);
                            break;
                        case "Cena":
                            items_lista.setImagen(R.drawable.dinner);
                            break;
                    }
                    items_lista.setId(cursor.getInt(0));
                    items_lista.setTipo_imc(cursor.getString(1));
                    items_lista.setTipo_comida(cursor.getString(2));
                    items_lista.setDescripcion(cursor.getString(3));
                    items_lista.setNombre(cursor.getString(4) + " " + cursor.getString(5));
                    arrayList.add(items_lista);
                }
            } else {
                vacio.setVisibility(View.VISIBLE);
                recicler.setVisibility(View.GONE);
            }
        }
    }

    public void calcularIMC(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity_dietas_guardadas.this);
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
                    Toast.makeText(activity_dietas_guardadas.this, "Debe llenar todos los campos",
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

                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(activity_dietas_guardadas.this);
                    alert.setTitle("IMC");
                    alert.setMessage("A continuación se muestran los resultados \n\n" +
                            "IMC: " + imc_calculado + "\n" +
                            "Transcripción: " + transcripcion_ + "\n\n" +
                            "Ya puede ver las dietas recomendadas para su IMC");
                    alert.setPositiveButton("Ver dietas", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent ver = new Intent(activity_dietas_guardadas.this, activity_listado_de_dietas.class);
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