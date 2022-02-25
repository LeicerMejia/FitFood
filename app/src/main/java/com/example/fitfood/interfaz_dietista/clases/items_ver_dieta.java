package com.example.fitfood.interfaz_dietista.clases;

public class items_ver_dieta {
    int id;
    int imagen;
    String tipo_imc;
    String tipo_comida;
    String descripcion;

    public items_ver_dieta(){

    }

    public items_ver_dieta(int id, int imagen, String tipo_imc, String tipo_comida, String descripcion) {
        this.id = id;
        this.imagen = imagen;
        this.tipo_imc = tipo_imc;
        this.tipo_comida = tipo_comida;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getTipo_imc() {
        return tipo_imc;
    }

    public void setTipo_imc(String tipo_imc) {
        this.tipo_imc = tipo_imc;
    }

    public String getTipo_comida() {
        return tipo_comida;
    }

    public void setTipo_comida(String tipo_comida) {
        this.tipo_comida = tipo_comida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
