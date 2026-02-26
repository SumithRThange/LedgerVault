package com.timeboundwallet.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.timeboundwallet.R;
import com.timeboundwallet.models.WalletResponse;
import com.timeboundwallet.network.ApiService;
import com.timeboundwallet.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BalanceActivity extends AppCompatActivity {

    private Long userId;
    private TextView tvWalletId;
    private TextView tvBalance;
    private TextView tvMaxLimit;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        userId = getIntent().getLongExtra("userId", -1L);

        tvWalletId = findViewById(R.id.tvBalanceWalletId);
        tvBalance = findViewById(R.id.tvBalanceAmount);
        tvMaxLimit = findViewById(R.id.tvBalanceMaxLimit);
        progressBar = findViewById(R.id.progressBarBalance);

        apiService = RetrofitClient.getApiService();

        if (userId == -1L) {
            Toast.makeText(this, "Invalid user session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadBalance();
    }

    private void loadBalance() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getWalletBalance(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(BalanceActivity.this, "Could not load balance", Toast.LENGTH_SHORT).show();
                    return;
                }

                WalletResponse wallet = response.body();
                tvWalletId.setText("Wallet ID: " + wallet.getWalletId());
                tvBalance.setText("Balance: " + wallet.getBalance());
                tvMaxLimit.setText("Max Limit: " + wallet.getMaxLimit());
            }

            @Override
            public void onFailure(Call<WalletResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BalanceActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
