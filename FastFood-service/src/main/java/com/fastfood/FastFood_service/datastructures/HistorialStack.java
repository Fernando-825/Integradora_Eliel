package com.fastfood.FastFood_service.datastructures;

import com.fastfood.FastFood_service.model.HistorialOperacion;

public class HistorialStack {
    private static final int MAX_SIZE = 50;
    private HistorialOperacion[] stackArray;
    private int top; // Índice del último elemento insertado

    public HistorialStack() {
        this.stackArray = new HistorialOperacion[MAX_SIZE];
        this.top = -1;
    }

    /** Insertar en la cima */
    public void push(HistorialOperacion op) {
        if (top == MAX_SIZE - 1) {
            // Manejo simple de "Stack Overflow", podrías optar por lanzar una excepción
            System.err.println("Pila de historial llena. No se puede agregar más.");
            return;
        }
        stackArray[++top] = op;
    }

    /** Eliminar de la cima y devolver */
    public HistorialOperacion pop() {
        if (isEmpty()) {
            return null;
        }
        // Devolver el elemento y decrementar 'top'
        return stackArray[top--];
    }

    /** Verificar si está vacía */
    public boolean isEmpty() {
        return top == -1;
    }
}
