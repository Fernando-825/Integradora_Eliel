package com.fastfood.FastFood_service.controller;

import com.fastfood.FastFood_service.model.HistorialOperacion;
import com.fastfood.FastFood_service.model.Pedido;
import com.fastfood.FastFood_service.model.dto.PedidoRequest;
import com.fastfood.FastFood_service.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;

// es la capa que expone las funcionalidades a través de la web usando endpoints REST
// @RestController: Indica que esta clase maneja peticiones REST.
//@RequestMapping("/api/pedidos"): Define la ruta base para todos los endpoints.
//@Autowired: Permite la inyección de dependencias (en este caso, el PedidoService),
// siguiendo el principio de Inversión de Control (IoC) de Spring.

// Simplemete recibe peticiones http, traduce los datos de entrada JSON

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // --- 4.1. Registrar un nuevo pedido ---
    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest request) {
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(request);
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Manejo de la validación
            Map<String, String> error = Collections.singletonMap("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    // --- 4.2. Listar todos los pedidos ---
    @GetMapping
    public ResponseEntity<Pedido[]> listarPedidos() {
        Pedido[] pedidos = pedidoService.listarTodos();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    // --- 4.3. Consultar un pedido por id ---
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedido(@PathVariable int id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        if (pedido == null) {
            Map<String, String> error = Collections.singletonMap("error", "Pedido no encontrado con ID: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(pedido, HttpStatus.OK);
    }

    // --- 4.4. Cancelar un pedido por id ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarPedido(@PathVariable int id) {
        Pedido pedido = pedidoService.cancelarPedido(id);

        if (pedido == null) {
            Map<String, String> error = Collections.singletonMap("error", "Pedido no encontrado con ID: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("mensaje", "Pedido cancelado correctamente");
        response.put("pedido", pedido);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --- 4.5. Despachar el siguiente pedido ---
    @PostMapping("/despachar")
    public ResponseEntity<?> despacharSiguiente() {
        Pedido pedidoDespachado = pedidoService.despacharSiguiente();

        if (pedidoDespachado == null) {
            Map<String, String> error = Collections.singletonMap("error", "No hay pedidos pendientes para despachar.");
            return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409
        }

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("mensaje", "Pedido despachado correctamente");
        response.put("pedido", pedidoDespachado);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --- 4.6. Obtener estadísticas de pedidos ---
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> stats = pedidoService.obtenerEstadisticas();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    // --- 4.7. Cálculo recursivo del monto total ---
    @GetMapping("/total-recursivo")
    public ResponseEntity<Map<String, Double>> getTotalRecursivo() {
        double total = pedidoService.getTotalMontoRecursivo();
        Map<String, Double> response = Collections.singletonMap("totalMontoRecursivo", total);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --- 4.8. Rollback de la última operación ---
    @PostMapping("/rollback")
    public ResponseEntity<?> rollbackUltimaOperacion() {
        HistorialOperacion revertida = pedidoService.rollback();

        if (revertida == null) {
            Map<String, String> error = Collections.singletonMap("error", "No hay operaciones registradas para revertir.");
            return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409
        }

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("mensaje", "Rollback realizado correctamente");
        response.put("operacionRevertida", revertida.getTipoOperacion());

        // Devolver el estado del pedido después del rollback (que es el estado 'antes' de la operación original)
        response.put("pedido", revertida.getPedidoAntes());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
