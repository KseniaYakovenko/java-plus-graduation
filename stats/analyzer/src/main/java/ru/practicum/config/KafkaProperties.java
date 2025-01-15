package ru.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String bootstrapServers;
    private final ConsumerProps userActionsConsumer = new ConsumerProps();
    private final ConsumerProps eventsSimilarityConsumer = new ConsumerProps();

    @Getter
    @Setter
    public static class ConsumerProps {
        private String topic;
        private String groupId;
        private String keyDeserializer;
        private String valueDeserializer;
    }
}