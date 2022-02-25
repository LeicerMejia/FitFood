package com.example.fitfood.interfaz_dietista.clases;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.R;
import com.example.fitfood.interfaz_dietista.activity_subir_dieta;
import com.example.fitfood.interfaz_dietista.activity_ver_dietas;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class activity_editar_item extends AppCompatActivity {

    EditText descripcion;
    AutoCompleteTextView tipo_imc, tipo_comida;
    ImageView img;
    BaseDeDatos bd;
    SQLiteDatabase sql;
    Window window;
    String id_dieta;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_dialog_editar_dieta);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_editar_dieta);
        descripcion = (EditText) findViewById(R.id.et_descripcion_editar);
        tipo_imc = (AutoCompleteTextView) findViewById(R.id.et_indicacion_editar);
        tipo_comida = (AutoCompleteTextView) findViewById(R.id.et_tipo_comida_editar);
        img = (ImageView) findViewById(R.id.iv_icon_editar);
        window = getWindow();
        id_dieta = getIntent().getStringExtra("id_de_la_dieta");

        window.setStatusBarColor(Color.parseColor("#FFFF6900"));

        toolbar.setTitle("Editar dieta");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setSupportActionBar(toolbar);

        String items_imc [] = new String[] {"Bajo peso", "Normal", "Sobrepeso", "Obesidad leve", "Obesidad mórbida"};
        String items_tipo_comida [] = new String[] {"Desayuno", "Merienda mañana", "Almuerzo", "Merienda tarde", "Cena"};

        ArrayAdapter<String> adapter_imc = new ArrayAdapter<>(activity_editar_item.this, R.layout.list_item, items_imc);
        ArrayAdapter<String> adapter_tipo_comida = new ArrayAdapter<>(activity_editar_item.this, R.layout.list_item,
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

        bd = new BaseDeDatos(activity_editar_item.this);
        sql = bd.getWritableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM dieta WHERE id_dieta = '" + id_dieta + "'", null);

        if(cursor.moveToFirst() && cursor != null){
            tipo_imc.setText(cursor.getString(2), false);
            tipo_comida.setText(cursor.getString(3), false);
            descripcion.setText(cursor.getString(4));

            String items = tipo_comida.getText().toString().trim();

            switch (items){
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
    }

    @Override
    public void onBackPressed(){
        dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_screen_dialog_edit, menu);
        iconColor(menu, R.color.white);
        return true;
    }

    public void iconColor(Menu menu, int color) {
        for (int i = 0; i < menu.size(); i++ ){
            Drawable drawable2 = menu.getItem(i).getIcon();
            if (drawable2 != null){
                drawable2.mutate();
                drawable2.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            String texto_imc = tipo_imc.getText().toString().trim();
            String texto_comoida = tipo_comida.getText().toString().trim();
            String texto_descripcion = descripcion.getText().toString().trim();

            int validar = 0;

            if(texto_imc.equals("")){
                tipo_imc.setError("Debes seleccionar un item");
                validar++;
            }
            if(texto_comoida.equals("")){
                tipo_comida.setError("Debes seleccionar un item");
                validar++;
            }
            if(texto_descripcion.equals("")){
                descripcion.setError("Debes escribir una descripcion");
                validar++;
            }

            if(validar == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("indicacion", texto_imc);
                contentValues.put("tipo_comida", texto_comoida);
                contentValues.put("descripcion", texto_descripcion);
                int consulta = sql.update("dieta" , contentValues, "id_dieta = '" + id_dieta + "'", null);

                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(activity_editar_item.this);
                if(consulta > 0){
                    materialAlertDialogBuilder.setMessage("La dieta se actualizó correctamente");
                } else {
                    materialAlertDialogBuilder.setMessage("Se ha producido un error al intentar actualizar, por favor intente" +
                            " nuevamente más tarde");
                }
                materialAlertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
                materialAlertDialogBuilder.show();
            } else {
                Toast.makeText(activity_editar_item.this, "Se encontraron campos vacíos", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == android.R.id.home){
            dismiss();
        }


            return super.onOptionsItemSelected(item);
    }

    public void dismiss(){
        Intent volver = new Intent(activity_editar_item.this, activity_ver_dietas.class);
        startActivity(volver);
        finish();
    }
}
