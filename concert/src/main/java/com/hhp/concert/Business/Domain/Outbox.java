package com.hhp.concert.Business.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "outbox", indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_eventId", columnList = "eventId")
})
public class Outbox extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private String topic;

    @Column(unique = true)
    private UUID eventId;

    private String payload;

    private LocalDateTime updatedAt;

    public enum OutboxStatus {
        INIT,
        PUBLISH,
        FAILED
    }

    public Outbox(String topic, String payload, UUID eventId) {
        this.status = OutboxStatus.INIT;
        this.topic = topic;
        this.payload = payload;
        this.eventId = eventId;
    }

    public void updateFailedStatus() {
        this.status = OutboxStatus.FAILED;
    }
    public void updatePublishStatus() {
        this.status = OutboxStatus.PUBLISH;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
