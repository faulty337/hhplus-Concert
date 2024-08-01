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
@Table(name = "concert_session")
public class ConcertSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime sessionTime;

    private Long concertId;

    public ConcertSession(LocalDateTime sessionTime, Long concertId) {
        this.sessionTime = sessionTime;
        this.concertId = concertId;
    }
}
