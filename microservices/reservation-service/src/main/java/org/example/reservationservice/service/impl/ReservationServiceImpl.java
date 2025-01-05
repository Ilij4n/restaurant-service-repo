package org.example.reservationservice.service.impl;

import org.example.reservationservice.client.UserServiceClient;
import org.example.reservationservice.domain.*;
import org.example.reservationservice.dto.*;
import org.example.reservationservice.mapper.ReservationMapper;
import org.example.reservationservice.repository.LoyaltyPerkRepository;
import org.example.reservationservice.repository.ReservationRepository;
import org.example.reservationservice.repository.RestaurantRepository;
import org.example.reservationservice.repository.TimeSlotRepository;
import org.example.reservationservice.repository.specification.ReservationSpecification;
import org.example.reservationservice.service.ReservationService;
import org.example.security.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation of the reservation service that handles all reservation-related operations.
 * This service manages the creation, cancellation, and querying of reservations while
 * maintaining consistency with time slots and user data.
 */
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final RestaurantRepository restaurantRepository;
    private final LoyaltyPerkRepository loyaltyPerkRepository;
    private final ReservationMapper reservationMapper;
    private final UserServiceClient userServiceClient;

    // TODO: Add these once implemented
    // private final NotificationServiceClient notificationServiceClient;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  TimeSlotRepository timeSlotRepository,
                                  ReservationMapper reservationMapper,
                                  RestaurantRepository restaurantRepository,
                                  UserServiceClient userServiceClient,
                                  LoyaltyPerkRepository loyaltyPerkRepository) {

        this.reservationRepository = reservationRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.reservationMapper = reservationMapper;
        this.restaurantRepository = restaurantRepository;
        this.userServiceClient = userServiceClient;
        this.loyaltyPerkRepository = loyaltyPerkRepository;
    }

    /**
     * Creates a new reservation for a given time slot.
     * The method ensures atomic updates of both the reservation and time slot status.
     * It also handles user service communication with retry capability.
     */
    @Override
    @Transactional
    public MessageResponseDto createReservation(Long slotId, CreateReservationDto dto, String userEmail, Long userId, String authToken) {
        // Fetch and validate the time slot
        TimeSlot timeSlot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        validateTimeSlotForReservation(timeSlot);

        // Create the reservation using mapper - dto to domain
        Reservation reservation = reservationMapper.toEntity(dto, timeSlot, userEmail, userId);

        // Update time slot visibility and save
        timeSlot.setVisible(false);
        timeSlotRepository.save(timeSlot);

        // Save the reservation
        reservation = reservationRepository.save(reservation);

        // Update user's reservation count and check for loyalty perks
        ReservationCountDto countDto = userServiceClient.updateReservationCount(userId, true, authToken);
        String loyaltyMessage = checkAndGenerateLoyaltyMessage(
                timeSlot.getRestaurant().getId(),
                countDto.getCount()
        );

        // Create response with appropriate message
        MessageResponseDto response = new MessageResponseDto();
        response.setSuccess(true);

        String baseMessage = "Your reservation has been successfully created.";
        if (loyaltyMessage != null) {
            response.setMessage(baseMessage + "\n\n" + loyaltyMessage);
        }
        else {
            response.setMessage(baseMessage + " You will receive a confirmation email shortly.");
        }



        // TODO: Implementirati slanje odgovarajuce poruke sa perkom preko maila
        // sendReservationNotification(reservation);
        return response;
    }

    /**
     * Handles reservation cancellation with different behavior based on who initiates the cancellation.
     * When cancelled by a user, the time slot becomes available again.
     * When cancelled by a manager, the time slot remains unavailable.
     */

    /**
     *  I klijent i menadžer restorana imaju mogućnost da
     *  otkažu rezervaciju. Potrebno je poslati informaciju (imejl) o otkazivanju
     *  rezervacije klijentu **ili** menadžeru
     *  Ive read enough, im satisfied:
     */

    @Override
    @Transactional
    public void cancelReservation(Long reservationId, Role role, String userEmail, Long userId, String authToken) {

        // Fetch the reservation, including the time slot (we'll use this information later)
        // Now only fetching active user reservations - there existed an issue where the normal
        // id check would return a cancelled reservation instead of an active one...
        // This would occur in instances where user had multiple reservations for a singular timeslot
        // Now technically, the user can be evil and spam create-cancel on a timeslot repeatedly, but im too tired to care about that rn

        Reservation reservation = reservationRepository
                .findByIdAndStatusAndDeletedFalse(reservationId, ReservationStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active reservation found with this ID"));

        // For clients, we check both email and userId for extra security
        // This ensures the JWT token matches the user making the request
        if (Role.CLIENT.equals(role)) {
            if (!reservation.getUserEmail().equals(userEmail) || !reservation.getUserId().equals(userId.toString())) {
                throw new RuntimeException("You can only cancel your own reservations");
            }
        }

        // For managers, we check if they manage the restaurant where the reservation was made
        if (Role.MANAGER.equals(role) && !isManagerOfRestaurant(userId, reservation.getRestaurantId())) {
            throw new RuntimeException("You can only cancel reservations for your restaurant");
        }

        // Temporal validation - ensure we're not cancelling past reservations
        if (reservation.getTimeSlot().getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot cancel past reservations");
        }

        handleCancellation(reservation, role);

        // TODO: Once notification service is available:
        // if (Role.MANAGER.equals(role)) {
        //     // Send notification to both manager and client
        //     sendCancellationNotification(reservation.getUserEmail(), "Your reservation has been cancelled by the restaurant");
        //     sendCancellationNotification(userEmail, "You have cancelled a customer's reservation");
        // } else {
        //     // Send notification only to client
        //     sendCancellationNotification(userEmail, "You have cancelled your reservation");
        // }

        // Update user's reservation count only if the client is cancelling
        if (Role.CLIENT.equals(role)) {
            System.out.println("AUTH TOKEN JE: " + authToken);
            ReservationCountDto reservationCountDto = userServiceClient.updateReservationCount(userId, false, authToken);
            System.out.println("Count je: " + reservationCountDto.getCount());
        }
    }

    /**
     * Retrieves paginated reservations for a specific user with filtering capabilities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getUserReservations(ReservationFilterCriteria criteria, Pageable pageable) {
        criteria.validate();

        // Create and execute specification
        Specification<Reservation> spec = ReservationSpecification.withFilterCriteria(criteria);
        Page<Reservation> reservations = reservationRepository.findAll(spec, pageable);

        return reservations.map(reservationMapper::toDto);
    }

    /**
     * Retrieves paginated reservations for a specific restaurant with filtering capabilities.
     */
    // What exactly stops the endpoint for this method from being called by a manager not managing said restaurant? - oh well
    // Guess we could add that check later on... though front should partially prevent this from happening
    @Override
    @Transactional(readOnly = true)
    public Page<ReservationDto> getRestaurantReservations(Long restaurantId, ReservationFilterCriteria criteria, Pageable pageable) {
        criteria.validate();

        // Add restaurant ID to criteria and execute search
        criteria.setRestaurantId(restaurantId);
        Specification<Reservation> spec = ReservationSpecification.withFilterCriteria(criteria);
        Page<Reservation> reservations = reservationRepository.findAll(spec, pageable);

        return reservations.map(reservationMapper::toDto);
    }

    // Private helper methods for validation and business logic

    private void validateTimeSlotForReservation(TimeSlot timeSlot) {
        if (!timeSlot.getVisible()) {
            throw new RuntimeException("This time slot is not available for reservation");
        }

        if (timeSlot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot reserve past time slots");
        }
    }

    // Handles different role-based cancellation behavior
    private void handleCancellation(Reservation reservation, Role role) {
        if (Role.CLIENT.equals(role)) {
            reservation.setStatus(ReservationStatus.CANCELLED_BY_USER);
            // Make time slot available again for user cancellations
            TimeSlot timeSlot = reservation.getTimeSlot();
            timeSlot.setVisible(true);
            timeSlotRepository.save(timeSlot);
        } else {
            reservation.setStatus(ReservationStatus.CANCELLED_BY_RESTAURANT);
            // Time slot remains unavailable for restaurant cancellations
            // A manager can opt to manually re-enable the slot in the future
        }

        reservationRepository.save(reservation);
    }

    // Helper method to check if a manager has authority over a restaurant
    private boolean isManagerOfRestaurant(Long managerId, Long restaurantId) {
        return restaurantRepository
                .findByIdAndManagerIdAndDeletedFalse(restaurantId, managerId)
                .isPresent();
    }

    private String checkAndGenerateLoyaltyMessage(Long restaurantId, Integer visitCount) {
        Optional<LoyaltyPerk> perk = loyaltyPerkRepository
                .findByRestaurantIdAndRequiredVisitsAndIsActiveTrueAndDeletedFalse(
                        restaurantId,
                        visitCount
                );

        if (perk.isPresent()) {
            LoyaltyPerk earnedPerk = perk.get();
            return String.format(
                    "Congratulations! This is your %d%s visit to %s!\n" +
                            "You have earned the following perk for your next visit:\n" +
                            "%s" +
                            (earnedPerk.getDiscountPercentage() != null ?
                                    String.format("\nDiscount: %d%%%%", earnedPerk.getDiscountPercentage()) : ""),
                    visitCount,
                    getOrdinalSuffix(visitCount),
                    earnedPerk.getRestaurant().getName(),
                    earnedPerk.getPerkDescription()
            );
        }

        return null;
    }

    // Helper method to generate ordinal suffixes (1st, 2nd, 3rd, etc.)
    private String getOrdinalSuffix(int number) {
        if (number >= 11 && number <= 13) {
            return "th";
        }
        return switch (number % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }

    // TODO: Implement these methods once services are available
    /*
    private void sendReservationNotification(Reservation reservation) {
        // Async notification sending logic
    }

    private void sendCancellationNotification(Reservation reservation, Role canceller) {
        // Async notification sending logic
    }

    private void updateUserReservationCount(Long userId, boolean increment) {
        // User service communication logic
    }
    */
}