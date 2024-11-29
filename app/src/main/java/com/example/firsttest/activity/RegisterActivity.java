package com.example.firsttest.activity;

//RegisterActivity-注册界面
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.firsttest.R;
import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import javax.validation.constraints.NotNull;
import static com.example.firsttest.network.NetworkSettings.SIGN_IN_UP;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;
    private OkHttpClient client = new OkHttpClient();
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username_register);
        phoneEditText = findViewById(R.id.phone_register);
        passwordEditText = findViewById(R.id.password_register);
        confirmPasswordEditText = findViewById(R.id.confirm_password_register);
        registerButton = findViewById(R.id.register_button);

        //注册按钮的点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        //验证手机号11位
        if (phone.length() != 11) {
            Toast.makeText(RegisterActivity.this, "手机号必须是11位", Toast.LENGTH_SHORT).show();
            return;
        }

        //验证密码长度大于6位
        if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "密码长度必须大于6位", Toast.LENGTH_SHORT).show();
            return;
        }

        //验证两次输入的密码一致
        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        //构造请求体
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", username);
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("password", password);
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        // 构造请求
        Request request = new Request.Builder()
                .url(SIGN_IN_UP)
                .post(body)
                .build();

        //发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "注册失败：网络问题", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    //跳转到登录页面
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "注册失败：服务器错误 (" + response.code() + ")", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}