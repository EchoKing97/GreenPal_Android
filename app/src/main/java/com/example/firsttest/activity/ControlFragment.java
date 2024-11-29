package com.example.firsttest.activity;

//ControlFragment-控制界面-植物管理
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.firsttest.R;
import com.example.firsttest.plantcare.adapter.PlantAdapter;
import com.example.firsttest.plantcare.model.Plant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;
import static com.example.firsttest.network.NetworkSettings.GET_PLANT;

public class ControlFragment extends Fragment {
    private ListView plantsListView;
    private PlantAdapter plantAdapter;
    private Button addPlantButton;

    public ControlFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        plantsListView = view.findViewById(R.id.plants_list);
        plantAdapter = new PlantAdapter(getContext());
        plantsListView.setAdapter(plantAdapter);

        //增加植物按钮的点击事件
        addPlantButton = view.findViewById(R.id.add_plant_button);
        addPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPlantActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        //检查网络权限
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.INTERNET}, 1);
        } else {
            fetchUserPlants();
        }

        //植物列表的点击事件
        plantsListView = view.findViewById(R.id.plants_list);
        plantAdapter = new PlantAdapter(getContext());
        plantsListView.setAdapter(plantAdapter);
        plantsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Plant selectedPlant = plantAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), EditPlantActivity.class);
                intent.putExtra("plant", selectedPlant);
                startActivity(intent);
            }
        });
        return view;
    }

    private void fetchUserPlants() {
        OkHttpClient client = new OkHttpClient();
        int userId = getUserIdFromSharedPreferences();
        Request request = new Request.Builder()
                .url(GET_PLANT + "?userId=" + userId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseString = response.body().string();
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Plant>>() {}.getType();
                    final List<Plant> userPlants = gson.fromJson(responseString, type);
                    getActivity().runOnUiThread(() -> {
                        plantAdapter.clear();
                        for (Plant plant : userPlants) {
                            plantAdapter.add(plant);
                        }
                        plantAdapter.notifyDataSetChanged();
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "创建植物失败", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private int getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", MODE_PRIVATE);
        return sharedPreferences.getInt("userId", -1);
    }
}