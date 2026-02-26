package com.timeboundwallet.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.timeboundwallet.R;
import com.timeboundwallet.models.ApiMessageResponse;
import com.timeboundwallet.models.TransferRequest;
import com.timeboundwallet.network.ApiErrorParser;
import com.timeboundwallet.network.ApiService;
import com.timeboundwallet.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferActivity extends AppCompatActivity {

    private Long senderWalletId;
    private EditText etReceiverWalletId;
    private EditText etTransferAmount;
    private ProgressBar progressBar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        senderWalletId = getIntent().getLongExtra("senderWalletId", -1L);
        etReceiverWalletId = findViewById(R.id.etReceiverWalletId);
        etTransferAmount = findViewById(R.id.etTransferAmount);
        Button btnTransferNow = findViewById(R.id.btnTransferNow);
        progressBar = findViewById(R.id.progressBarTransfer);

        apiService = RetrofitClient.getApiService();

        btnTransferNow.setOnClickListener(v -> callTransfer());
    }

    private void callTransfer() {
        String receiverWalletText = etReceiverWalletId.getText().toString().trim();
        String amountText = etTransferAmount.getText().toString().trim();

        if (receiverWalletText.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Long receiverWalletId;
        double amount;
        try {
            receiverWalletId = Long.parseLong(receiverWalletText);
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        TransferRequest request = new TransferRequest(senderWalletId, receiverWalletId, amount);
        apiService.transfer(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiMessageResponse> call, Response<ApiMessageResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful()) {
                    Toast.makeText(TransferActivity.this,
                            ApiErrorParser.parse(response, "Transfer failed"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String message = response.body() != null ? response.body().getMessage() : "Transfer successful";
                Toast.makeText(TransferActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<ApiMessageResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TransferActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
