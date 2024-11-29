package com.example.firsttest.activity;

//ControlFragment—AddPlantActivity-控制界面下的创建植物界面
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;
import com.example.firsttest.R;
import com.example.firsttest.plantcare.model.Plant;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.*;
import java.util.*;
import static com.example.firsttest.network.NetworkSettings.ADD_PLANT;

public class AddPlantActivity extends AppCompatActivity {
    private EditText plantNameEditText;
    private Spinner plantCategorySpinner;
    private ImageView plantImageView;
    private Button createPlantButton;
    private CheckBox plantPreferenceShade, plantPreferenceLight, plantPreferenceDrought, plantPreferenceMoisture, plantPreferenceWarmth, plantPreferenceBarren;
    private OkHttpClient client = new OkHttpClient();
    private Plant savedPlant;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        plantNameEditText = findViewById(R.id.plant_name);
        plantCategorySpinner = findViewById(R.id.plant_category);
        plantImageView = findViewById(R.id.plant_image);
        createPlantButton = findViewById(R.id.create_plant_button);
        plantPreferenceShade = findViewById(R.id.plant_preference_shade);
        plantPreferenceLight = findViewById(R.id.plant_preference_light);
        plantPreferenceDrought = findViewById(R.id.plant_preference_drought);
        plantPreferenceMoisture = findViewById(R.id.plant_preference_moisture);
        plantPreferenceWarmth = findViewById(R.id.plant_preference_warmth);
        plantPreferenceBarren = findViewById(R.id.plant_preference_barren);

        plantImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        createPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlant();
            }
        });

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.plant_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plantCategorySpinner.setAdapter(categoryAdapter);

    }

    private void createPlant() {
        String plantName = plantNameEditText.getText().toString();
        String plantCategory = (String) plantCategorySpinner.getSelectedItem();
        List<String> preferences = new ArrayList<>();
        if (plantPreferenceShade.isChecked()) preferences.add("喜阴");
        if (plantPreferenceLight.isChecked()) preferences.add("趋光");
        if (plantPreferenceDrought.isChecked()) preferences.add("耐旱");
        if (plantPreferenceMoisture.isChecked()) preferences.add("喜湿");
        if (plantPreferenceWarmth.isChecked()) preferences.add("喜暖");
        if (plantPreferenceBarren.isChecked()) preferences.add("耐瘠");

        Drawable drawable = plantImageView.getDrawable();
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        byte[] imageBytes;
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            imageBytes = byteArrayOutputStream.toByteArray();
        } else {
            Toast.makeText(this, "无法获取图片", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = getUserIdFromSharedPreferences();
        //构建请求体
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("plant_name", plantName);
        builder.addFormDataPart("category", plantCategory);
        builder.addFormDataPart("preferences", String.join(", ", preferences));
        builder.addFormDataPart("userId", String.valueOf(userId));
        builder.addFormDataPart("image", "image.jpg", RequestBody.create(MediaType.parse("image/jpeg"), imageBytes));

        //构建请求
        Request request = new Request.Builder()
                .url(ADD_PLANT)
                .post(builder.build())
                .build();

        //发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AddPlantActivity.this, "请求失败：网络问题", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseString = response.body().string();
                    runOnUiThread(() -> {
                        Toast.makeText(AddPlantActivity.this, "植物创建成功", Toast.LENGTH_SHORT).show();
                        savedPlant = new Gson().fromJson(responseString, Plant.class);
                        if (savedPlant != null) {
                            Intent intent = new Intent();
                            intent.putExtra("plant", savedPlant);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(AddPlantActivity.this, "创建植物失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
        setResult(Activity.RESULT_OK);
        finish();
    }

    private int getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        return sharedPreferences.getInt("userId", -1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                plantImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}