package com.example.fitfood.interfaz_dietista.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitfood.BaseDeDatos;
import com.example.fitfood.R;

import java.util.ArrayList;
import java.util.List;

public class adaptador_lista_seguidores extends RecyclerView.Adapter<adaptador_lista_seguidores.ViewHolder> {
    List<items_lista_seguidores> seguidores;
    Context context;

    public adaptador_lista_seguidores(List<items_lista_seguidores> seguidores, Context context) {
        this.seguidores = seguidores;
        this.context = context;
    }

    @NonNull
    @Override
    public adaptador_lista_seguidores.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liset_style_seguidores, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adaptador_lista_seguidores.ViewHolder holder, int position) {
        holder.imagen.setImageResource(seguidores.get(position).getImagen());
        holder.nombre.setText(seguidores.get(position).getNombre());
        holder.usuario.setText(seguidores.get(position).getUsuario());
        holder.id.setText(String.valueOf(seguidores.get(position).getId()));
        holder.ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = holder.id.getText().toString().trim();
                LayoutInflater inflater = LayoutInflater.from(context);
                View vista = inflater.inflate(R.layout.activity_usuario_detalle, null);
                AlertDialog dialog = new AlertDialog.Builder(context).create();
                BaseDeDatos bd = new BaseDeDatos(context);
                SQLiteDatabase sql = bd.getReadableDatabase();

                Cursor cursor = sql.rawQuery("SELECT * FROM usuarios WHERE id_usuario = '" + id + "'", null);

                dialog.setView(vista);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView nombre_dialog = vista.findViewById(R.id.tv_nombre_detalle);
                TextView correo_dialog = vista.findViewById(R.id.tv_correo_detalle);
                TextView usuario_dialog = vista.findViewById(R.id.tv_user_detalle);
                Button btn_close = vista.findViewById(R.id.button_cerrar_custom_dialog);

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                if(cursor.moveToFirst() && cursor != null){
                    nombre_dialog.setText(cursor.getString(1) + " " + cursor.getString(2));
                    usuario_dialog.setText(cursor.getString(3));
                    correo_dialog.setText(cursor.getString(4));
                }
                dialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return seguidores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, usuario, id;
        Button ver;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.tv_nombre_ver_usuario);
            usuario = (TextView) itemView.findViewById(R.id.tv_user_ver_usuario);
            id = (TextView) itemView.findViewById(R.id.tv_id_ver_usuario);
            ver = (Button) itemView.findViewById(R.id.btn_more_ver_usuario);
            imagen = (ImageView) itemView.findViewById(R.id.imagen_de_lista);
        }
    }
}
