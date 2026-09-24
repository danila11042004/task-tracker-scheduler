package danila.schedulerservice.kafka.message;

import danila.schedulerservice.dto.TaskDto;

import java.util.List;
import java.util.UUID;

public record SummarizationResponse(
        UUID uuid,
        List<TaskDto> taskList,
        String email,
        String dailyReport) {
}
