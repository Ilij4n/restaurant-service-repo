package org.example.reservationservice.repository;

import org.example.reservationservice.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant> {
    // Find restaurant by ID and ensure it's not deleted
    @Query("SELECT r FROM Restaurant r WHERE r.id = :id AND r.deleted = false")
    Optional<Restaurant> findByIdAndDeletedFalse(@Param("id") Long id);

    // Find all restaurants managed by a specific manager
    @Query("SELECT r FROM Restaurant r WHERE r.managerId = :managerId AND r.deleted = false")
    List<Restaurant> findByManagerId(@Param("managerId") Long managerId);

    // Check if a manager already has a restaurant with the given name
    boolean existsByManagerIdAndNameAndDeletedFalse(Long managerId, String name);

    // Find a specific restaurant managed by a specific manager
    @Query("SELECT r FROM Restaurant r WHERE r.id = :restaurantId AND r.managerId = :managerId AND r.deleted = false")
    Optional<Restaurant> findByIdAndManagerId(@Param("restaurantId") Long restaurantId, @Param("managerId") Long managerId);
}
