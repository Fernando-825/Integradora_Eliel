package com.fastfood.FastFood_service.datastructures;

import com.fastfood.FastFood_service.model.Pedido;

public class PedidoQueue {
    private static final int MAX_SIZE = 100;
    private Pedido[] queueArray;
    private int front;
    private int rear;
    private int currentSize;

    public PedidoQueue() {
        this.queueArray = new Pedido[MAX_SIZE];
        this.front = 0;
        this.rear = -1;
        this.currentSize = 0;
    }

    /** Insertar al final */
    public void enqueue(Pedido pedido) {
        if (currentSize == MAX_SIZE) {
            throw new IllegalStateException("La cola está llena.");
        }
        rear = (rear + 1) % MAX_SIZE;
        queueArray[rear] = pedido;
        currentSize++;
    }

    /** Eliminar del frente */
    public Pedido dequeue() {
        if (isEmpty()) {
            return null; // O lanzar excepción
        }
        Pedido pedido = queueArray[front];
        // Opcional: limpiar el espacio (aunque el frente avanza)
        queueArray[front] = null;
        front = (front + 1) % MAX_SIZE;
        currentSize--;
        return pedido;
    }

    /** Verificar si está vacía */
    public boolean isEmpty() {
        return currentSize == 0;
    }

    /** Buscar y remover un pedido por ID (Necesario para Cancelar) */
    public Pedido removeById(int id) {
        if (isEmpty()) return null;

        // Crear un array temporal para guardar los pedidos que no se eliminan
        Pedido[] tempArray = new Pedido[currentSize - 1];
        Pedido removedPedido = null;
        int newSize = 0;

        // Recorrer la cola de manera lógica
        for (int i = 0; i < currentSize; i++) {
            int index = (front + i) % MAX_SIZE;
            Pedido currentPedido = queueArray[index];

            if (currentPedido.getId() == id) {
                removedPedido = currentPedido;
            } else {
                if (newSize < tempArray.length) {
                    tempArray[newSize++] = currentPedido;
                }
            }
        }

        // Si se encontró y removió, reconstruir la cola
        if (removedPedido != null) {
            // Resetear la cola
            this.queueArray = new Pedido[MAX_SIZE];
            this.front = 0;
            this.rear = -1;
            this.currentSize = 0;

            // Reinsertar los pedidos restantes
            for (int i = 0; i < newSize; i++) {
                enqueue(tempArray[i]);
            }
        }

        return removedPedido;
    }
}