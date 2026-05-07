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
import com.carrental.utils.NotificationHelper;
import com.carrental.utils.SessionManager;
import java.util.List;

public class ReservationListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private DatabaseQueries dbQueries;
    private List<Reservation> reservationList;
    private Button btnAdd;
    private SessionManager sessionManager;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);

        sessionManager = new SessionManager(this);
        notificationHelper = new NotificationHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbQueries = new DatabaseQueries(this);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationListActivity.this, ReservationFormActivity.class);
            startActivity(intent);
        });

        loadReservations();
    }

    private void loadReservations() {
        try {
            dbQueries.open();

            if (sessionManager.isClient()) {
                int clientId = sessionManager.getClientId();
                if (clientId != -1) {
                    reservationList = dbQueries.getClientReservations(clientId);
                } else {
                    reservationList = new java.util.ArrayList<>();
                    Toast.makeText(this, "Aucune réservation trouvée pour ce client", Toast.LENGTH_SHORT).show();
                }
            } else {
                reservationList = dbQueries.getAllReservations();
            }

            dbQueries.close();

            boolean canEdit = sessionManager.canModify();
            boolean canCancel = true;
            boolean canPay = sessionManager.isClient();

            adapter = new ReservationAdapter(reservationList, canEdit, canCancel, canPay,
                    this::onEditClick, this::onCancelClick, this::onPayClick);
            recyclerView.setAdapter(adapter);

            if (reservationList == null || reservationList.isEmpty()) {
                Toast.makeText(this, "Aucune réservation trouvée", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur chargement: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
                        if (sessionManager.isClient()) {
                            notificationHelper.showClientCancellationNotification(reservation.getCarName());
                        } else {
                            notificationHelper.showReservationCancelledNotification(
                                    reservation.getClientName(),
                                    reservation.getCarName()
                            );
                        }
                        Toast.makeText(this, "Réservation annulée", Toast.LENGTH_SHORT).show();
                        loadReservations();
                    } else {
                        Toast.makeText(this, "Erreur lors de l'annulation", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private void onPayClick(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Paiement")
                .setMessage("Voulez-vous payer " + String.format("%.2f DT", reservation.getPrixTotal()) + " pour cette réservation ?")
                .setPositiveButton("Payer", (dialog, which) -> {
                    dbQueries.open();
                    String result = dbQueries.processPayment(
                            reservation.getId(),
                            reservation.getClientId(),
                            reservation.getPrixTotal()
                    );
                    dbQueries.close();

                    if ("SUCCESS".equals(result)) {
                        Toast.makeText(this, "Paiement réussi !", Toast.LENGTH_SHORT).show();
                        loadReservations();
                    } else {
                        Toast.makeText(this, "Paiement échoué: " + result, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
    }
}