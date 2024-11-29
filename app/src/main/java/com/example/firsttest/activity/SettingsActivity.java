package com.example.firsttest.activity;

//ProfileFragment-SettingsActivity-个人中心界面下的保存用户设置界面
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import com.example.firsttest.R;
import com.example.firsttest.network.ResponseBody;
import com.example.firsttest.network.User;
import com.example.firsttest.network.UserServiceApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private ImageView profileImageView;
    private Button saveButton;
    private UserServiceApi userServiceApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usernameEditText = findViewById(R.id.username);
        phoneEditText = findViewById(R.id.phone);
        passwordEditText = findViewById(R.id.password);
        profileImageView = findViewById(R.id.profile_image);
        saveButton = findViewById(R.id.save_button);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userServiceApi = retrofit.create(UserServiceApi.class);

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        usernameEditText.setText(sharedPreferences.getString("username", ""));
        phoneEditText.setText(sharedPreferences.getString("phone", ""));
        passwordEditText.setText(sharedPreferences.getString("password", ""));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "点击了头像", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSettings() {
        String username = usernameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", 0); // 假设您已经存储了用户ID
        User user = new User(userId, username, password, phone);

        Call<ResponseBody> call = userServiceApi.updateUser(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("phone", phone);
                    editor.putString("password", password);
                    editor.apply();

                    Toast.makeText(SettingsActivity.this, "设置保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}