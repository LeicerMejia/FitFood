package com.example.fitfood.interfaz_dietista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.R;
import com.example.fitfood.interfaz_dietista.clases.items_lista_seguidores;
import com.example.fitfood.interfaz_dietista.clases.adaptador_lista_seguidores;
import java.util.ArrayList;

public class activity_ver_seguidores extends AppCompatActivity {
    ImageButton volver;
    RecyclerView recicler;
    ArrayList<items_lista_seguidores> lista_seguidores;
    adaptador_lista_seguidores adaptador;
    Window window;
    LinearLayout vacio;
    items_lista_seguidores items_lista_seguidores;
    SharedPreferences preferences;
    BaseDeDatos bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_seguidores);

        volver = (ImageButton) findViewById(R.id.ib_volver_seguidores);
        recicler = (RecyclerView) findViewById(R.id.lista_de_seguidores);
        vacio = (LinearLayout) findViewById(R.id.linear_no_followers);
        lista_seguidores = new ArrayList<>();
        adaptador = new adaptador_lista_seguidores(lista_seguidores, activity_ver_seguidores.this);
        window = getWindow();
        preferences = PreferenceManager.getDefaultSharedPreferences(activity_ver_seguidores.this);

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        recicler.setLayoutManager(new LinearLayoutManager(activity_ver_seguidores.this));
        recicler.setAdapter(adaptador);

        listar();

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent volver = new Intent(activity_ver_seguidores.this, activity_principal_dietista.class);
                startActivity(volver);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent volver = new Intent(activity_ver_seguidores.this, activity_principal_dietista.class);
        startActivity(volver);
        finish();
    }

    public void listar(){
        bd = new BaseDeDatos(activity_ver_seguidores.this);
        SQLiteDatabase sql = bd.getReadableDatabase();
        String usuario = preferences.getString("usuario", null);
        Cursor datos1 = sql.rawQuery("SELECT id_usuario FROM usuarios WHERE usuario = '" + usuario + "'", null);
        if(datos1.moveToFirst() && datos1 != null){
            String id_usuario_dietista = datos1.getString(0);

            Cursor dato = sql.rawQuery("SELECT du.id_usuario, u.tipo_comida, u.descripcion FROM dieta AS u INNER JOIN dietas_usuario " +
                    "AS du ON u.id_dieta = du.id_dieta where du.id_dietista = '" + id_usuario_dietista + "'", null);

            if(dato.getCount() > 0){
                recicler.setVisibility(View.VISIBLE);
                vacio.setVisibility(View.GONE);

                while(dato.moveToNext()){
                    items_lista_seguidores = new items_lista_seguidores();
                    items_lista_seguidores.setId(dato.getInt(0));
                    items_lista_seguidores.setNombre(dato.getString(1));
                    items_lista_seguidores.setUsuario(dato.getString(2));
                    switch (dato.getString(1)){
                        case "":
                            items_lista_seguidores.setImagen(R.drawable.photo);
                            break;
                        case "Desayuno":
                            items_lista_seguidores.setImagen(R.drawable.breakfast);
                            break;
                        case "Merienda mañana":
                            items_lista_seguidores.setImagen(R.drawable.snacks2);
                            break;
                        case "Almuerzo":
                            items_lista_seguidores.setImagen(R.drawable.lunch);
                            break;
                        case "Merienda tarde":
                            items_lista_seguidores.setImagen(R.drawable.snacks);
                            break;
                        case "Cena":
                            items_lista_seguidores.setImagen(R.drawable.dinner);
                            break;
                    }

                    lista_seguidores.add(items_lista_seguidores);
                }

            } else {
                recicler.setVisibility(View.GONE);
                vacio.setVisibility(View.VISIBLE);
            }

        }
    }
}