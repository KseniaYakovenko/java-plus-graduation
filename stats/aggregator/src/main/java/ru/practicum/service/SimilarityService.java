package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.config.KafkaProperties;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SimilarityService {

    private final Map<Long, Map<Long, Integer>> weights = new HashMap<>();

    private final Map<Long, Integer> eventWeightsSum = new HashMap<>();

    private final MinWeightsMatrix minWeightsMatrix = new MinWeightsMatrix();

    private final KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate;
    private final KafkaProperties props;

    public SimilarityService(KafkaTemplate<String, EventSimilarityAvro> kafkaTemplate,
                             KafkaProperties props) {
        this.kafkaTemplate = kafkaTemplate;
        this.props = props;
    }

    public void processUserAction(UserActionAvro action) {
        long userId  = action.getUserId();
        long eventId = action.getEventId();
        int newWeight = convertActionType(action.getActionType());
        long timestampMillis = action.getTimestamp();
        Instant timestamp = Instant.ofEpochMilli(timestampMillis);

        Map<Long, Integer> userMap = weights.computeIfAbsent(eventId, e -> new HashMap<>());
        int oldWeight = userMap.getOrDefault(userId, 0);

        if (newWeight <= oldWeight) {
            log.debug("Обновление не требуется: userId={}, eventId={}, weight={} <= oldWeight={}",
                    userId, eventId, newWeight, oldWeight);
            return;
        }

        userMap.put(userId, newWeight);

        int oldSum = eventWeightsSum.getOrDefault(eventId, 0);
        int diff = newWeight - oldWeight;
        int updatedSum = oldSum + diff;
        eventWeightsSum.put(eventId, updatedSum);

        for (Long otherEvent : weights.keySet()) {
            if (!otherEvent.equals(eventId)) {
                updatePairSimilarity(eventId, otherEvent, timestamp);
            }
        }
    }

    private void updatePairSimilarity(long eventA, long eventB, Instant timestamp) {
        double sMin = calcSMin(eventA, eventB);
        minWeightsMatrix.put(eventA, eventB, sMin);

        double sA = eventWeightsSum.getOrDefault(eventA, 0);
        double sB = eventWeightsSum.getOrDefault(eventB, 0);
        if (sA == 0 || sB == 0) {

            log.debug("Обнаружена нулевая сумма (sA={}, sB={}), пропускающая сходство для событий {} и {}",
                    sA, sB, eventA, eventB);
            return;
        }

        float similarity = (float) (sMin / (sA * sB));

        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        EventSimilarityAvro similarityMsg = EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(similarity)
                .setTimestamp(timestamp)
                .build();

        kafkaTemplate.send(props.getProducer().getTopic(), similarityMsg);

        log.debug("Обновлено сходство для (A={}, B={}) => {}", first, second, similarity);
    }

    private double calcSMin(long eventA, long eventB) {
        Map<Long, Integer> userMapA = weights.getOrDefault(eventA, Map.of());
        Map<Long, Integer> userMapB = weights.getOrDefault(eventB, Map.of());

        double sum = 0.0;
        for (Map.Entry<Long, Integer> e : userMapA.entrySet()) {
            Long userId = e.getKey();
            int wA = e.getValue();
            Integer wB = userMapB.get(userId);
            if (wB != null) {
                sum += Math.min(wA, wB);
            }
        }
        return sum;
    }

    private int convertActionType(ActionTypeAvro actionType) {
        return switch (actionType) {
            case REGISTER -> 2;
            case LIKE -> 3;
            default -> 1;
        };
    }
}