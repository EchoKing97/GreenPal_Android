package com.example.firsttest.network;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Body;

public interface UserServiceApi {
    @PUT("/api/users/update")
    Call<ResponseBody> updateUser(@Body User user);
}