package com.pedidos360.auditoria.controller;

import com.pedidos360.auditoria.model.RegistroAuditoria;
import com.pedidos360.auditoria.service.AuditoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el microservicio de Auditoría.
 */
@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    /**
     * Estado del microservicio
     * GET /api/auditoria/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Microservicio de Auditoría Transaccional en ejecución");
        response.put("port", "8083");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos los registros auditados
     * GET /api/auditoria
     */
    @GetMapping
    public ResponseEntity<List<RegistroAuditoria>> listarTodos() {
        return ResponseEntity.ok(auditoriaService.listarRegistros());
    }

    /**
     * Consulta el registro de auditoría de un pedido específico
     * GET /api/auditoria/{pedidoId}
     */
    @GetMapping("/{pedidoId}")
    public ResponseEntity<?> buscarPorPedidoId(@PathVariable String pedidoId) {
        return auditoriaService.buscarPorPedidoId(pedidoId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "ERROR", "message", "Registro de auditoría no encontrado para el pedido: " + pedidoId)));
    }

    /**
     * Estadísticas operacionales y métricas del ecosistema
     * GET /api/auditoria/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        return ResponseEntity.ok(auditoriaService.obtenerEstadisticas());
    }
}
