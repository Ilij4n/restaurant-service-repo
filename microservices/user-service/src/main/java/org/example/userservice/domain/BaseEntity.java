package org.example.userservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deleted")
    private Boolean deleted = false;

    // Automatski nacin za postavljanje create i update timestampova preko JPQL-a
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /*
       Svaki put kad se izvrsi delete iz database (uglavnom pomocu odgovarajucih repositorya)
       ova metoda ce se triggerovati i setovati delete polje na true, umesto brisanja reda iz baze!
     */
    @PreRemove
    public void softDelete() {
        this.deleted = true;
    }
}