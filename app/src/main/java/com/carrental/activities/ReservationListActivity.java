package com.carrental.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.carrental.R;
import com.carrental.adapters.ReservationAdapter;
import com.carrental.database.DatabaseQueries;
import com.carrental.models.Reservation;
import com.carrental.utils.SessionManager;
import java.util.List;

public class ReservationListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private DatabaseQueries dbQueries;
    private List<Reservation> reservationList;
    private Button btnAdd;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);

        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbQueries = new DatabaseQueries(this);

        // Tout le monde peut ajouter une réservation
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationListActivity.this, ReservationFormActivity.class);
            startActivity(intent);
        });

        loadReservations();
    }

    private void loadReservations() {
        dbQueries.open();

        // Si c'est un client, ne voir que ses propres réservations
        if (sessionManager.isClient()) {
            // Récupérer l'ID du client connecté
            // Pour simplifier, on utilise l'ID de session
            int clientId = sessionManager.getUserId();
            reservationList = dbQueries.getClientReservations(clientId);
        } else {
            reservationList = dbQueries.getAllReservations();
        }

        dbQueries.close();

        boolean canEdit = sessionManager.canModify();
        boolean canCancel = true; // Tout le monde peut annuler ses réservations

        adapter = new ReservationAdapter(reservationList, canEdit, canCancel,
                this::onEditClick, this::onCancelClick);
        recyclerView.setAdapter(adapter);
    }

    private void onEditClick(Reservation reservation) {
        if (sessionManager.canModify()) {
            Intent intent = new Intent(ReservationListActivity.this, ReservationFormActivity.class);
            intent.putExtra("reservation_id", reservation.getId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Vous ne pouvez pas modifier les réservations", Toast.LENGTH_SHORT).show();
        }
    }

    private void onCancelClick(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Annuler la réservation")
                .setMessage("Êtes-vous sûr de vouloir annuler cette réservation?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    dbQueries.open();
                    int result = dbQueries.cancelReservation(reservation.getId());
                    dbQueries.close();

                    if (result > 0) {
                        Toast.makeText(this, "Réservation annulée", Toast.LENGTH_SHORT).show();
                        loadReservations();
                    } else {
                        Toast.makeText(this, "Erreur lors de l'annulation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
    }
}