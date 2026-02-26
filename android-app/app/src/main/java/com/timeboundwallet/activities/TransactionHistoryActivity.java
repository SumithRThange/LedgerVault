package com.timeboundwallet.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timeboundwallet.R;
import com.timeboundwallet.adapter.TransactionAdapter;
import com.timeboundwallet.models.WalletTransactionResponse;
import com.timeboundwallet.network.ApiService;
import com.timeboundwallet.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity {

    private Long walletId;
    private ProgressBar progressBar;
    private TransactionAdapter adapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        walletId = getIntent().getLongExtra("walletId", -1L);
        progressBar = findViewById(R.id.progressBarTransactions);
        RecyclerView recyclerView = findViewById(R.id.recyclerTransactions);

        adapter = new TransactionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getApiService();

        loadTransactions();
    }

    private void loadTransactions() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getTransactions(walletId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<WalletTransactionResponse>> call,
                                   Response<List<WalletTransactionResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(TransactionHistoryActivity.this, "Could not load transactions", Toast.LENGTH_SHORT).show();
                    return;
                }

                adapter.setTransactions(response.body());
            }

            @Override
            public void onFailure(Call<List<WalletTransactionResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TransactionHistoryActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
