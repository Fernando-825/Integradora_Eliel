package com.fastfood.FastFood_service.model;

// Define que se almacena en la pila
// Contiene tipoOperacion (CREAR, CANCELAR Y DESPACHAR)
// y 2 copias del pedido pedidoAntes/Despues. Esto es necesario para Rollback
public class HistorialOperacion {
    private String tipoOperacion; // "CREAR", "CANCELAR", "DESPACHAR"
    private Pedido pedidoAntes;
    private Pedido pedidoDespues;

    public HistorialOperacion(String tipoOperacion, Pedido pedidoAntes, Pedido pedidoDespues) {
        this.tipoOperacion = tipoOperacion;
        this.pedidoAntes = pedidoAntes;
        this.pedidoDespues = pedidoDespues;
    }

    // Getters y Setters
    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public Pedido getPedidoAntes() { return pedidoAntes; }
    public void setPedidoAntes(Pedido pedidoAntes) { this.pedidoAntes = pedidoAntes; }

    public Pedido getPedidoDespues() { return pedidoDespues; }
    public void setPedidoDespues(Pedido pedidoDespues) { this.pedidoDespues = pedidoDespues; }
}