package com.timeboundwallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.timeboundwallet.R;
import com.timeboundwallet.models.UserResponse;
import com.timeboundwallet.models.UserUpdateRequest;
import com.timeboundwallet.models.WalletLimitUpdateRequest;
import com.timeboundwallet.models.WalletResponse;
import com.timeboundwallet.network.ApiErrorParser;
import com.timeboundwallet.network.ApiService;
import com.timeboundwallet.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvLoggedInUser;

    private EditText etEditUserName;
    private EditText etEditUserEmail;

    private EditText etLimitUserId;
    private EditText etNewWalletLimit;

    private ApiService apiService;
    private Long loggedInUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressBar = findViewById(R.id.progressBarSettings);
        tvLoggedInUser = findViewById(R.id.tvLoggedInUser);

        etEditUserName = findViewById(R.id.etEditUserName);
        etEditUserEmail = findViewById(R.id.etEditUserEmail);

        etLimitUserId = findViewById(R.id.etLimitUserId);
        etNewWalletLimit = findViewById(R.id.etNewWalletLimit);

        Button btnViewUsers = findViewById(R.id.btnViewUsers);
        Button btnEditUser = findViewById(R.id.btnEditUser);
        Button btnEditWalletLimit = findViewById(R.id.btnEditWalletLimit);

        apiService = RetrofitClient.getApiService();

        loggedInUserId = getIntent().getLongExtra("userId", -1L);
        if (loggedInUserId == -1L) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvLoggedInUser.setText("Logged in User ID: " + loggedInUserId);
        etLimitUserId.setText(String.valueOf(loggedInUserId));

        btnViewUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewUsersActivity.class);
            startActivity(intent);
        });
        btnEditUser.setOnClickListener(v -> editUserCredentials());
        btnEditWalletLimit.setOnClickListener(v -> editWalletLimit());
    }

    private void editUserCredentials() {
        String name = etEditUserName.getText().toString().trim();
        String email = etEditUserEmail.getText().toString().trim();

        if (name.isEmpty() && email.isEmpty()) {
            Toast.makeText(this, "Enter name or email (or both)", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        apiService.updateUser(loggedInUserId, loggedInUserId,
                new UserUpdateRequest(name.isEmpty() ? null : name,
                        email.isEmpty() ? null : email)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(SettingsActivity.this,
                            ApiErrorParser.parse(response, "User update failed"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(SettingsActivity.this,
                        "Credentials updated. Please login again.",
                        Toast.LENGTH_LONG).show();
                logoutToRegister();
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SettingsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void editWalletLimit() {
        String userIdText = etLimitUserId.getText().toString().trim();
        String maxLimitText = etNewWalletLimit.getText().toString().trim();

        if (userIdText.isEmpty() || maxLimitText.isEmpty()) {
            Toast.makeText(this, "Enter user ID and wallet limit", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId;
        double newLimit;
        try {
            userId = Long.parseLong(userIdText);
            newLimit = Double.parseDouble(maxLimitText);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        apiService.updateWalletLimit(userId, new WalletLimitUpdateRequest(newLimit)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(SettingsActivity.this,
                            ApiErrorParser.parse(response, "Wallet limit update failed"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(SettingsActivity.this,
                        "Wallet limit updated to: " + response.body().getMaxLimit(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<WalletResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SettingsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
