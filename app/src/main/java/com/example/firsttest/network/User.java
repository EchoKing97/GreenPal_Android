package com.example.firsttest.network;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String name;
    private String password;
    private String phone;

    public User(String name, String password, String phoneNumber) {
        this.name = name;
        this.password = password;
        this.phone = phoneNumber;
    }
}