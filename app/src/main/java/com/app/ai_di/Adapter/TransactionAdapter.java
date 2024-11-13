package com.app.ai_di.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ai_di.R;
import com.app.ai_di.model.Transanction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final Context context;
    private final List<Transanction> transactionList;

    public TransactionAdapter(Context context, List<Transanction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.redeem_list_layout, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transanction transaction = transactionList.get(position);

        // Bind the data to the views
        holder.tvStatus.setText(getStatusTitle(transaction.getType(), transaction.getCodes()));
        holder.tvType.setText(transaction.getDatetime());
        holder.tvAmount.setText("₹" + transaction.getAmount());
        holder.tvDateTime.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    private String getStatusTitle(String type, String code) {
        switch (type) {
            case "earning_wallet":
                return "Earning Wallet to Balance";
            case "bonus_wallet":
                return "Bonus wallet to Balance";
            case "refer_bonus":
                return "Refer Bonus";
            case "recharge":
                return "Recharge";
            case "plan_activated":
                return "Plan activated";
            case "sync_wallet":
            case "Generated":
                return "Sync Amount to Earning Wallet";
            case "cancelled":
                return "Cancelled withdrawal amount credited";
            case "admin_credit_balance":
                return "Amount credited by admin";
            default:
                return "Transaction";
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        final TextView tvStatus;
        final TextView tvType;
        final TextView tvAmount;
        final TextView tvDateTime;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvType = itemView.findViewById(R.id.tvType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
        }
    }
}


//public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ItemHolder> {
//    final Activity activity;
//    final ArrayList<Transanction> transactions;
//
//    public TransactionAdapter(Activity activity, ArrayList<Transanction> wallets) {
//        this.activity = activity;
//        this.transactions = wallets;
//    }
//
//    @NonNull
//    @Override
//    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(activity).inflate(R.layout.redeem_list_layout, parent, false);
//        return new ItemHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
//        Transanction transaction = transactions.get(position);
//
//        switch (transaction.getType()) {
//            case "refer_bonus":
//                holder.tvTitle.setText("Refer bonus added by admin");
//                break;
//            case "code_bonus":
//                holder.tvTitle.setText(transaction.getCodes() + " Codes added by admin");
//                break;
//            case "register_bonus":
//                holder.tvTitle.setText(transaction.getCodes() + " Codes added by admin");
//                break;
//            case "cancelled":
//                holder.tvTitle.setText("Cancelled withdrawal amount credited");
//                break;
//            case "admin_credit_balance":
//                holder.tvTitle.setText("Amount credited by admin");
//                break;
//            default:
//                holder.tvTitle.setText("Amount credited For Qr Code");
//                break;
//        }
//
//        holder.tvType.setText(transaction.getDatetime());
//        holder.tvDateTime.setVisibility(View.GONE);  // If not needed, you can show it later if required
//        holder.tvAmount.setText("₹" + transaction.getAmount());
//    }
//
//    @Override
//    public int getItemCount() {
//        return transactions.size();
//    }
//
//    static class ItemHolder extends RecyclerView.ViewHolder {
//        final TextView tvTitle, tvDateTime, tvAmount, tvType;
//
//        public ItemHolder(@NonNull View itemView) {
//            super(itemView);
//            tvTitle = itemView.findViewById(R.id.tvTitle);
//            tvDateTime = itemView.findViewById(R.id.tvDateTime);
//            tvType = itemView.findViewById(R.id.tvType);
//            tvAmount = itemView.findViewById(R.id.tvAmount);
//        }
//    }
//}


