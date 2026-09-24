package danila.schedulerservice.kafka.producer;

import danila.schedulerservice.kafka.message.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailSenderProducer {
    private final String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EmailSenderProducer(@Value("${kafka.topics.sending-email}") String topic,
                               KafkaTemplate<String, Object> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(EmailMessage request) {
        try {
            kafkaTemplate.send(topic, request).get();
        } catch (Exception e) {
            log.error("Failed to send daily report №{}", request.uuid(), e);
            throw new KafkaException("Failed to send daily report", e);
        }
    }
}
