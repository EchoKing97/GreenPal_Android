package com.example.firsttest.activity;

//HomeFragment-首页界面
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.firsttest.R;
import com.example.firsttest.plantcare.api.PlantCareTipApi;
import com.example.firsttest.plantcare.model.PlantCareTip;
import com.example.firsttest.weather.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import java.util.List;

public class HomeFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationAddressTextView;
    private TextView temperatureTextView;
    private TextView humidityTextView;
    private TextView windDirectionTextView;
    private TextView windPowerTextView;
    private WeatherApi weatherApi;
    private PlantCareTipApi plantCareTipApi;
    private TextView careTip1;
    private TextView careTip2;
    private TextView careTip3;
    private TextView careTip4;
    private TextView careTip5;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        locationAddressTextView = view.findViewById(R.id.location_address);
        temperatureTextView = view.findViewById(R.id.temperature);
        humidityTextView = view.findViewById(R.id.humidity);
        windDirectionTextView = view.findViewById(R.id.wind_direction);
        windPowerTextView = view.findViewById(R.id.wind_power);
        careTip1 = view.findViewById(R.id.care_tip1);
        careTip2 = view.findViewById(R.id.care_tip2);
        careTip3 = view.findViewById(R.id.care_tip3);
        careTip4 = view.findViewById(R.id.care_tip4);
        careTip5 = view.findViewById(R.id.care_tip5);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        Retrofit retrofit = ApiClient.getRetrofitInstance();
        weatherApi = retrofit.create(WeatherApi.class);
        plantCareTipApi = retrofit.create(PlantCareTipApi.class);

        getLocationAndUpdateWeather();
        getRandomCareTips();
        return view;
    }

    private void getLocationAndUpdateWeather() {
        Call<LocationResponse> call = weatherApi.getLocation();
        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String province = response.body().getProvince();
                    String city = response.body().getCity();
                    String adcode = response.body().getAdcode();
                    locationAddressTextView.setText(province + " " + city);
                    getWeatherByAdcode(adcode);
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                locationAddressTextView.setText("定位失败....");
            }
        });
    }

    private void getWeatherByAdcode(String city) {
        Call<WeatherResponse> call = weatherApi.getWeather(city);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    updateUI(weatherResponse);
                }
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {}
        });
    }

    private void updateUI(WeatherResponse weatherResponse) {

        if (weatherResponse.getLives() != null && !weatherResponse.getLives().isEmpty()) {
            WeatherLive live = weatherResponse.getLives().get(0);
            temperatureTextView.setText("温度: " + live.getTemperature() + "°C");
            humidityTextView.setText("湿度: " + live.getHumidity() + "%");
            windDirectionTextView.setText("风向: " + live.getWinddirection());
            windPowerTextView.setText("风力: " + live.getWindpower() + "级");
        } else {
            temperatureTextView.setText("温度: 未知");
            humidityTextView.setText("湿度: 未知");
            windDirectionTextView.setText("风向: 未知");
            windPowerTextView.setText("风力: 未知");
        }
    }

    private void getRandomCareTips() {
        Call<List<PlantCareTip>> call = plantCareTipApi.getRandom5Tips();
        call.enqueue(new Callback<List<PlantCareTip>>() {
            @Override
            public void onResponse(Call<List<PlantCareTip>> call, Response<List<PlantCareTip>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlantCareTip> tips = response.body();
                    updateCareTips(tips);
                } else {
                    for (TextView tipView : new TextView[]{careTip1, careTip2, careTip3, careTip4, careTip5}) {
                        tipView.setText("加载失败...");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PlantCareTip>> call, Throwable t) {
                for (TextView tipView : new TextView[]{careTip1, careTip2, careTip3, careTip4, careTip5}) {
                    tipView.setText("加载失败...");
                }
            }
        });
    }

    private void updateCareTips(List<PlantCareTip> tips) {
        if (tips.size() >= 5) {
            careTip1.setText(tips.get(0).getContent());
            careTip2.setText(tips.get(1).getContent());
            careTip3.setText(tips.get(2).getContent());
            careTip4.setText(tips.get(3).getContent());
            careTip5.setText(tips.get(4).getContent());
        } else {
            for (int i = 0; i < tips.size(); i++) {
                TextView tipView = (TextView) (i == 0 ? careTip1 : i == 1 ? careTip2 : i == 2 ? careTip3 : i == 3 ? careTip4 : careTip5);
                tipView.setText(tips.get(i).getContent());
            }
            for (int i = tips.size(); i < 5; i++) {
                TextView tipView = (TextView) (i == 0 ? careTip1 : i == 1 ? careTip2 : i == 2 ? careTip3 : i == 3 ? careTip4 : careTip5);
                tipView.setText("无提示");
            }
        }
    }
}