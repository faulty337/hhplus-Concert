package com.hhp.concert.Business.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "process_queue")
public class ProcessQueue extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private String token;

    public ProcessQueue(Long userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public ProcessQueue(Long userId) {
        this.userId = userId;
    }
}
