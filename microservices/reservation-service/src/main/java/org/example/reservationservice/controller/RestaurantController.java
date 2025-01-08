package org.example.reservationservice.controller;

import io.jsonwebtoken.Claims;
import org.example.reservationservice.dto.*;
import org.example.reservationservice.domain.*;
import org.example.reservationservice.service.RestaurantService;
import org.example.security.annotation.CheckSecurity;
import org.example.security.domain.Role;
import org.example.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


// Restaurant management controller - handles restaurant CRUD and configuration
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final TokenService tokenService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService, TokenService tokenService) {
        this.restaurantService = restaurantService;
        this.tokenService = tokenService;
    }
//da
    @PostMapping
    @CheckSecurity(roles = {Role.MANAGER})
    public ResponseEntity<RestaurantDto> createRestaurant(@RequestHeader("Authorization") String authorization, @Valid @RequestBody CreateRestaurantDto dto) {
        // Dodati servisnu logiku za ubacivanje restorana u bazu - NOTE: authorization token u pored role, u claimsu sadrzi i ID menadzera
        // Pa nije potrebno u telu slati taj id

        // Moramo da znamo managerId, pa ga vadimo iz tokena
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long managerId = claims.get("userId", Long.class);

        return ResponseEntity.ok(restaurantService.createRestaurant(dto, managerId));
    }
    //da
    @PutMapping("/{restaurantId}")
    @CheckSecurity(roles = {Role.MANAGER})
    public ResponseEntity<RestaurantDto> updateRestaurant(@RequestHeader("Authorization") String authorization, @PathVariable Long restaurantId, @Valid @RequestBody UpdateRestaurantDto dto) {
        // Pri pravljenju UpdateRestaurantDto-a staviti sva polja koja user moze da menja, ali pretopstaviti da sva mogu biti null, ovim omogucavamo parcijalnu promemu restorana, slicno kao u updateProfile metodi user-servicea

        // Extract managerId from JWT claims
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long managerId = claims.get("userId", Long.class);

        return ResponseEntity.ok(restaurantService.updateRestaurant(restaurantId, dto, managerId));
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurant(restaurantId));
    }
    // @ModelAttribute i Pageable implicitno resavaju svo filtriranje, paginaciju i sortiranje, jako su kul
    // Sa fronta ponovo, samo koristiti requestParams na sledeci nacin:
    // GET /api/restaurants?name=sushi&location=tokyo&page=0&size=10&sort=name,asc
    @GetMapping
    public ResponseEntity<Page<RestaurantDto>> searchRestaurants(@ModelAttribute RestaurantFilterCriteria criteria, Pageable pageable) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(criteria, pageable));
    }
//da
    @GetMapping("/manager")
    @CheckSecurity(roles = {Role.MANAGER})
    public ResponseEntity<Page<RestaurantDto>> getManagerRestaurants(@RequestHeader("Authorization") String authorization, @ModelAttribute RestaurantFilterCriteria criteria, Pageable pageable) {
        // Extract managerId from JWT claims
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long managerId = claims.get("userId", Long.class);

        return ResponseEntity.ok(restaurantService.getManagerRestaurants(managerId, criteria, pageable));
    }
//da
    @GetMapping("/cuisine-types")
    public ResponseEntity<List<CuisineTypeDto>> getCuisineTypes() {
        // This will return all available cuisine types with their display names
        return ResponseEntity.ok(Arrays.stream(CuisineType.values())
                .map(type -> new CuisineTypeDto(type, type.getDisplayName()))
                .collect(Collectors.toList()));
    }

}
