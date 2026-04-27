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
    }

    private void loadSpinners() {
        dbQueries.open();
        clientList = dbQueries.getAllClients();
        carList = dbQueries.getAllCars();
        dbQueries.close();

        if (!isClientMode) {
            ArrayAdapter<Client> clientAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, clientList);
            clientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spClient.setAdapter(clientAdapter);
        }

        List<Car> availableCars = new java.util.ArrayList<>();
        for (Car car : carList) {
            if (car.isDisponible() || (reservationId != -1)) {
                availableCars.add(car);
            }
        }

        ArrayAdapter<Car> carAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, availableCars);
        carAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCar.setAdapter(carAdapter);
    }

    private void loadReservation() {
        dbQueries.open();
        List<Reservation> allReservations = dbQueries.getAllReservations();
        dbQueries.close();

        for (Reservation reservation : allReservations) {
            if (reservation.getId() == reservationId) {
                if (isClientMode && reservation.getClientId() != forcedClientId) {
                    Toast.makeText(this, "Accès non autorisé", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                if (!isClientMode) {
                    for (int i = 0; i < clientList.size(); i++) {
                        if (clientList.get(i).getId() == reservation.getClientId()) {
                            spClient.setSelection(i);
                            break;
                        }
                    }
                }

                for (int i = 0; i < carList.size(); i++) {
                    if (carList.get(i).getId() == reservation.getCarId()) {
                        spCar.setSelection(i);
                        break;
                    }
                }

                etDateDebut.setText(reservation.getDateDebut());
                etDateFin.setText(reservation.getDateFin());
                tvPrixTotal.setText(String.format("%.2f DT", reservation.getPrixTotal()));
                break;
            }
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

        Car selectedCar = (Car) spCar.getSelectedItem();
        if (selectedCar == null) {
            Toast.makeText(this, "Sélectionnez une voiture", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
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
            tvPrixTotal.setText(String.format("%.2f DT", total));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveReservation() {
        if (spCar.getSelectedItem() == null) {
            Toast.makeText(this, "Sélectionnez une voiture", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isClientMode && spClient.getSelectedItem() == null) {
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
        } else {
            Client selectedClient = (Client) spClient.getSelectedItem();
            clientId = selectedClient.getId();
        }

        Car selectedCar = (Car) spCar.getSelectedItem();

        String prixTotalStr = tvPrixTotal.getText().toString().replace(" DT", "");
        double prixTotal = Double.parseDouble(prixTotalStr);

        Reservation reservation = new Reservation(reservationId, clientId,
                selectedCar.getId(), dateDebut, dateFin, prixTotal, "ACTIVE");

        dbQueries.open();
        long result;
        if (reservationId == -1) {
            result = dbQueries.addReservation(reservation);
            if (result != -1) {
                Toast.makeText(this, "Réservation créée avec succès", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la création", Toast.LENGTH_SHORT).show();
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
        dbQueries.close();
    }
}