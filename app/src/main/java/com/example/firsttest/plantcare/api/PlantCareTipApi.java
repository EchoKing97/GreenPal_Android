package com.example.firsttest.plantcare.api;

import com.example.firsttest.plantcare.model.PlantCareTip;
import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface PlantCareTipApi {
    @GET("api/plantcaretips/random5")
    Call<List<PlantCareTip>> getRandom5Tips();
}