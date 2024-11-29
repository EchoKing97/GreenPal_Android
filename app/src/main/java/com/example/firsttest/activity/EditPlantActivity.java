package com.example.firsttest.activity;

//ControlFragment—EditPlantActivity-控制界面下的编辑植物界面
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import com.bumptech.glide.Glide;
import com.example.firsttest.R;
import com.example.firsttest.plantcare.adapter.OperationRecordAdapter;
import com.example.firsttest.plantcare.model.OperationRecord;
import com.example.firsttest.plantcare.model.Plant;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.example.firsttest.network.NetworkSettings.ADD_OPERATION;
import static com.example.firsttest.network.NetworkSettings.GET_OPERATION;

public class EditPlantActivity extends AppCompatActivity {
    private ImageView plantImage;
    private TextView plantNameText;
    private TextView plantCategory;
    private TextView plantPreferences;
    private Button waterButton;
    private Button careButton;
    private ListView operationRecordList;
    private Plant plant;
    private String plantName;
    private List<OperationRecord> operationRecords = new ArrayList<>();
    private OperationRecordAdapter operationRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        plantImage = findViewById(R.id.plant_image);
        plantNameText = findViewById(R.id.plant_name);
        plantCategory = findViewById(R.id.plant_category);
        plantPreferences = findViewById(R.id.plant_preferences);
        waterButton = findViewById(R.id.water_button);
        careButton = findViewById(R.id.care_button);
        operationRecordList = findViewById(R.id.operation_record_list);

        plant = getIntent().getParcelableExtra("plant");
        if (plant != null) {
            String plantImageBase64 = plant.getPlant_image();
            Glide.with(this)
                    .load("data:image/jpeg;base64," + plantImageBase64)
                    .into(plantImage);
            plantNameText.setText(plant.getPlant_name());
            plantCategory.setText(plant.getCategory());
            plantPreferences.setText(plant.getPreferences());
            plantName = plant.getPlant_name();
        }

        //浇水和养护按钮的点击事件
        waterButton.setOnClickListener(v -> recordOperation(plantName, "浇水"));
        careButton.setOnClickListener(v -> recordOperation(plantName, "养护"));

        //操作记录列表
        initOperationRecordList();
    }

    @SuppressLint("StaticFieldLeak")
    private void recordOperation(final String plantName, final String operation) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                int userId = getUserIdFromSharedPreferences();
                String json = "{\"userId\":" + userId + ",\"plantName\":\"" + plantName + "\",\"operation\":\"" + operation + "\"}";
                RequestBody body = RequestBody.create(json, JSON);
                Request request = new Request.Builder()
                        .url(ADD_OPERATION)
                        .post(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                initOperationRecordList();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void initOperationRecordList() {
        new AsyncTask<Void, Void, List<OperationRecord>>() {
            @Override
            protected List<OperationRecord> doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(GET_OPERATION + "?plantName=" + plantName)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        return parseOperationRecords(responseData);
                    } else {
                        return new ArrayList<>();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<OperationRecord> records) {
                super.onPostExecute(records);
                operationRecords.clear();
                operationRecords.addAll(records);
                if (operationRecordAdapter == null) {
                    operationRecordAdapter = new OperationRecordAdapter(EditPlantActivity.this, operationRecords);
                    operationRecordList.setAdapter(operationRecordAdapter);
                } else {
                    operationRecordAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }

    private List<OperationRecord> parseOperationRecords(String jsonData) {
        List<OperationRecord> records = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String operationTimeStr = jsonObject.getString("operationTime");
                Date operationTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(operationTimeStr);
                OperationRecord record = new OperationRecord(
                        jsonObject.getString("operation"),
                        operationTime,
                        jsonObject.getString("plantName"),
                        jsonObject.getInt("plantId"),
                        jsonObject.getInt("userId")
                );
                records.add(record);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return records;
    }

    private int getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        return sharedPreferences.getInt("userId", -1);
    }
}