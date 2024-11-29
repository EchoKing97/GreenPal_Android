package com.example.firsttest.plantcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import com.bumptech.glide.Glide;
import com.example.firsttest.R;
import com.example.firsttest.plantcare.model.Plant;
import java.util.ArrayList;

public class PlantAdapter extends BaseAdapter {
    private final ArrayList<Plant> plants;
    private final LayoutInflater inflater;

    public PlantAdapter(Context context) {
        this.plants = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void add(Plant plant) {
        plants.add(plant);
    }

    public void clear() {
        plants.clear();
    }

    @Override
    public int getCount() {
        return plants.size();
    }

    @Override
    public Plant getItem(int position) {
        return plants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.plant_item, parent, false);
        }
        Plant plant = plants.get(position);
        ImageView plantImage = convertView.findViewById(R.id.plant_image);
        TextView plantName = convertView.findViewById(R.id.plant_name);
        TextView plantCategory = convertView.findViewById(R.id.plant_category);
        TextView plantPreferences = convertView.findViewById(R.id.plant_preferences);

        plantName.setText(plant.getPlant_name());
        plantCategory.setText(plant.getCategory());
        plantPreferences.setText(plant.getPreferences());

        Glide.with(convertView.getContext())
                .load("data:image/jpeg;base64," + plant.getPlant_image())
                .into(plantImage);

        return convertView;
    }
}