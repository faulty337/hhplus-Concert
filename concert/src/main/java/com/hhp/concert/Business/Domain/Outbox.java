package com.hhp.concert.Business.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "outbox", indexes = {
        @Index(name = "idx_status", columnList = "status")
})
public class Outbox extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private String topic;

    private String payload;

    private LocalDateTime updatedAt;

    public enum OutboxStatus {
        INIT,
        PUBLISH,
        FAILED
    }

    public Outbox(String topic, String payload) {
        this.status = OutboxStatus.INIT;
        this.topic = topic;
        this.payload = payload;
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
