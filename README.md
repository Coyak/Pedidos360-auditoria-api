# Microservicio de Auditoría - Pedidos360 (Nuevo Microservicio a Elección)

Microservicio desarrollado en **Java 17** con **Spring Boot** propuesto e implementado por el equipo como componente adicional de valor para el ecosistema **Pedidos360**, encargado de la auditoría y trazabilidad inmutable de todas las transacciones de compra mediante colas y Dead Letter Queues (DLQ) en **RabbitMQ**.

---

## 🎯 Justificación y Valor de Negocio

Cumple con el requerimiento de la pauta de Evaluación:
> *"Nuevo Microservicio a Elección: Los equipos deberán proponer y desarrollar un microservicio adicional propio que se integre al ecosistema y utilice las colas de RabbitMQ para cumplir con alguna funcionalidad de valor para la tienda."*

Este microservicio captura cada evento `pedidos.pedido.creado`, genera un hash criptográfico SHA-256 para garantizar la integridad y no repudio de la orden, y proporciona endpoints REST para fiscalización, estadísticas de ventas y auditoría forense.

---

## 🚀 Requisitos Previos

- **Java**: JDK 17 o 21
- **Maven**: 3.8+ o Maven Wrapper (`mvnw.cmd`)
- **RabbitMQ**: Broker en ejecución (puerto 5672)
- **Puerto del servicio**: `8083`

---

## ⚙️ Configuración del Entorno (`application.properties`)

```properties
server.port=8083
spring.application.name=auditoria-api

# Conexión a RabbitMQ Broker
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=pedidos360_user
spring.rabbitmq.password=pedidos360_pass

# Topología Centralizada de Mensajería
pedidos360.rabbitmq.exchange.pedidos=pedidos.exchange
pedidos360.rabbitmq.exchange.dlx=pedidos.dlx.exchange
pedidos360.rabbitmq.queue.auditoria=pedidos.auditoria.queue
pedidos360.rabbitmq.queue.auditoria-dlq=pedidos.auditoria.dlq
pedidos360.rabbitmq.routing-key.creado=pedidos.pedido.creado
pedidos360.rabbitmq.routing-key.auditoria-dlq=pedidos.auditoria.dlq
```

---

## 🛠️ Cómo Compilar y Ejecutar

1. **Compilar el proyecto**:
   ```bash
   .\mvnw.cmd clean package -DskipTests
   ```

2. **Ejecutar localmente**:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

---

## 📌 Endpoints REST Disponibles

### 1. Estado del Microservicio
- **Método:** `GET`
- **Ruta:** `/api/auditoria/status`

### 2. Listado General de Órdenes Auditadas
- **Método:** `GET`
- **Ruta:** `/api/auditoria`

### 3. Consulta de Auditoría por Pedido ID
- **Método:** `GET`
- **Ruta:** `/api/auditoria/{pedidoId}`

### 4. Métricas y Estadísticas de Auditoría
- **Método:** `GET`
- **Ruta:** `/api/auditoria/stats`

---

## 🐰 Mensajería Asíncrona (RabbitMQ)

- **Cola de entrada:** `pedidos.auditoria.queue`
- **Dead Letter Queue:** `pedidos.auditoria.dlq`
- **Confirmación:** Manual explícita (`basicAck`) y NACK sin requeue hacia DLQ.
