package com.example.fitfood.interfaz_dietista.clases;

public class items_lista_seguidores {
    int imagen;
    String nombre;
    String usuario;
    int id;

    public items_lista_seguidores(){

    }

    public items_lista_seguidores(int imagen, String nombre, String usuario, int id) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.usuario = usuario;
        this.id = id;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
