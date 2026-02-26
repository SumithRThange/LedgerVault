package com.timeboundwallet.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.timeboundwallet.R;
import com.timeboundwallet.models.UserResponse;
import com.timeboundwallet.network.ApiService;
import com.timeboundwallet.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewUsersActivity extends AppCompatActivity {

    private static final int USER_SCAN_LIMIT = 50;

    private ProgressBar progressBar;
    private TextView tvUsersResult;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        progressBar = findViewById(R.id.progressBarViewUsers);
        tvUsersResult = findViewById(R.id.tvUsersResult);
        Button btnBack = findViewById(R.id.btnBackFromUsers);

        apiService = RetrofitClient.getApiService();

        btnBack.setOnClickListener(v -> finish());

        loadUsers();
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getAllUsers().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    loadUsersByIdFallback();
                    return;
                }

                progressBar.setVisibility(View.GONE);
                renderUsers(response.body());
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                loadUsersByIdFallback();
            }
        });
    }

    private void loadUsersByIdFallback() {
        List<UserResponse> users = new ArrayList<>();
        fetchUserByIdRecursive(1, USER_SCAN_LIMIT, users);
    }

    private void fetchUserByIdRecursive(int currentId, int maxId, List<UserResponse> users) {
        if (currentId > maxId) {
            progressBar.setVisibility(View.GONE);
            renderUsers(users);
            return;
        }

        apiService.getUserById((long) currentId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users.add(response.body());
                }
                fetchUserByIdRecursive(currentId + 1, maxId, users);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                fetchUserByIdRecursive(currentId + 1, maxId, users);
            }
        });
    }

    private void renderUsers(List<UserResponse> users) {
        if (users == null || users.isEmpty()) {
            tvUsersResult.setText("No users found");
            Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (UserResponse user : users) {
            builder.append("ID: ").append(user.getId())
                    .append(" | Name: ").append(user.getName())
                    .append(" | Email: ").append(user.getEmail())
                    .append(" | Status: ").append(Boolean.TRUE.equals(user.getActive()) ? "ACTIVE" : "DEACTIVATED")
                    .append("\n");
        }
        tvUsersResult.setText(builder.toString().trim());
    }
}
