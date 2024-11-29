package com.example.firsttest.weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("/location")
    Call<LocationResponse> getLocation();

    @GET("/weather")
    Call<WeatherResponse> getWeather(@Query("city") String city);
}
