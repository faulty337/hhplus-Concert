package com.hhp.concert.Business.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "concert_user")
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String waitingToken;

    private int balance;

    public User(String waitingToken, int balance) {
        this.waitingToken = waitingToken;
        this.balance = balance;
    }

    public void updateWaitingToken(String waitingToken){
        this.waitingToken = waitingToken;
    }

    public void chargeBalance(int amount){
        this.balance += amount;
    }
}
