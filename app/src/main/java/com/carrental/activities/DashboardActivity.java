package com.carrental.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
        requestNotificationPermission();

        sessionManager = new SessionManager(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        cardCars = findViewById(R.id.cardCars);
        cardClients = findViewById(R.id.cardClients);
        cardReservations = findViewById(R.id.cardReservations);
        cardPayments = findViewById(R.id.cardPayments);

        String role = sessionManager.getRole();
        tvWelcome.setText("Bienvenue, " + sessionManager.getUsername() + " (" + role + ")");

        // Cacher la carte Clients pour les clients
        if (sessionManager.isClient()) {
            cardClients.setVisibility(View.GONE);
        }

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
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }
}