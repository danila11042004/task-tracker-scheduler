package danila.schedulerservice.kafka.message;

import danila.schedulerservice.dto.UserDto;

import java.util.UUID;

public record SummarizationRequest(
        UUID uuid,
        UserDto userDto) {
}
