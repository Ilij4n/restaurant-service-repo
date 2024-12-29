package org.example.userservice.repository;

import org.example.security.domain.Role;
import org.example.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Basic finder methods
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Find active users
    List<User> findByIsActiveTrueAndDeletedFalse();

    // Find by role
    List<User> findByRoleAndDeletedFalse(Role role);

    // Find active users by role
    List<User> findByRoleAndIsActiveTrueAndDeletedFalse(Role role);

    // Custom query to update user's banned status
    @Modifying
    @Query("UPDATE User u SET u.isBanned = :banned WHERE u.id = :userId")
    void updateBanStatus(@Param("userId") Long userId, @Param("banned") boolean banned);

    // Custom query to update user's active status
    @Modifying
    @Query("UPDATE User u SET u.isActive = :active WHERE u.id = :userId")
    void updateActiveStatus(@Param("userId") Long userId, @Param("active") boolean active);

    // FIXME - verovatno izdvojiti ovo u zaseban ClientRepo jer reservationCount nije polje usera
    // Takodje implementirati metodu koja vraca broj rezervacija za user id umesto ove
    // Find users eligible for loyalty benefits (clients with minimum reservation count)
//    @Query("SELECT u FROM User u WHERE TYPE(u) = Client AND u.reservationCount >= :minReservations")
//    List<User> findClientsWithMinimumReservations(@Param("minReservations") Integer minReservations);
}