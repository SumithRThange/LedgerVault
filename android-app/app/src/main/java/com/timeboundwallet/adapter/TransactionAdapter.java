package com.timeboundwallet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timeboundwallet.R;
import com.timeboundwallet.models.WalletTransactionResponse;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final List<WalletTransactionResponse> transactions = new ArrayList<>();

    public void setTransactions(List<WalletTransactionResponse> newTransactions) {
        transactions.clear();
        transactions.addAll(newTransactions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        WalletTransactionResponse item = transactions.get(position);
        holder.tvType.setText("Type: " + item.getType());
        holder.tvAmount.setText("Amount: " + item.getAmount());
        holder.tvStatus.setText("Status: " + item.getStatus());
        holder.tvExpiryDate.setText("Expiry: " + (item.getExpiryDate() == null ? "N/A" : item.getExpiryDate()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvType;
        TextView tvAmount;
        TextView tvStatus;
        TextView tvExpiryDate;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
        }
    }
}
