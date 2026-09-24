package danila.schedulerservice.kafka.consumer;

import danila.schedulerservice.client.BackendClient;
import danila.schedulerservice.dto.TaskDto;
import danila.schedulerservice.kafka.message.EmailMessage;
import danila.schedulerservice.kafka.message.SummarizationResponse;
import danila.schedulerservice.kafka.producer.EmailSenderProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SummarizationConsumer {
    private final EmailSenderProducer emailSenderProducer;
    private final BackendClient backendClient;

    @KafkaListener(topics = "${kafka.topics.summarization-response}",
            properties = "spring.json.value.default.type=danila.schedulerservice.kafka.message.SummarizationResponse",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(SummarizationResponse response) {
        EmailMessage request = new EmailMessage(UUID.randomUUID(), response.email(),
                "Task Report for" + LocalDate.now(), response.dailyReport());
        emailSenderProducer.send(request);
        List<Long> userTaskIdList = response.taskList().stream()
                .map(TaskDto::id)
                .toList();
        backendClient.deleteUserCompletedTasks(userTaskIdList);
    }
}
