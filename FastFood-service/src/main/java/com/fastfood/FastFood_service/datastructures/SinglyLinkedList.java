package com.fastfood.FastFood_service.datastructures;

import com.fastfood.FastFood_service.model.Pedido;

public class SinglyLinkedList {

    // Clase interna para el nodo
    private static class Node {
        Pedido data;
        Node next;

        Node(Pedido data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    public SinglyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    /** Agregar al final */
    public void add(Pedido pedido) {
        Node newNode = new Node(pedido);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    /** Buscar por id */
    public Pedido findById(int id) {
        Node current = head;
        while (current != null) {
            if (current.data.getId() == id) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    /** Eliminar por id */
    public boolean removeById(int id) {
        if (head == null) return false;

        // Caso: El nodo a eliminar es la cabeza
        if (head.data.getId() == id) {
            head = head.next;
            size--;
            return true;
        }

        // Buscar el nodo anterior al que se va a eliminar
        Node current = head;
        while (current.next != null && current.next.data.getId() != id) {
            current = current.next;
        }

        // Si se encontró el nodo
        if (current.next != null) {
            current.next = current.next.next;
            size--;
            return true;
        }
        return false;
    }

    /** Número de elementos */
    public int size() {
        return size;
    }

    /** Método para recorrer los pedidos y devolver un array (para listar) */
    public Pedido[] toArray() {
        Pedido[] pedidos = new Pedido[size];
        Node current = head;
        int index = 0;
        while (current != null) {
            pedidos[index++] = current.data;
            current = current.next;
        }
        return pedidos;
    }

    // Método auxiliar para el cálculo recursivo
    public Node getHead() {
        return head;
    }

    // Necesario para el Rollback de CREAR
    public Pedido removeLast() {
        if (head == null) return null;

        if (head.next == null) {
            Pedido data = head.data;
            head = null;
            size--;
            return data;
        }

        Node current = head;
        while (current.next.next != null) {
            current = current.next;
        }

        Pedido data = current.next.data;
        current.next = null;
        size--;
        return data;
    }
}
