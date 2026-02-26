package com.timeboundwallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.timeboundwallet.R;
import com.timeboundwallet.models.UserCreateRequest;
import com.timeboundwallet.models.UserResponse;
import com.timeboundwallet.models.WalletCreateRequest;
import com.timeboundwallet.models.WalletResponse;
import com.timeboundwallet.network.ApiErrorParser;
import com.timeboundwallet.network.ApiService;
import com.timeboundwallet.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etMaxLimit;
    private EditText etExistingUserId;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMaxLimit = findViewById(R.id.etMaxLimit);
        etExistingUserId = findViewById(R.id.etExistingUserId);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnGoDashboard = findViewById(R.id.btnGoDashboard);
        progressBar = findViewById(R.id.progressBar);

        apiService = RetrofitClient.getApiService();

        btnRegister.setOnClickListener(v -> registerUserAndWallet());
        btnGoDashboard.setOnClickListener(v -> goToExistingUserDashboard());
    }

    private void registerUserAndWallet() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String maxLimitText = etMaxLimit.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || maxLimitText.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double maxLimit;
        try {
            maxLimit = Double.parseDouble(maxLimitText);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Max limit must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        apiService.createUser(new UserCreateRequest(name, email)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this,
                            ApiErrorParser.parse(response, "User creation failed"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Long userId = response.body().getId();
                createWallet(userId, maxLimit);
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createWallet(Long userId, double maxLimit) {
        apiService.createWallet(new WalletCreateRequest(userId, maxLimit)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RegisterActivity.this,
                            ApiErrorParser.parse(response, "Wallet creation failed"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                WalletResponse wallet = response.body();
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                intent.putExtra("userId", wallet.getUserId());
                intent.putExtra("walletId", wallet.getWalletId());
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<WalletResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToExistingUserDashboard() {
        String existingUserIdText = etExistingUserId.getText().toString().trim();

        if (existingUserIdText.isEmpty()) {
            Toast.makeText(this, "Enter existing user ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(existingUserIdText);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "User ID must be numeric", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loadWalletAndOpenDashboard(userId);
    }

    private void loadWalletAndOpenDashboard(Long userId) {
        apiService.getWalletBalance(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(RegisterActivity.this,
                            ApiErrorParser.parse(response, "Wallet not found for this user"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                WalletResponse wallet = response.body();
                Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                intent.putExtra("userId", wallet.getUserId());
                intent.putExtra("walletId", wallet.getWalletId());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<WalletResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
