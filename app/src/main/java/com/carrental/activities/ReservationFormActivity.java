package com.carrental.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.carrental.R;
import com.carrental.database.DatabaseQueries;
import com.carrental.models.Car;
import com.carrental.models.Client;
import com.carrental.models.Reservation;
import com.carrental.utils.SessionManager;
import com.carrental.utils.ValidationUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReservationFormActivity extends AppCompatActivity {

    private Spinner spClient, spCar;
    private EditText etDateDebut, etDateFin;
    private TextView tvPrixTotal;
    private Button btnSave, btnCalculer;
    private DatabaseQueries dbQueries;
    private SessionManager sessionManager;
    private int reservationId = -1;
    private List<Client> clientList;
    private List<Car> carList;
    private boolean isClientMode = false;
    private int forcedClientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_form);

        try {
            sessionManager = new SessionManager(this);
            isClientMode = sessionManager.isClient();

            if (isClientMode) {
                forcedClientId = sessionManager.getClientId();
            }

            spClient = findViewById(R.id.spClient);
            spCar = findViewById(R.id.spCar);
            etDateDebut = findViewById(R.id.etDateDebut);
            etDateFin = findViewById(R.id.etDateFin);
            tvPrixTotal = findViewById(R.id.tvPrixTotal);
            btnSave = findViewById(R.id.btnSave);
            btnCalculer = findViewById(R.id.btnCalculer);

            dbQueries = new DatabaseQueries(this);
            dbQueries.open();

            if (isClientMode) {
                spClient.setVisibility(View.GONE);
                TextView tvClientLabel = findViewById(R.id.tvClientLabel);
                if (tvClientLabel != null) {
                    tvClientLabel.setVisibility(View.GONE);
                }
            }

            etDateDebut.setOnClickListener(v -> showDatePickerDialog(etDateDebut));
            etDateFin.setOnClickListener(v -> showDatePickerDialog(etDateFin));

            btnCalculer.setOnClickListener(v -> calculateTotalPrice());

            if (getIntent().hasExtra("reservation_id")) {
                reservationId = getIntent().getIntExtra("reservation_id", -1);
            }

            loadSpinners();

            if (reservationId != -1) {
                loadReservation();
            }

            btnSave.setOnClickListener(v -> saveReservation());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadSpinners() {
        try {
            clientList = dbQueries.getAllClients();
            carList = dbQueries.getAllCars();

            if (!isClientMode && clientList != null && !clientList.isEmpty()) {
                ArrayAdapter<Client> clientAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, clientList);
                clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spClient.setAdapter(clientAdapter);
            } else if (!isClientMode && (clientList == null || clientList.isEmpty())) {
                Toast.makeText(this, "Aucun client trouvé. Veuillez d'abord ajouter un client.", Toast.LENGTH_LONG).show();
            }

            if (carList != null && !carList.isEmpty()) {
                List<Car> availableCars = new java.util.ArrayList<>();
                for (Car car : carList) {
                    if (car.isDisponible() || (reservationId != -1)) {
                        availableCars.add(car);
                    }
                }

                if (availableCars.isEmpty()) {
                    Toast.makeText(this, "Aucune voiture disponible", Toast.LENGTH_LONG).show();
                }

                ArrayAdapter<Car> carAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, availableCars);
                carAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCar.setAdapter(carAdapter);
            } else {
                Toast.makeText(this, "Aucune voiture trouvée. Veuillez d'abord ajouter une voiture.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur chargement données: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadReservation() {
        try {
            List<Reservation> allReservations = dbQueries.getAllReservations();

            for (Reservation reservation : allReservations) {
                if (reservation.getId() == reservationId) {
                    if (isClientMode && reservation.getClientId() != forcedClientId) {
                        Toast.makeText(this, "Accès non autorisé", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    if (!isClientMode && clientList != null) {
                        for (int i = 0; i < clientList.size(); i++) {
                            if (clientList.get(i).getId() == reservation.getClientId()) {
                                spClient.setSelection(i);
                                break;
                            }
                        }
                    }

                    if (carList != null) {
                        for (int i = 0; i < carList.size(); i++) {
                            if (carList.get(i).getId() == reservation.getCarId()) {
                                spCar.setSelection(i);
                                break;
                            }
                        }
                    }

                    etDateDebut.setText(reservation.getDateDebut());
                    etDateFin.setText(reservation.getDateFin());
                    tvPrixTotal.setText(String.format("%.2f DT", reservation.getPrixTotal()));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = year1 + "-" + String.format("%02d", (month1 + 1)) +
                            "-" + String.format("%02d", dayOfMonth);
                    editText.setText(date);
                    if (etDateDebut.getText().toString().length() > 0 &&
                            etDateFin.getText().toString().length() > 0) {
                        calculateTotalPrice();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void calculateTotalPrice() {
        try {
            String dateDebut = etDateDebut.getText().toString().trim();
            String dateFin = etDateFin.getText().toString().trim();

            if (dateDebut.isEmpty() || dateFin.isEmpty()) {
                return;
            }

            if (!ValidationUtils.isValidDateRange(dateDebut, dateFin)) {
                Toast.makeText(this, "Date fin doit être après date début", Toast.LENGTH_SHORT).show();
                return;
            }

            Car selectedCar = (Car) spCar.getSelectedItem();
            if (selectedCar == null) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            java.util.Date start = sdf.parse(dateDebut);
            java.util.Date end = sdf.parse(dateFin);

            long diff = end.getTime() - start.getTime();
            long days = diff / (1000 * 60 * 60 * 24);

            if (days <= 0) {
                Toast.makeText(this, "Sélectionnez au moins 1 jour", Toast.LENGTH_SHORT).show();
                return;
            }

            double total = days * selectedCar.getPrixJour();
            // Formatage avec point pour éviter les problèmes de virgule
            tvPrixTotal.setText(String.format(Locale.US, "%.2f DT", total));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveReservation() {
        try {
            if (spCar.getSelectedItem() == null) {
                Toast.makeText(this, "Sélectionnez une voiture", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isClientMode && (spClient.getSelectedItem() == null || clientList == null || clientList.isEmpty())) {
                Toast.makeText(this, "Sélectionnez un client", Toast.LENGTH_SHORT).show();
                return;
            }

            String dateDebut = etDateDebut.getText().toString().trim();
            String dateFin = etDateFin.getText().toString().trim();

            if (dateDebut.isEmpty() || dateFin.isEmpty()) {
                Toast.makeText(this, "Sélectionnez les dates", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ValidationUtils.isValidDateRange(dateDebut, dateFin)) {
                Toast.makeText(this, "Date fin doit être après date début", Toast.LENGTH_SHORT).show();
                return;
            }

            int clientId;
            if (isClientMode) {
                clientId = forcedClientId;
                if (clientId == -1) {
                    Toast.makeText(this, "Erreur: Client non trouvé", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Client selectedClient = (Client) spClient.getSelectedItem();
                clientId = selectedClient.getId();
            }

            Car selectedCar = (Car) spCar.getSelectedItem();

            String prixTotalStr = tvPrixTotal.getText().toString().replace(" DT", "");
            // CORRECTION: Remplacer la virgule par un point pour Double.parseDouble
            prixTotalStr = prixTotalStr.replace(",", ".");
            double prixTotal = Double.parseDouble(prixTotalStr);

            if (prixTotal <= 0) {
                Toast.makeText(this, "Calculez d'abord le prix total", Toast.LENGTH_SHORT).show();
                return;
            }

            Reservation reservation = new Reservation(reservationId, clientId,
                    selectedCar.getId(), dateDebut, dateFin, prixTotal, "ACTIVE");

            long result;
            if (reservationId == -1) {
                result = dbQueries.addReservation(reservation);
                if (result != -1) {
                    Toast.makeText(this, "Réservation créée avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Erreur lors de la création de la réservation", Toast.LENGTH_LONG).show();
                }
            } else {
                result = dbQueries.updateReservation(reservation);
                if (result > 0) {
                    Toast.makeText(this, "Réservation modifiée", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbQueries != null) {
            dbQueries.close();
        }
    }
}