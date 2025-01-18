package ru.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
public class KafkaProperties {
    private String bootstrapServers;
    private Consumer consumer = new Consumer();
    private Producer producer = new Producer();

    @Getter
    @Setter
    public static class Consumer {
        private String groupId;
        private String topic;
        private String keyDeserializer;
        private String valueDeserializer;
    }

    @Getter
    @Setter
    public static class Producer {
        private String topic;
        private String keySerializer;
        private String valueSerializer;
    }
}