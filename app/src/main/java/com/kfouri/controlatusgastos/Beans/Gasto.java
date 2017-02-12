package com.kfouri.controlatusgastos.Beans;

/**
 * Created by mkfouri on 27/10/2015.
 */
public class Gasto {

    private long id;
    private long fecha;
    private String categoria;
    private Float importe;
    private String descripcion;
    private String Mes;

    public Gasto(long id,long fecha,String categoria,Float importe, String descripcion)
    {
        super();
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.categoria = categoria;
        this.importe = importe;
        this.id = id;
    }

    public Gasto(long id,long fecha,String categoria,Float importe, String descripcion,String Mes)
    {
        super();
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.categoria = categoria;
        this.importe = importe;
        this.id = id;
        this.Mes = Mes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Float getImporte() {
        return importe;
    }

    public void setImporte(Float importe) {
        this.importe = importe;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMes() {
        return Mes;
    }

    public void setMes(String mes) {
        Mes = mes;
    }
}
