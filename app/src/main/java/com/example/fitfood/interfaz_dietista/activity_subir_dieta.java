package com.example.fitfood.interfaz_dietista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.R;
import com.example.fitfood.activity_registro;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class activity_subir_dieta extends AppCompatActivity {
    EditText descripcion;
    AutoCompleteTextView tipo_imc, tipo_comida;
    Button subir, volver;
    BaseDeDatos bd;
    Window window;
    ImageView img;
    ConstraintLayout view;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_dieta);

        descripcion = (EditText) findViewById(R.id.et_descripcion);
        tipo_imc = (AutoCompleteTextView) findViewById(R.id.et_indicacion);
        tipo_comida = (AutoCompleteTextView) findViewById(R.id.et_tipo_comida);
        subir = (Button) findViewById(R.id.btn_subir_dieta);
        volver = (Button) findViewById(R.id.btn_cancelar_dieta);
        img = (ImageView) findViewById(R.id.iv_icon);
        view = (ConstraintLayout) findViewById(R.id.view_subir);
        bd = new BaseDeDatos(activity_subir_dieta.this);
        window = getWindow();
        preferences = PreferenceManager.getDefaultSharedPreferences(activity_subir_dieta.this);

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        String items_imc [] = new String[] {"Bajo peso", "Normal", "Sobrepeso", "Obesidad leve", "Obesidad mórbida"};
        String items_tipo_comida [] = new String[] {"Desayuno", "Merienda mañana", "Almuerzo", "Merienda tarde", "Cena"};

        ArrayAdapter<String> adapter_imc = new ArrayAdapter<>(activity_subir_dieta.this, R.layout.list_item, items_imc);
        ArrayAdapter<String> adapter_tipo_comida = new ArrayAdapter<>(activity_subir_dieta.this, R.layout.list_item,
                items_tipo_comida);

        tipo_imc.setAdapter(adapter_imc);
        tipo_comida.setAdapter(adapter_tipo_comida);

        tipo_comida.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = tipo_comida.getText().toString().trim();

                switch (item){
                    case "":
                        img.setImageResource(R.drawable.photo);
                        break;
                    case "Desayuno":
                        img.setImageResource(R.drawable.breakfast);
                        break;
                    case "Merienda mañana":
                        img.setImageResource(R.drawable.snacks2);
                        break;
                    case "Almuerzo":
                        img.setImageResource(R.drawable.lunch);
                        break;
                    case "Merienda tarde":
                        img.setImageResource(R.drawable.snacks);
                        break;
                    case "Cena":
                        img.setImageResource(R.drawable.dinner);
                        break;
                }
            }
        });


        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imc = tipo_imc.getText().toString().trim();
                String comida = tipo_comida.getText().toString().trim();
                String descripcion_text = descripcion.getText().toString().trim();
                String usuario = preferences.getString("usuario", null);
                int validar = 0;

                if(imc.equals("")){
                    tipo_imc.setError("Debes seleccionar un item");
                    validar++;
                }
                if(comida.equals("")){
                    tipo_comida.setError("Debes seleccionar un item");
                    validar++;
                }
                if(descripcion_text.equals("")){
                    descripcion.setError("Debes escribir una descripcion");
                    validar++;
                }

                if(validar == 0){
                    SQLiteDatabase sql = bd.getWritableDatabase();
                    Cursor dato = sql.rawQuery("SELECT id_usuario FROM usuarios WHERE usuario = '" + usuario + "'", null);
                    ContentValues contentValues = new ContentValues();

                    if(dato.moveToFirst() && dato != null){
                        String id = dato.getString(0);

                        contentValues.put("id_usuario", id);
                        contentValues.put("indicacion", imc);
                        contentValues.put("tipo_comida", comida);
                        contentValues.put("descripcion", descripcion_text);
                        sql.insert("dieta", null, contentValues);

                        MaterialAlertDialogBuilder alerta = new MaterialAlertDialogBuilder(activity_subir_dieta.this);
                        alerta.setMessage("La dieta se ha subido exitosamente. \n\n ¿Desea subir otra?");
                        alerta.setPositiveButton("Subir otra", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                tipo_comida.setText("");
                                tipo_imc.setText("");
                                descripcion.setText("");
                            }
                        });
                        alerta.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent volver = new Intent(activity_subir_dieta.this, activity_principal_dietista.class);
                                startActivity(volver);
                                finish();
                            }
                        });
                        alerta.setCancelable(false);
                        alerta.show();
                    }
                } else {
                    Snackbar.make(view, "Se encontraron campos vacios", Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                }


            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent volver = new Intent(activity_subir_dieta.this, activity_principal_dietista.class);
                startActivity(volver);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent volver = new Intent(activity_subir_dieta.this, activity_principal_dietista.class);
        startActivity(volver);
        finish();
    }
}