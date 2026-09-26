package danila.schedulerservice.scheduler;

import danila.schedulerservice.client.BackendClient;
import danila.schedulerservice.dto.UserDto;
import danila.schedulerservice.kafka.message.SummarizationRequest;
import danila.schedulerservice.kafka.producer.SummarizationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class DailyReportScheduler {
    private final BackendClient backendClient;
    private final SummarizationProducer summarizationProducer;

    @Scheduled(cron = "0 59 23 * * *", zone = "Europe/Moscow")
    public void requestDailyReport() {
        List<UserDto> userDtoList = backendClient.getUserDtoList();
        for (UserDto userDto : userDtoList) {
            summarizationProducer.send(new SummarizationRequest(UUID.randomUUID(), userDto));
        }
    }

}
