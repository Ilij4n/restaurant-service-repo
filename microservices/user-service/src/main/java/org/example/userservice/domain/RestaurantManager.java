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

    @Column(name = "employment_date", nullable = false)
    private LocalDate employmentDate;
}