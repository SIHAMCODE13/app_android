package com.carrental.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.carrental.R;
import com.carrental.database.DatabaseQueries;
import com.carrental.models.Payment;
import com.carrental.utils.SessionManager;
import java.util.List;

public class PaymentHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PaymentAdapter adapter;
    private DatabaseQueries dbQueries;
    private SessionManager sessionManager;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbQueries = new DatabaseQueries(this);

        // Hide filter for now as we are switching to actual payment records
        View filterContainer = findViewById(R.id.filterContainer);
        if (filterContainer != null) filterContainer.setVisibility(View.GONE);

        loadPayments();
    }

    private void loadPayments() {
        dbQueries.open();
        List<Payment> payments = dbQueries.getAllPayments();
        dbQueries.close();

        if (payments.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new PaymentAdapter(payments);
            recyclerView.setAdapter(adapter);
        }
    }

    class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
        private List<Payment> payments;

        PaymentAdapter(List<Payment> payments) {
            this.payments = payments;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_payment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Payment payment = payments.get(position);
            holder.tvClientCar.setText(payment.getClientName() + " - " + payment.getCarName());
            holder.tvDate.setText("Payé le: " + payment.getPaymentDate());
            holder.tvAmount.setText(String.format("%.2f DT", payment.getAmount()));
            holder.tvStatus.setText("Confirmé");
            holder.tvStatus.setTextColor(getColor(android.R.color.holo_green_dark));
        }

        @Override
        public int getItemCount() {
            return payments.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvClientCar, tvDate, tvAmount, tvStatus;

            ViewHolder(View itemView) {
                super(itemView);
                tvClientCar = itemView.findViewById(R.id.tvClientCar);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvStatus = itemView.findViewById(R.id.tvStatus);
            }
        }
    }
}