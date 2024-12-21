package ru.pachan.main.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.pachan.main.dto.writer.WriterDto;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${application.kafka.topic}")
    private String topicName;

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String servers;

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.enhancedObjectMapper();
    }

    @Bean
    public ProducerFactory<String, WriterDto> producerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        DefaultKafkaProducerFactory<String, WriterDto> kafkaProducerFactory = new DefaultKafkaProducerFactory<>(props);
        kafkaProducerFactory.setValueSerializer(new JsonSerializer<>(mapper));
        return kafkaProducerFactory;
    }

    @Bean
    public KafkaTemplate<String, WriterDto> kafkaTemplate(ProducerFactory<String, WriterDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public String topicName() {
        return TopicBuilder.name(topicName).partitions(1).replicas(1).build().name();
    }

}