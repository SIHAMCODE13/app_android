package com.carrental.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.carrental.R;
import com.carrental.utils.SessionManager;

public class DashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvWelcome;
    private Button btnLogout;
    private CardView cardCars, cardClients, cardReservations, cardPayments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        cardCars = findViewById(R.id.cardCars);
        cardClients = findViewById(R.id.cardClients);
        cardReservations = findViewById(R.id.cardReservations);
        cardPayments = findViewById(R.id.cardPayments);

        tvWelcome.setText("Bienvenue, " + sessionManager.getUsername() + " (" + sessionManager.getRole() + ")");

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
            Toast.makeText(DashboardActivity.this, "Déconnecté", Toast.LENGTH_SHORT).show();
        });

        cardCars.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, CarListActivity.class)));

        cardClients.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ClientListActivity.class)));

        cardReservations.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ReservationListActivity.class)));

        cardPayments.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, PaymentHistoryActivity.class)));
    }
}