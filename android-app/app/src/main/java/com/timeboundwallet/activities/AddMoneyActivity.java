package com.timeboundwallet.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.timeboundwallet.R;
import com.timeboundwallet.models.AddMoneyRequest;
import com.timeboundwallet.models.WalletResponse;
import com.timeboundwallet.network.ApiErrorParser;
import com.timeboundwallet.network.ApiService;
import com.timeboundwallet.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMoneyActivity extends AppCompatActivity {

    private Long userId;
    private EditText etAmount;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        userId = getIntent().getLongExtra("userId", -1L);
        etAmount = findViewById(R.id.etAddAmount);
        Button btnSubmit = findViewById(R.id.btnSubmitAddMoney);
        progressBar = findViewById(R.id.progressBarAddMoney);

        apiService = RetrofitClient.getApiService();

        btnSubmit.setOnClickListener(v -> callAddMoney());
    }

    private void callAddMoney() {
        String amountText = etAmount.getText().toString().trim();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Amount is required", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Amount must be numeric", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        apiService.addMoney(userId, new AddMoneyRequest(amount)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AddMoneyActivity.this,
                            ApiErrorParser.parse(response, "Add money failed"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(AddMoneyActivity.this,
                        "Money added. New balance: " + response.body().getBalance(),
                        Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<WalletResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddMoneyActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
