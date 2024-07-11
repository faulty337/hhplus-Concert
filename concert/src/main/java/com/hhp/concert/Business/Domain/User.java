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

    private String token;

    private int balance;



    public User(String token, int balance) {
        this.token = token;
        this.balance = balance;
    }

    public void updateWaitingToken(String waitingToken){
        this.token = waitingToken;
    }
    public void userBalance(int amount){
        this.balance -= amount;
    }
    public void chargeBalance(int amount){
        this.balance += amount;
    }
}
