package com.example.fitfood.interfaz_consumidor.clases;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.R;
import com.example.fitfood.interfaz_consumidor.activity_dietas_guardadas;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class adaptador_ver_dietas_consumidor extends RecyclerView.Adapter<adaptador_ver_dietas_consumidor.ViewHolder> {
    List<items_ver_dietas_consumidor> lista;
    Context context;

    public adaptador_ver_dietas_consumidor(List<items_ver_dietas_consumidor> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public adaptador_ver_dietas_consumidor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_style_dietas_total, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adaptador_ver_dietas_consumidor.ViewHolder holder, int position) {
        holder.id.setText(String.valueOf(lista.get(position).getId()));
        holder.nombre.setText(lista.get(position).getNombre());
        holder.imc.setText(lista.get(position).getTipo_imc());
        holder.comida.setText(lista.get(position).getTipo_comida());
        holder.imagen.setImageResource(lista.get(position).getImagen());
        holder.descripcion.setText(lista.get(position).getDescripcion());
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseDeDatos bd = new BaseDeDatos(context);
                SQLiteDatabase sql = bd.getWritableDatabase();
                String id_dieta = holder.id.getText().toString().trim();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                String username = preferences.getString("usuario", null);
                Cursor usuario_id = sql.rawQuery("SELECT id_usuario FROM usuarios WHERE usuario = '" + username + "'", null);

                if(usuario_id.moveToFirst() && usuario_id != null){
                    String id = usuario_id.getString(0);
                    Cursor val = sql.rawQuery("SELECT * FROM dietas_usuario WHERE id_dieta = '" + id_dieta + "' AND " +
                            "id_usuario = '" + id + "'", null);
                    if(val.moveToFirst() && val != null){
                        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
                        dialog.setMessage("Vaya! parece que ya has guardado esta dieta");
                        dialog.setNegativeButton("Ver dietas guardadas", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent ver = new Intent(context, activity_dietas_guardadas.class);
                                context.startActivity(ver);
                                ((Activity) context).finish();
                            }
                        });
                        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                    } else {
                        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
                        dialog.setMessage("¿Esta seguro que desea guardar esta dieta?");
                        dialog.setNegativeButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Cursor usuario = sql.rawQuery("SELECT id_usuario FROM usuarios WHERE usuario = '" + username + "'", null);
                                Cursor dietista = sql.rawQuery("SELECT id_usuario FROM dieta WHERE id_dieta = '" + id_dieta + "'", null);

                                if(usuario.moveToFirst() && usuario != null && dietista.moveToFirst() && dietista != null){
                                    int id_usuario = usuario.getInt(0);

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("id_usuario", id_usuario);
                                    contentValues.put("id_dieta", id_dieta);
                                    contentValues.put("id_dietista", dietista.getInt(0));
                                    sql.insert("dietas_usuario", null, contentValues);

                                    Snackbar.make(view, "La dieta se guardo correctamente", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(view, "Ha ocurrido un error al intentar guardar la dieta", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id, nombre, imc, comida, descripcion;
        FloatingActionButton save;
        ImageView imagen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.tvw_id_dieta_dietista);
            nombre = (TextView) itemView.findViewById(R.id.tvw_nombre_dietista);
            imc = (TextView) itemView.findViewById(R.id.tvw_tipoImc_dietista);
            comida = (TextView) itemView.findViewById(R.id.tvw_tipoComida_dietista);
            descripcion = (TextView) itemView.findViewById(R.id.tvw_descripcion_dietista);
            save = (FloatingActionButton) itemView.findViewById(R.id.fab_save_vieta);
            imagen = (ImageView) itemView.findViewById(R.id.ivw_lista_dietista);
        }
    }
}
