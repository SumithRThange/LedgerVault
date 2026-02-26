package com.timeboundwallet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.timeboundwallet.R;
import com.timeboundwallet.models.WalletResponse;
import com.timeboundwallet.network.ApiService;
import com.timeboundwallet.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private Long userId;
    private Long walletId;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        userId = getIntent().getLongExtra("userId", -1L);
        walletId = getIntent().getLongExtra("walletId", -1L);

        progressBar = findViewById(R.id.progressBarDashboard);

        Button btnViewBalance = findViewById(R.id.btnViewBalance);
        Button btnAddMoney = findViewById(R.id.btnGoAddMoney);
        Button btnTransfer = findViewById(R.id.btnGoTransfer);
        Button btnTransactions = findViewById(R.id.btnGoTransactions);
        Button btnSettings = findViewById(R.id.btnGoSettings);
        Button btnCreateNewUser = findViewById(R.id.btnCreateNewUser);

        apiService = RetrofitClient.getApiService();

        btnViewBalance.setOnClickListener(v -> {
            Intent intent = new Intent(this, BalanceActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnAddMoney.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMoneyActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        btnTransfer.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransferActivity.class);
            intent.putExtra("senderWalletId", walletId);
            startActivity(intent);
        });

        btnTransactions.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransactionHistoryActivity.class);
            intent.putExtra("walletId", walletId);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("walletId", walletId);
            startActivity(intent);
        });

        btnCreateNewUser.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWalletReference();
    }

    private void refreshWalletReference() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getWalletBalance(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(DashboardActivity.this, "Could not load wallet", Toast.LENGTH_SHORT).show();
                    return;
                }

                WalletResponse wallet = response.body();
                walletId = wallet.getWalletId();
            }

            @Override
            public void onFailure(Call<WalletResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DashboardActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
