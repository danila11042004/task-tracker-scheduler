package danila.schedulerservice.client;

import danila.schedulerservice.dto.UserDto;
import danila.schedulerservice.exception.BackendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class BackendClient {
    private final RestClient restClient;

    public BackendClient(
            @Value("${backend.secret}") String secret,
            @Value("${backend.url}") String url) {
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + secret)
                .build();
    }

    @Retryable(
            includes = RestClientException.class,
            maxRetries = 2,
            delay = 2000,
            multiplier = 2
    )
    public List<UserDto> getUserDtoList() {
        return restClient.get()
                .uri("/internal/scheduler/users-with-tasks")
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        ((request, response) -> {
                            String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            log.error("Failed get user list.Status: {} Body: {}", response.getStatusCode(),
                                    body);
                            throw new BackendException("Backend return error");
                        }))
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Retryable(
            includes = RestClientException.class,
            maxRetries = 2,
            delay = 2000,
            multiplier = 2
    )
    public void deleteUserCompletedTasks(List<Long> userTaskIdList) {
        restClient.method(HttpMethod.DELETE)
                .uri("/internal/scheduler/tasks/completed")
                .body(userTaskIdList)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        ((request, response) -> {
                            String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                            log.error("Failed delete user completed tasks.Status: {} Body: {}",
                                    response.getStatusCode(), body);
                            throw new BackendException("Backend return error");
                        }))
                .toBodilessEntity();
    }
}
