package com.pedidos360.auditoria.messaging;

import com.pedidos360.auditoria.dto.PedidoEventoDTO;
import com.pedidos360.auditoria.service.AuditoriaService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumidor de eventos de RabbitMQ para el dominio de Auditoría y Control Operacional.
 * Escucha la cola 'pedidos.auditoria.queue', persiste la traza de auditoría
 * y gestiona confirmación explícita (ACK) o NACK hacia DLQ ante errores.
 */
@Component
public class AuditoriaConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaConsumer.class);

    private final AuditoriaService auditoriaService;

    public AuditoriaConsumer(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @RabbitListener(
            queues = "${pedidos360.rabbitmq.queue.auditoria}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void procesarAuditoriaPedido(
            @Payload PedidoEventoDTO pedido,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("------------------------------------------------------------------");
        log.info(">>> [RABBITMQ AUDITORÍA] Evento de pedido capturado para auditoría: {}", pedido.getPedidoId());

        try {
            if (pedido.getPedidoId() == null || pedido.getPedidoId().trim().isEmpty()) {
                log.error(">>> [ERROR VALIDACIÓN - DLQ] El pedido carece de identificador único. Desviando a DLQ.");
                channel.basicNack(deliveryTag, false, false);
                return;
            }

            // Registrar trazabilidad inmutable
            auditoriaService.registrarAuditoriaPedido(pedido);

            log.info(">>> [AUDITORÍA OK] Registro exitoso para pedido {}. Enviando ACK manual (deliveryTag={})",
                    pedido.getPedidoId(), deliveryTag);
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error(">>> [ERROR TÉCNICO - DLQ] Error al auditar pedido {}: {}. Enviando NACK hacia DLQ.",
                    pedido.getPedidoId(), e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        } finally {
            log.info("------------------------------------------------------------------");
        }
    }
}
