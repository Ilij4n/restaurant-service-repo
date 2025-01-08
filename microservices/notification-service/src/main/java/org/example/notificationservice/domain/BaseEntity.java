package org.example.notificationservice.domain;

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

    // Automatically set create and update timestamps using Hibernate annotations
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /*
       Every time a delete is executed from database (usually through corresponding repositories)
       this method will be triggered and set delete field to true, instead of deleting the row from database!
     */
    @PreRemove
    public void softDelete() {
        this.deleted = true;
    }
}