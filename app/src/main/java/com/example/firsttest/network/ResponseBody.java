package com.example.firsttest.network;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseBody {
    private int code;
    private String message;
    private User user;

    public ResponseBody(int code, String message, User user) {
        this.code = code;
        this.message = message;
        this.user = user;
    }
}