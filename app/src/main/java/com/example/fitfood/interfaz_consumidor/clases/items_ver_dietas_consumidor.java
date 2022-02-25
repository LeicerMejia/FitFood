package com.example.fitfood.interfaz_consumidor.clases;

public class items_ver_dietas_consumidor {
    int imagen;
    int id;
    String nombre;
    String tipo_comida;
    String tipo_imc;
    String descripcion;

    public items_ver_dietas_consumidor(){

    }

    public items_ver_dietas_consumidor(int imagen, int id, String nombre, String tipo_comida, String tipo_imc, String descripcion) {
        this.imagen = imagen;
        this.id = id;
        this.nombre = nombre;
        this.tipo_comida = tipo_comida;
        this.tipo_imc = tipo_imc;
        this.descripcion = descripcion;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo_comida() {
        return tipo_comida;
    }

    public void setTipo_comida(String tipo_comida) {
        this.tipo_comida = tipo_comida;
    }

    public String getTipo_imc() {
        return tipo_imc;
    }

    public void setTipo_imc(String tipo_imc) {
        this.tipo_imc = tipo_imc;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
