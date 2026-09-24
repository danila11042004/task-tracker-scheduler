package danila.schedulerservice.kafka.producer;

import danila.schedulerservice.kafka.message.SummarizationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SummarizationProducer {
    private final String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public SummarizationProducer(@Value("${kafka.topics.summarization-request}") String topic,
                                 KafkaTemplate<String, Object> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(SummarizationRequest request) {
        kafkaTemplate.send(topic, request)
                .whenComplete((result, exception) ->
                {
                    if (exception != null) {
                        log.error("Failed to send message with user and tasks №{}", request.uuid(), exception);
                    }
                });
    }
}
