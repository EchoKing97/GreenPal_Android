package com.example.firsttest.activity;

//ProfileFragment-个人中心界面
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.example.firsttest.R;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Button;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.firsttest.network.UserImage;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import okhttp3.*;
import java.io.*;
import static com.example.firsttest.network.NetworkSettings.GET_USER_IMAGE;
import static com.example.firsttest.network.NetworkSettings.UPLOAD_USER_IMAGE;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage = view.findViewById(R.id.profile_image);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "未登录");
        int userId = sharedPreferences.getInt("userId", -1);

        TextView profileName = view.findViewById(R.id.profile_name);
        TextView profileUid = view.findViewById(R.id.profile_uid);
        profileName.setText(username);
        profileUid.setText("UID" + String.valueOf(userId));

        //设置按钮的点击事件
        Button settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        //退出账号按钮的点击事件
        Button logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //设置头像的点击事件
        ImageView profileImage = view.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileImageClick();
            }
        });
        loadProfileImage(userId);

        return view;
    }

    private void onProfileImageClick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("userId", String.valueOf(userId));
        builder.addFormDataPart("image", "image.jpg", RequestBody.create(MediaType.parse("image/jpeg"), imageBytes));

        Request request = new Request.Builder()
                .url(UPLOAD_USER_IMAGE)
                .post(builder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "请求失败：网络问题", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseString = response.body().string();
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "头像上传成功：" + responseString, Toast.LENGTH_SHORT).show());
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "上传头像失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void loadProfileImage(int userId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(GET_USER_IMAGE + userId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "请求失败：网络问题", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        UserImage userImage = new Gson().fromJson(responseBody, UserImage.class);
                        if (userImage != null && userImage.getImage() != null) {
                            String base64Image = userImage.getImage();
                            final String finalBase64Image = base64Image;
                            getActivity().runOnUiThread(() -> {
                                Glide.with(getActivity())
                                        .load("data:image/jpeg;base64," + finalBase64Image)
                                        .into(profileImage);
                            });
                        } else {
                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "头像数据为空", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "解析头像数据失败", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "加载头像失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                uploadImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}