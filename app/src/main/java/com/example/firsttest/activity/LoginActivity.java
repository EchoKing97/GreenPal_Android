package com.example.firsttest.activity;

//LoginActivity-登录界面
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import com.example.firsttest.MainActivity;
import com.example.firsttest.R;
import com.example.firsttest.network.ResponseBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;
import java.io.IOException;
import javax.validation.constraints.NotNull;
import static com.example.firsttest.network.NetworkSettings.LOGIN;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private OkHttpClient client = new OkHttpClient();
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        //登录按钮的点击事件
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        //注册按钮的点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到注册页面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String name = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        //构造请求体
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("password", password);
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        //构造请求
        Request request = new Request.Builder()
                .url(LOGIN)
                .post(body)
                .build();

        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "登录失败：网络问题", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseBody = response.body().string();
                final int statusCode = response.code();
                runOnUiThread(() -> {
                    if (statusCode == 200) {
                        // 登录成功
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        //解析返回信息
                        ResponseBody responseBodyObj = gson.fromJson(responseBody, ResponseBody.class);
                        if (responseBodyObj != null && responseBodyObj.getUser() != null) {
                            //存储UID和用户名
                            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("userId", responseBodyObj.getUser().getId());
                            editor.putString("username", responseBodyObj.getUser().getName());
                            editor.apply();
                        }

                        //登录成功-跳转到主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (statusCode == 404) {
                        Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                    } else if (statusCode == 401) {
                        Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败：服务器错误 (" + statusCode + ")", Toast.LENGTH_SHORT).show();
                    }
                });
                response.close();
            }
        });
    }
}