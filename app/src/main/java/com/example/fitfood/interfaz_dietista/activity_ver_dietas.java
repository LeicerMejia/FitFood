package com.example.fitfood.interfaz_dietista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.example.fitfood.interfaz_dietista.clases.adaptador_ver_dieta;
import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.R;
import com.example.fitfood.interfaz_dietista.clases.items_ver_dieta;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class activity_ver_dietas extends AppCompatActivity {
    RecyclerView recicler_lista;
    ImageButton ib_volver;
    ArrayList<items_ver_dieta> lista_dietas;
    BaseDeDatos bd;
    adaptador_ver_dieta adaptadorVerDietas;
    SharedPreferences preferences;
    String usuario_actual;
    Cursor cursor;
    LinearLayout empty;
    ConstraintLayout view;
    Button subir_primera_dieta;
    items_ver_dieta items_lista;
    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_dietas);

        recicler_lista = (RecyclerView) findViewById(R.id.recicler_ver_dietas);
        ib_volver = (ImageButton) findViewById(R.id.ib_volver);
        lista_dietas = new ArrayList<>();
        bd = new BaseDeDatos(activity_ver_dietas.this);
        adaptadorVerDietas = new adaptador_ver_dieta(lista_dietas, activity_ver_dietas.this);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity_ver_dietas.this);
        empty = (LinearLayout) findViewById(R.id.linear_visibility_ver_dietas);
        view = (ConstraintLayout) findViewById(R.id.view_ver_dietas);
        subir_primera_dieta = (Button) findViewById(R.id.btn_subir_primera_dieta);
        window = getWindow();

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        usuario_actual = preferences.getString("usuario", null);
        recicler_lista.setLayoutManager(new LinearLayoutManager(activity_ver_dietas.this));
        recicler_lista.setAdapter(adaptadorVerDietas);

        mostrarLista();

        subir_primera_dieta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent subir = new Intent(activity_ver_dietas.this, activity_subir_dieta.class);
                startActivity(subir);
                finish();
            }
        });
        ib_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent subir = new Intent(activity_ver_dietas.this, activity_principal_dietista.class);
                startActivity(subir);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent subir = new Intent(activity_ver_dietas.this, activity_principal_dietista.class);
        startActivity(subir);
        finish();
    }

    public void mostrarLista(){
        SQLiteDatabase sql = bd.getWritableDatabase();
        cursor = sql.rawQuery("SELECT du.id_dieta, du.indicacion, du.tipo_comida, du.descripcion FROM usuarios AS u " +
                "INNER JOIN dieta AS du ON" +
                " u.id_usuario = du.id_usuario WHERE u.usuario = '" + usuario_actual + "'", null);

        if(cursor.getCount() > 0){
            recicler_lista.setVisibility(View.VISIBLE);
            view.setBackgroundColor(Color.parseColor("#54FAD992"));
            empty.setVisibility(View.GONE);
            while (cursor.moveToNext()){
                items_lista = new items_ver_dieta();
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
                lista_dietas.add(items_lista);
            }
        } else {
            recicler_lista.setVisibility(View.GONE);
            view.setBackgroundColor(Color.parseColor("#ffffff"));
            empty.setVisibility(View.VISIBLE);
        }
    }
}