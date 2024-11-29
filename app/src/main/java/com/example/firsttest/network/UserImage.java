package com.example.firsttest.network;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserImage {
    @SerializedName("id")
    private int id;

    @SerializedName("image")
    private String image;
}
