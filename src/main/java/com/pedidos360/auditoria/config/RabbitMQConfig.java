package com.pedidos360.auditoria.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pedidos360.auditoria.dto.PedidoEventoDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de Apache Kafka para Pedidos360 Auditoría API.
 * Consume eventos de compras confirmadas desde el tópico 'pedidos-auditoria-topic'.
 */
@EnableKafka
@Configuration
public class RabbitMQConfig {

    @Value("${spring.kafka.bootstrap-servers:kafka:29092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:auditoria-consumer-group}")
    private String groupId;

    @Value("${pedidos360.kafka.topic.auditoria:pedidos-auditoria-topic}")
    private String auditoriaTopicName;

    @Bean
    public NewTopic pedidosAuditoriaTopic() {
        return TopicBuilder.name(auditoriaTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public ConsumerFactory<String, PedidoEventoDTO> consumerFactory() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonDeserializer<PedidoEventoDTO> jsonDeserializer = new JsonDeserializer<>(PedidoEventoDTO.class, objectMapper, false);
        jsonDeserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PedidoEventoDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PedidoEventoDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
