package com.ecommerce.notificationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String recipientId;
    private boolean read;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    private String notificationType;
    private String link;

    public Notification() {
        this.timestamp = LocalDateTime.now();
    }

}
