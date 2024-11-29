package com.example.firsttest.plantcare.model;

import android.os.Parcelable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import android.os.Parcel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plant implements Parcelable {
    private int plant_id;
    private String plant_name;
    private String category;
    private String preferences;
    private String plant_image;

    protected Plant(Parcel in) {
        plant_id = in.readByte();
        plant_image = in.readString();
        plant_name = in.readString();
        category = in.readString();
        preferences = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(plant_id);
        dest.writeString(plant_image);
        dest.writeString(plant_name);
        dest.writeString(category);
        dest.writeString(preferences);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Plant> CREATOR = new Creator<Plant>() {
        @Override
        public Plant createFromParcel(Parcel in) {
            return new Plant(in);
        }

        @Override
        public Plant[] newArray(int size) {
            return new Plant[size];
        }
    };

}
