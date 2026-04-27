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
import com.carrental.models.Reservation;
import com.carrental.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class PaymentHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PaymentAdapter adapter;
    private DatabaseQueries dbQueries;
    private List<PaymentItem> paymentList;
    private Spinner spFilter;
    private Button btnFilter;
    private SessionManager sessionManager;

    static class PaymentItem {
        String clientName;
        String carName;
        String date;
        double amount;
        String status;

        PaymentItem(String clientName, String carName, String date, double amount, String status) {
            this.clientName = clientName;
            this.carName = carName;
            this.date = date;
            this.amount = amount;
            this.status = status;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        spFilter = findViewById(R.id.spFilter);
        btnFilter = findViewById(R.id.btnFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbQueries = new DatabaseQueries(this);

        if (!sessionManager.isClient()) {
            String[] filters = {"Tous", "Payés", "En attente"};
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, filters);
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spFilter.setAdapter(adapterSpinner);
            btnFilter.setOnClickListener(v -> loadPayments());
        } else {
            spFilter.setVisibility(View.GONE);
            btnFilter.setVisibility(View.GONE);
        }

        loadPayments();
    }

    private void loadPayments() {
        dbQueries.open();
        List<Reservation> reservations;

        if (sessionManager.isClient()) {
            int clientId = sessionManager.getClientId();
            reservations = dbQueries.getClientReservations(clientId);
        } else {
            reservations = dbQueries.getAllReservations();
        }

        dbQueries.close();

        paymentList = new ArrayList<>();

        String filter = "Tous";
        if (!sessionManager.isClient() && spFilter.getSelectedItem() != null) {
            filter = spFilter.getSelectedItem().toString();
        }

        for (Reservation reservation : reservations) {
            String paymentStatus = reservation.getStatut().equals("ACTIVE") ? "En attente" : "Payé";

            if (filter.equals("Tous") ||
                    (filter.equals("Payés") && paymentStatus.equals("Payé")) ||
                    (filter.equals("En attente") && paymentStatus.equals("En attente"))) {

                if (reservation.getStatut().equals("CANCELLED")) {
                    paymentStatus = "Payé";
                }

                String clientDisplay = sessionManager.isClient() ? "Vous" : reservation.getClientName();

                paymentList.add(new PaymentItem(
                        clientDisplay,
                        reservation.getCarName(),
                        reservation.getDateDebut(),
                        reservation.getPrixTotal(),
                        paymentStatus
                ));
            }
        }

        adapter = new PaymentAdapter(paymentList);
        recyclerView.setAdapter(adapter);

        if (paymentList.isEmpty()) {
            TextView tvEmpty = findViewById(R.id.tvEmpty);
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            TextView tvEmpty = findViewById(R.id.tvEmpty);
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {
        private List<PaymentItem> payments;

        PaymentAdapter(List<PaymentItem> payments) {
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
            PaymentItem payment = payments.get(position);
            holder.tvClientCar.setText(payment.clientName + " - " + payment.carName);
            holder.tvDate.setText("Date: " + payment.date);
            holder.tvAmount.setText(String.format("%.2f DT", payment.amount));
            holder.tvStatus.setText(payment.status);

            if (payment.status.equals("Payé")) {
                holder.tvStatus.setTextColor(getColor(android.R.color.holo_green_dark));
            } else {
                holder.tvStatus.setTextColor(getColor(android.R.color.holo_orange_dark));
            }
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