package com.example.fitfood.interfaz_dietista.clases;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class adaptador_ver_dieta extends RecyclerView.Adapter<adaptador_ver_dieta.ViewHolder> {
    List<items_ver_dieta> list;
    Context context;
    BaseDeDatos bd;

    public adaptador_ver_dieta(List<items_ver_dieta> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public adaptador_ver_dieta.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_ver_dietas_style, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adaptador_ver_dieta.ViewHolder holder, int position) {
        holder.id.setText(String.valueOf(list.get(position).getId()));
        holder.imagen.setImageResource(list.get(position).getImagen());
        holder.descripcion.setText(list.get(position).getDescripcion());
        holder.tipo_comida.setText(list.get(position).getTipo_comida());
        holder.tipo_imc.setText(list.get(position).getTipo_imc());
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.menu);
                popupMenu.inflate(R.menu.menu_ver_lista_item);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String id_de_dieta = holder.id.getText().toString().trim();
                        bd = new BaseDeDatos(context);
                        SQLiteDatabase sql = bd.getWritableDatabase();

                        switch (menuItem.getItemId()) {
                            case R.id.editar_dieta_menu_item_ver:
                                Intent i = new Intent(context, activity_editar_item.class);
                                i.putExtra("id_de_la_dieta", id_de_dieta);
                                context.startActivity(i);
                                break;
                            case R.id.eliminar_dieta_menu_item_ver:
                                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                                alert.setTitle("Eliminar dieta");
                                alert.setMessage("¿Esta seguro que desea eliminar esta dieta? \n\n" +
                                        "Nota: Recuerde que una vez eliminado esta acción no se puede deshacer");
                                alert.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                       int consulta = sql.delete("dieta", "id_dieta = '"+ id_de_dieta + "'", null);
                                       MaterialAlertDialogBuilder alertaConsulta = new MaterialAlertDialogBuilder(context);

                                       if(consulta > 0){
                                           alertaConsulta.setMessage("Se ha eliminado correctamente la dieta");
                                       } else {
                                           alertaConsulta.setMessage("Lo sentimos ha ocurrido un error al intentar eliminar" +
                                                   " la dieta, por favor intente nuevamente más tarde");
                                       }
                                       alertaConsulta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {
                                               ((Activity) context).finish();
                                               context.startActivity(((Activity) context).getIntent());
                                           }
                                       });
                                       alertaConsulta.show();
                                    }
                                });
                                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                alert.show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tipo_imc, tipo_comida, descripcion, id;
        ImageView imagen;
        FloatingActionButton menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tipo_imc = (TextView) itemView.findViewById(R.id.tvw_tipoImc_lista_ver);
            tipo_comida = (TextView) itemView.findViewById(R.id.tvw_tipoComida_lista_ver);
            descripcion = (TextView) itemView.findViewById(R.id.tvw_descripcion_lista_ver);
            id = (TextView) itemView.findViewById(R.id.tvw_id_dieta_lista_ver);
            imagen = (ImageView) itemView.findViewById(R.id.ivw_lista_ver);
            menu = (FloatingActionButton) itemView.findViewById(R.id.fab_menu_option_ver);
        }
    }
}
