package org.example.userservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("MANAGER")
@Getter
@Setter
@NoArgsConstructor
public class RestaurantManager extends User {

    @Column(name = "employment_date")  // Removed nullable = false
    private LocalDate employmentDate;
    // Malo glupo jer imamo jedan prema vise vezu izmedju managera i restorana, al nek ima koje dodatno polje
    // Da bude maaaalo manje glupo, ovo moze da mu bude ime nekog "glavnog" restorana
    @Column(name = "restaurant_name")
    private String restaurantName;


}