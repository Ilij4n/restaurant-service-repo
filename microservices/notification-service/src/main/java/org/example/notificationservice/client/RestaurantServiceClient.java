package org.example.notificationservice.client;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.dto.RestaurantDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Component
public class RestaurantServiceClient {
    private final RestTemplate restTemplate;
    private final String restaurantServiceBaseUrl;

    public RestaurantServiceClient(
            RestTemplate restTemplate,
            @Value("${services.restaurant-service.base-url}") String restaurantServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.restaurantServiceBaseUrl = restaurantServiceBaseUrl;
    }

    public boolean verifyManagerRestaurant(Long managerId, Long restaurantId, String token) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(restaurantServiceBaseUrl)
                    .path("/api/restaurants/manager")
                    .queryParam("page", 0)
                    .queryParam("size", 100)  // Assuming a manager won't have more than 100 restaurants, kinda hard coded, but whatever
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.split(" ")[1]);

            ResponseEntity<RestaurantPageResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<RestaurantPageResponse>() {}
            );

            if (response.getBody() == null) {
                return false;
            }

            // Check if the restaurantId exists in the manager's restaurants
            return response.getBody().getContent().stream()
                    .anyMatch(restaurant -> restaurant.getId().equals(restaurantId));

        } catch (Exception e) {
            System.out.println("Naziv exceptiona je: " + e.getClass().getName());
            log.error("Error verifying manager restaurant: ", e);
            throw new RuntimeException("Failed to verify manager's restaurant access");
        }
    }

    // Inner class to handle pagination response
    @lombok.Data
    private static class RestaurantPageResponse {
        private List<RestaurantDto> content;
    }
}