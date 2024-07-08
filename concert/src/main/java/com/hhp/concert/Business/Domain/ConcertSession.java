package com.hhp.concert.Business.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConcertSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime sessionTime;

    @ManyToOne
    private Concert concert;

    public ConcertSession(LocalDateTime sessionTime, Concert concert) {
        this.sessionTime = sessionTime;
        this.concert = concert;
    }
}
