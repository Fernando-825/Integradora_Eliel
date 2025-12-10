package com.fastfood.FastFood_service.service;

import com.fastfood.FastFood_service.datastructures.HistorialStack;
import com.fastfood.FastFood_service.datastructures.PedidoQueue;
import com.fastfood.FastFood_service.datastructures.SinglyLinkedList;
import com.fastfood.FastFood_service.model.HistorialOperacion;
import com.fastfood.FastFood_service.model.Pedido;
import com.fastfood.FastFood_service.model.dto.PedidoRequest;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class PedidoService {

    private final SinglyLinkedList pedidosList;
    private final PedidoQueue pedidosQueue;
    private final HistorialStack historialStack;
    private int nextId = 1;

    public PedidoService() {
        this.pedidosList = new SinglyLinkedList();
        this.pedidosQueue = new PedidoQueue();
        this.historialStack = new HistorialStack();
    }

    // --- 4.1. Registrar un nuevo pedido ---
    public Pedido crearPedido(PedidoRequest request) {
        if (request.getNombreCliente() == null || request.getNombreCliente().trim().isEmpty() ||
                request.getDescripcion() == null || request.getDescripcion().trim().isEmpty() ||
                request.getMonto() <= 0) {
            throw new IllegalArgumentException("Datos de pedido inválidos.");
        }

        Pedido nuevoPedido = new Pedido(
                nextId++,
                request.getNombreCliente(),
                request.getDescripcion(),
                request.getMonto(),
                "REGISTRADO"
        );

        // 1. Insertarlo en la lista
        pedidosList.add(nuevoPedido);

        // 2. Encolarlo en la cola de pendientes
        pedidosQueue.enqueue(nuevoPedido);

        // 3. Registrar la operación en la pila de historial
        // pedidoAntes es null porque es una creación
        historialStack.push(new HistorialOperacion("CREAR", null, nuevoPedido.deepCopy()));

        return nuevoPedido;
    }

    // --- 4.2. Listar todos los pedidos ---
    public Pedido[] listarTodos() {
        return pedidosList.toArray();
    }

    // --- 4.3. Consultar un pedido por id ---
    public Pedido buscarPorId(int id) {
        return pedidosList.findById(id);
    }

    // --- 4.4. Cancelar un pedido por id ---
    public Pedido cancelarPedido(int id) {
        Pedido pedido = pedidosList.findById(id);
        if (pedido == null) {
            return null; // Pedido no encontrado
        }

        if ("CANCELADO".equals(pedido.getEstado())) {
            // Ya está cancelado, no hace nada pero lo devuelve
            return pedido;
        }

        // 1. Guardar estado antes para el rollback
        Pedido pedidoAntes = pedido.deepCopy();

        // 2. Cambiar estado
        pedido.setEstado("CANCELADO");

        // 3. Eliminarlo de la cola (si estaba ahí)
        pedidosQueue.removeById(id);

        // 4. Registrar la operación en la pila de historial
        historialStack.push(new HistorialOperacion("CANCELAR", pedidoAntes, pedido.deepCopy()));

        return pedido;
    }

    // --- 4.5. Despachar el siguiente pedido ---
    public Pedido despacharSiguiente() {
        Pedido pedido = pedidosQueue.dequeue();

        if (pedido == null) {
            return null; // Cola vacía
        }

        // 1. El pedido ya fue desencolado. Guardar estado antes para el rollback
        Pedido pedidoAntes = pedido.deepCopy();

        // 2. Actualizar el estado en la lista principal (ya que el objeto es el mismo)
        pedido.setEstado("DESPACHADO");

        // 3. Registrar la operación en la pila de historial
        historialStack.push(new HistorialOperacion("DESPACHAR", pedidoAntes, pedido.deepCopy()));

        return pedido;
    }

    // --- 4.6. Obtener estadísticas de pedidos ---
    public Map<String, Object> obtenerEstadisticas() {
        Pedido[] todos = pedidosList.toArray();
        int totalPedidos = todos.length;
        double totalMonto = calcularTotalMontoRecursivo(pedidosList.getHead()); // Usar método recursivo
        int totalRegistrados = 0;
        int totalDespachados = 0;
        int totalCancelados = 0;

        for (Pedido p : todos) {
            switch (p.getEstado()) {
                case "REGISTRADO":
                    totalRegistrados++;
                    break;
                case "DESPACHADO":
                    totalDespachados++;
                    break;
                case "CANCELADO":
                    totalCancelados++;
                    break;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPedidos", totalPedidos);
        stats.put("totalMonto", totalMonto);
        stats.put("totalRegistrados", totalRegistrados);
        stats.put("totalDespachados", totalDespachados);
        stats.put("totalCancelados", totalCancelados);

        return stats;
    }

    // --- 4.7. Cálculo recursivo del monto total ---
    public double calcularTotalMontoRecursivo(Object node) {
        // La implementación se hace con el nodo interno de la SinglyLinkedList
        if (node == null) {
            return 0.0;
        }

        // Usamos reflexión simple para acceder a los campos de la clase interna Node
        // Si no se quiere usar reflexión, se debería hacer el método estático en la SLL y
        // pasar el head al inicio. Para mantenerlo limpio en Service, usaremos un truco.

        try {
            // Intentamos acceder a los campos data y next
            Pedido pedido = (Pedido) node.getClass().getDeclaredField("data").get(node);
            Object nextNode = node.getClass().getDeclaredField("next").get(node);

            return pedido.getMonto() + calcularTotalMontoRecursivo(nextNode);

        } catch (Exception e) {
            // Esto es solo un manejo de error simplificado para la reflexión
            // En un proyecto real, se debería manejar mejor o cambiar la estructura
            System.err.println("Error en la recursividad: " + e.getMessage());
            return 0.0;
        }
    }

    // Método público para el endpoint de recursividad
    public double getTotalMontoRecursivo() {
        return calcularTotalMontoRecursivo(pedidosList.getHead());
    }

    // --- 4.8. Rollback de la última operación ---
    public HistorialOperacion rollback() {
        HistorialOperacion ultimaOp = historialStack.pop();

        if (ultimaOp == null) {
            return null; // Pila vacía
        }

        String tipoOp = ultimaOp.getTipoOperacion();
        Pedido pedidoDespues = ultimaOp.getPedidoDespues();
        Pedido pedidoAntes = ultimaOp.getPedidoAntes();

        // El ID del pedido afectado es el del estado "después" (o "antes", es el mismo pedido)
        int id = pedidoDespues.getId();

        switch (tipoOp) {
            case "CREAR":
                // Deshacer CREAR: eliminar el pedido de la lista y de la cola
                pedidosList.removeById(id);
                pedidosQueue.removeById(id);
                // Nota: El id incremental (nextId) se mantiene, es aceptable.
                break;

            case "CANCELAR":
            case "DESPACHAR":
                // Deshacer CANCELAR / DESPACHAR: restaurar el pedido a su estado anterior (pedidoAntes)
                Pedido pedidoActual = pedidosList.findById(id);
                if (pedidoActual != null) {
                    // Restaurar el estado y otros campos relevantes
                    pedidoActual.setEstado(pedidoAntes.getEstado());
                    // Otros campos (nombre, monto, etc.) también se restaurarían si pudieran cambiar,
                    // pero en este proyecto solo cambia el estado.

                    // Si el estado anterior era "REGISTRADO", volver a encolar
                    if ("REGISTRADO".equals(pedidoAntes.getEstado())) {
                        pedidosQueue.enqueue(pedidoActual);
                    }
                }
                break;

            default:
                // Operación desconocida, no hacer nada
                break;
        }

        return ultimaOp;
    }
}
