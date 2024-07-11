package com.hhp.concert.Business.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
