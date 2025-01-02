package org.example.reservationservice.runner;

import org.example.reservationservice.domain.CuisineType;
import org.example.reservationservice.domain.Restaurant;
import org.example.reservationservice.repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Profile("default")
@Component
public class AppRunner implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final Random random = new Random();

    // Constants for data generation
    private static final Long FIRST_MANAGER_ID = 3L;  // ID from user service AppRunner
    private static final Long SECOND_MANAGER_ID = 4L; // This will be created in user service
    private static final int RESTAURANTS_PER_MANAGER = 50; // 50 restaurants per manager = 100 total

    // Lists for generating random restaurant names
    private static final String[] RESTAURANT_PREFIXES = {
            "The", "La", "El", "Le", "Café", "Bistro", "Ristorante", "Casa", "Trattoria"
    };

    private static final String[] RESTAURANT_NAMES = {
            "Garden", "Kitchen", "Table", "Spoon", "Fork", "Plate", "Grill", "Cucina", "Tavern",
            "Corner", "Street", "Avenue", "Square", "Market", "House", "Room", "Dining", "Chef"
    };

    private static final String[] RESTAURANT_SUFFIXES = {
            "& Co.", "House", "Place", "Restaurant", "Eatery", "Dining", "Kitchen", "Express", ""
    };

    // Lists for generating addresses
    private static final String[] STREETS = {
            "Main Street", "Oak Avenue", "Maple Road", "Cedar Lane", "Pine Street",
            "Elm Boulevard", "Park Road", "Lake Drive", "Forest Avenue", "River Street"
    };

    private static final String[] CITIES = {
            "Belgrade", "Novi Sad", "Niš", "Kragujevac", "Subotica",
            "Zrenjanin", "Pančevo", "Čačak", "Kraljevo", "Novi Pazar"
    };

    public AppRunner(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("Loading test restaurant data...");

        // Create restaurants for first manager
        createRestaurantsForManager(FIRST_MANAGER_ID);

        // Create restaurants for second manager
        createRestaurantsForManager(SECOND_MANAGER_ID);

        System.out.println("Test restaurant data loading completed!");
    }

    private void createRestaurantsForManager(Long managerId) {
        List<Restaurant> restaurants = new ArrayList<>();

        for (int i = 0; i < RESTAURANTS_PER_MANAGER; i++) {
            Restaurant restaurant = new Restaurant();

            // Generate random restaurant name
            restaurant.setName(generateRestaurantName());

            // Generate random address
            restaurant.setAddress(generateAddress());

            // Set random cuisine type
            restaurant.setCuisineType(getRandomCuisineType());

            // Generate random business hours
            LocalTime[] businessHours = generateBusinessHours();
            restaurant.setOpeningTime(businessHours[0]);
            restaurant.setClosingTime(businessHours[1]);

            // Set description
            restaurant.setDescription(generateDescription(restaurant.getName(),
                    restaurant.getCuisineType().getDisplayName()));

            // Set manager ID
            restaurant.setManagerId(managerId);

            restaurants.add(restaurant);
        }

        restaurantRepository.saveAll(restaurants);
        System.out.println("Created " + RESTAURANTS_PER_MANAGER + " restaurants for manager ID: " + managerId);
    }

    private String generateRestaurantName() {
        String prefix = RESTAURANT_PREFIXES[random.nextInt(RESTAURANT_PREFIXES.length)];
        String name = RESTAURANT_NAMES[random.nextInt(RESTAURANT_NAMES.length)];
        String suffix = RESTAURANT_SUFFIXES[random.nextInt(RESTAURANT_SUFFIXES.length)];

        return String.format("%s %s %s", prefix, name, suffix).trim();
    }

    private String generateAddress() {
        int streetNumber = random.nextInt(1, 200);
        String street = STREETS[random.nextInt(STREETS.length)];
        String city = CITIES[random.nextInt(CITIES.length)];

        return String.format("%d %s, %s", streetNumber, street, city);
    }

    private CuisineType getRandomCuisineType() {
        CuisineType[] types = CuisineType.values();
        return types[random.nextInt(types.length)];
    }

    private LocalTime[] generateBusinessHours() {
        // Generate realistic business hours
        int openingHour = random.nextInt(7, 12); // Open between 7 AM and 11 AM
        int closingHour = random.nextInt(20, 24); // Close between 8 PM and 11 PM

        return new LocalTime[]{
                LocalTime.of(openingHour, 0),
                LocalTime.of(closingHour, 0)
        };
    }

    private String generateDescription(String name, String cuisineType) {
        String[] templates = {
                "Welcome to %s, your destination for authentic %s cuisine. Our chefs bring years of experience and passion to every dish.",
                "%s offers a modern take on traditional %s dishes in a comfortable and elegant setting.",
                "Experience the finest %s dining at %s, where we combine traditional recipes with contemporary presentation.",
                "At %s, we pride ourselves on serving authentic %s dishes made from the freshest ingredients.",
                "Discover the rich flavors of %s cuisine at %s, where every meal is a celebration of taste and tradition."
        };

        String template = templates[random.nextInt(templates.length)];
        return String.format(template, name, cuisineType);
    }
}