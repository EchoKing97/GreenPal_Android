package com.example.firsttest.weather;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class WeatherResponse {
    private int status;
    private int count;
    private String info;
    private String infocode;
    private List<WeatherLive> lives;
}


