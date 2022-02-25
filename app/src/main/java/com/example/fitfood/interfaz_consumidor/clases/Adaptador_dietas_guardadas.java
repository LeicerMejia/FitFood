package com.example.fitfood.interfaz_consumidor.clases;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class Adaptador_dietas_guardadas extends RecyclerView.Adapter<Adaptador_dietas_guardadas.ViewHolder> {
    List<items_dietas_guardadas> list;
    Context context;

    public Adaptador_dietas_guardadas(List<items_dietas_guardadas> lista, Context context) {
        this.list = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public Adaptador_dietas_guardadas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_style_dietas_guardadas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adaptador_dietas_guardadas.ViewHolder holder, int position) {
        holder.id.setText(String.valueOf(list.get(position).getId()));
        holder.imagen.setImageResource(list.get(position).getImagen());
        holder.descripcion.setText(list.get(position).getDescripcion());
        holder.comida.setText(list.get(position).getTipo_comida());
        holder.imc.setText(list.get(position).getTipo_imc());
        holder.nombre.setText(list.get(position).getNombre());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseDeDatos bd = new BaseDeDatos(context);
                SQLiteDatabase sql = bd.getWritableDatabase();
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                String username = preferences.getString("usuario", null);

                alert.setMessage("¿Esta seguro que desea dejar de seguir esta dieta?");
                alert.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Cursor usuario = sql.rawQuery("SELECT id_usuario FROM usuarios WHERE usuario = '" + username + "'", null);
                        if(usuario.moveToFirst() && usuario != null){
                            String id = usuario.getString(0);
                            String id_dieta = holder.id.getText().toString().trim();
                            int resultado = sql.delete("dietas_usuario", "id_usuario = '" + id + "' AND id_dieta = '" +
                                    id_dieta +"'", null);
                            if(resultado > 0){
                                context.startActivity(((Activity) context).getIntent());
                                ((Activity) context).finish();
                                Toast.makeText(context, "La dieta se dejo de seguir", Toast.LENGTH_SHORT).show();
                            } else {
                                MaterialAlertDialogBuilder a = new MaterialAlertDialogBuilder(context);
                                a.setMessage("No se ha podido dejar de seguir la dieta, por favor intente nuevamente más tarde");
                                a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                a.show();
                            }
                        }
                    }
                });
                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView id, nombre, imc, comida, descripcion;
        FloatingActionButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagen = (ImageView) itemView.findViewById(R.id.ivw_lista_guardadas_dr);
            id = (TextView) itemView.findViewById(R.id.tvw_id_dieta_lista_dr);
            nombre = (TextView) itemView.findViewById(R.id.tvw_nombre_dietista_dr);
            imc = (TextView) itemView.findViewById(R.id.tvw_tipoImc_dietista_dr);
            comida = (TextView) itemView.findViewById(R.id.tvw_tipoComida_dietista_dr);
            descripcion = (TextView) itemView.findViewById(R.id.tvw_descripcion_lista_dr);
            delete = (FloatingActionButton) itemView.findViewById(R.id.fab_unfollow);

        }
    }
}
