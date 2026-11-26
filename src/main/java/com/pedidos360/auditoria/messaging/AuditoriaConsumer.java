package com.pedidos360.auditoria.messaging;

import com.pedidos360.auditoria.dto.PedidoEventoDTO;
import com.pedidos360.auditoria.service.AuditoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de Apache Kafka para el dominio de Auditoría y Control Operacional.
 * Escucha el tópico 'pedidos-auditoria-topic' y persiste la traza criptográfica SHA-256.
 */
@Component
public class AuditoriaConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaConsumer.class);

    private final AuditoriaService auditoriaService;

    public AuditoriaConsumer(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @KafkaListener(
            topics = "${pedidos360.kafka.topic.auditoria:pedidos-auditoria-topic}",
            groupId = "${spring.kafka.consumer.group-id:auditoria-consumer-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void procesarAuditoriaPedido(@Payload PedidoEventoDTO pedido) {
        log.info("------------------------------------------------------------------");
        if (pedido == null || pedido.getPedidoId() == null || pedido.getPedidoId().trim().isEmpty()) {
            log.error(">>> [ERROR VALIDACIÓN KAFKA AUDITORÍA] El evento recibido carece de pedidoId válido.");
            return;
        }

        log.info(">>> [KAFKA AUDITORÍA] Evento de pedido capturado desde 'pedidos-auditoria-topic': {}", pedido.getPedidoId());

        try {
            auditoriaService.registrarAuditoriaPedido(pedido);
            log.info(">>> [AUDITORÍA OK] Registro SHA-256 exitoso para pedido {}", pedido.getPedidoId());
        } catch (Exception e) {
            log.error(">>> [ERROR TÉCNICO AUDITORÍA] Error al auditar pedido {}: {}",
                    pedido.getPedidoId(), e.getMessage(), e);
        } finally {
            log.info("------------------------------------------------------------------");
        }
    }
}
