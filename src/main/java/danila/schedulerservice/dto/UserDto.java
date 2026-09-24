package danila.schedulerservice.dto;

import java.util.List;

public record UserDto(
        Long id,
        String email,
        List<TaskDto> taskList) {
}
