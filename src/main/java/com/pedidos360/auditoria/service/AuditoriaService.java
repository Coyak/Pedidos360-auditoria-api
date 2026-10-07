package com.pedidos360.auditoria.service;

import com.pedidos360.auditoria.dto.PedidoEventoDTO;
import com.pedidos360.auditoria.model.RegistroAuditoria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio encargado del registro y consulta de auditoría transaccional de compras.
 */
@Service
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    private final Map<String, RegistroAuditoria> registros = new ConcurrentHashMap<>();

    /**
     * Registra un pedido en el log de auditoría inmutable calculando hash de integridad.
     */
    public RegistroAuditoria registrarAuditoriaPedido(PedidoEventoDTO pedido) {
        String auditId = "AUDIT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        int itemsCount = pedido.getItems() != null ? pedido.getItems().size() : 0;
        String rawData = pedido.getPedidoId() + "|" + pedido.getClienteEmail() + "|" + pedido.getTotalPagar() + "|" + itemsCount;
        String hash = calcularHashSha256(rawData);

        RegistroAuditoria registro = new RegistroAuditoria(
                auditId,
                pedido.getPedidoId(),
                pedido.getClienteEmail(),
                pedido.getDireccionEnvio(),
                pedido.getTotalPagar(),
                itemsCount,
                pedido.getFechaCreacion() != null ? pedido.getFechaCreacion() : LocalDateTime.now(),
                LocalDateTime.now(),
                "AUDITADO_CONFORME",
                hash
        );

        registros.put(pedido.getPedidoId(), registro);
        log.info(">>> [AUDITORÍA REGISTRADA] Pedido: {}, AuditID: {}, Hash: {}",
                pedido.getPedidoId(), auditId, hash);
        return registro;
    }

    public List<RegistroAuditoria> listarRegistros() {
        return new ArrayList<>(registros.values());
    }

    public Optional<RegistroAuditoria> buscarPorPedidoId(String pedidoId) {
        if (pedidoId == null) return Optional.empty();
        return Optional.ofNullable(registros.get(pedidoId));
    }

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPedidosAuditados", registros.size());
        double totalRecaudado = registros.values().stream()
                .mapToDouble(r -> r.getTotalFacturado() != null ? r.getTotalFacturado() : 0.0)
                .sum();
        stats.put("totalMontoAuditado", totalRecaudado);
        stats.put("estadoEcosistema", "OPERACIONAL");
        return stats;
    }

    private String calcularHashSha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16);
        } catch (Exception e) {
            return "HASH_FALLBACK_" + UUID.randomUUID().toString().substring(0, 8);
        }
    }
}
