package com.carrental.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.carrental.R;
import com.carrental.adapters.CarAdapter;
import com.carrental.database.DatabaseQueries;
import com.carrental.models.Car;
import java.util.List;

public class CarListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CarAdapter adapter;
    private DatabaseQueries dbQueries;
    private List<Car> carList;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbQueries = new DatabaseQueries(this);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(CarListActivity.this, CarFormActivity.class);
            startActivity(intent);
        });

        loadCars();
    }

    private void loadCars() {
        dbQueries.open();
        carList = dbQueries.getAllCars();
        dbQueries.close();

        adapter = new CarAdapter(carList, this::onEditClick, this::onDeleteClick);
        recyclerView.setAdapter(adapter);
    }

    private void onEditClick(Car car) {
        Intent intent = new Intent(CarListActivity.this, CarFormActivity.class);
        intent.putExtra("car_id", car.getId());
        startActivity(intent);
    }

    private void onDeleteClick(Car car) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la voiture")
                .setMessage("Êtes-vous sûr de vouloir supprimer " + car.getMarque() + " " + car.getModele() + "?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    dbQueries.open();
                    int result = dbQueries.deleteCar(car.getId());
                    dbQueries.close();

                    if (result == -1) {
                        Toast.makeText(this, "Impossible de supprimer: voiture réservée", Toast.LENGTH_LONG).show();
                    } else if (result > 0) {
                        Toast.makeText(this, "Voiture supprimée", Toast.LENGTH_SHORT).show();
                        loadCars();
                    } else {
                        Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCars();
    }
}