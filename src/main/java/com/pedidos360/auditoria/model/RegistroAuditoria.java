package com.pedidos360.auditoria.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Registro de trazabilidad y auditoría inmutable de un pedido procesado.
 */
public class RegistroAuditoria implements Serializable {

    private String auditId;
    private String pedidoId;
    private String clienteEmail;
    private String direccionEnvio;
    private Double totalFacturado;
    private Integer totalItems;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaRegistroAuditoria;
    private String estadoAuditoria;
    private String hashIntegridad;

    public RegistroAuditoria() {}

    public RegistroAuditoria(String auditId, String pedidoId, String clienteEmail, String direccionEnvio,
                             Double totalFacturado, Integer totalItems, LocalDateTime fechaPedido,
                             LocalDateTime fechaRegistroAuditoria, String estadoAuditoria, String hashIntegridad) {
        this.auditId = auditId;
        this.pedidoId = pedidoId;
        this.clienteEmail = clienteEmail;
        this.direccionEnvio = direccionEnvio;
        this.totalFacturado = totalFacturado;
        this.totalItems = totalItems;
        this.fechaPedido = fechaPedido;
        this.fechaRegistroAuditoria = fechaRegistroAuditoria;
        this.estadoAuditoria = estadoAuditoria;
        this.hashIntegridad = hashIntegridad;
    }

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public String getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public Double getTotalFacturado() {
        return totalFacturado;
    }

    public void setTotalFacturado(Double totalFacturado) {
        this.totalFacturado = totalFacturado;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public LocalDateTime getFechaRegistroAuditoria() {
        return fechaRegistroAuditoria;
    }

    public void setFechaRegistroAuditoria(LocalDateTime fechaRegistroAuditoria) {
        this.fechaRegistroAuditoria = fechaRegistroAuditoria;
    }

    public String getEstadoAuditoria() {
        return estadoAuditoria;
    }

    public void setEstadoAuditoria(String estadoAuditoria) {
        this.estadoAuditoria = estadoAuditoria;
    }

    public String getHashIntegridad() {
        return hashIntegridad;
    }

    public void setHashIntegridad(String hashIntegridad) {
        this.hashIntegridad = hashIntegridad;
    }
}
