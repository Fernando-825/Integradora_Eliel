package com.fastfood.FastFood_service.model;

public class Pedido {
    private int id;
    private String nombreCliente;
    private String descripcion;
    private double monto;
    private String estado; // "REGISTRADO", "EN_PREPARACION", "DESPACHADO", "CANCELADO"

    // Constructor vacío
    public Pedido() {}

    // Constructor completo
    public Pedido(int id, String nombreCliente, String descripcion, double monto, String estado) {
        this.id = id;
        this.nombreCliente = nombreCliente;
        this.descripcion = descripcion;
        this.monto = monto;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // Método para crear una copia profunda (necesario para el Rollback)
    public Pedido deepCopy() {
        return new Pedido(this.id, this.nombreCliente, this.descripcion, this.monto, this.estado);
    }
}