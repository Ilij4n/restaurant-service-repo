package org.example.reservationservice.client;

import lombok.extern.slf4j.Slf4j;
import org.example.reservationservice.dto.ReservationCountDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client for communicating with the User Service.
 * Handles all HTTP requests to the user service endpoints.
 */
@Slf4j
@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;

    public UserServiceClient(RestTemplate restTemplate, @Value("${services.user-service.base-url}") String userServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.userServiceBaseUrl = userServiceBaseUrl;
    }

    /**
     * Gets the reservation count for a user.
     * Uses retry pattern to handle temporary service unavailability.
     *
     * @param userId User ID to get count for
     * @param token JWT token for authorization
     * @return ReservationCountDto containing the count
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public ReservationCountDto getReservationCount(Long userId, String token) {
        String url = UriComponentsBuilder
                .fromHttpUrl(userServiceBaseUrl)
                .path("/api/users/reservations-count/{userId}")
                .buildAndExpand(userId)
                .toUriString();

        HttpHeaders headers = createAuthHeaders(token);

        ResponseEntity<ReservationCountDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ReservationCountDto.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to get reservation count from user service");
        }

        return response.getBody();
    }

    /**
     * Updates the reservation count for a user.
     * Uses retry pattern to handle temporary service unavailability.
     *
     * @param userId User ID to update
     * @param increment True to increment, false to decrement
     * @param token JWT token for authorization
     * @return Updated ReservationCountDto
     */
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public ReservationCountDto updateReservationCount(Long userId, boolean increment, String token) {
        String url = UriComponentsBuilder
                .fromHttpUrl(userServiceBaseUrl)
                .path("/api/users/reservations-count/{userId}")
                .buildAndExpand(userId)
                .toUriString();

        HttpHeaders headers = createAuthHeaders(token);

        // Create request body
        UpdateReservationCountRequest request = new UpdateReservationCountRequest();
        request.setIncrement(increment);

        ResponseEntity<ReservationCountDto> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                ReservationCountDto.class
        );

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to update reservation count in user service");
        }

        return response.getBody();
    }

    private HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // FIXME SETBEARERAUTH SAM DODAJE BEARER PREFIXAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
        headers.setBearerAuth(token.split(" ")[1]);
        return headers;
    }

    /**
     * Inner class for update request payload
     */
    @lombok.Data
    private static class UpdateReservationCountRequest {
        private Boolean increment;
    }
}