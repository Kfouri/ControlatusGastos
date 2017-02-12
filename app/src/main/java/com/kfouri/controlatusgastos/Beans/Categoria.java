package com.kfouri.controlatusgastos.Beans;

public class Categoria
{

    private String descripcion;
    private long id;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Categoria(String descripcion, long id) {
        super();
        this.descripcion = descripcion;
        this.id = id;
    }

}
