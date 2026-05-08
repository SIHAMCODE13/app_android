package com.carrental.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.carrental.R;
import com.carrental.database.DatabaseQueries;
import com.carrental.models.Car;
import com.carrental.utils.ValidationUtils;

public class CarFormActivity extends AppCompatActivity {

    private EditText etMarque, etModele, etAnnee, etPrixJour;
    private CheckBox cbDisponible;
    private ImageView ivCar;
    private Button btnSave, btnSelectImage;
    private DatabaseQueries dbQueries;
    private int carId = -1;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        // Persist permissions for the URI
                        getContentResolver().takePersistableUriPermission(selectedImageUri, 
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        ivCar.setImageURI(selectedImageUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_form);

        etMarque = findViewById(R.id.etMarque);
        etModele = findViewById(R.id.etModele);
        etAnnee = findViewById(R.id.etAnnee);
        etPrixJour = findViewById(R.id.etPrixJour);
        cbDisponible = findViewById(R.id.cbDisponible);
        ivCar = findViewById(R.id.ivCar);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);

        dbQueries = new DatabaseQueries(this);

        if (getIntent().hasExtra("car_id")) {
            carId = getIntent().getIntExtra("car_id", -1);
            loadCar();
        }

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> saveCar());
    }

    private void loadCar() {
        dbQueries.open();
        Car car = dbQueries.getCar(carId);
        dbQueries.close();

        if (car != null) {
            etMarque.setText(car.getMarque());
            etModele.setText(car.getModele());
            etAnnee.setText(String.valueOf(car.getAnnee()));
            etPrixJour.setText(String.valueOf(car.getPrixJour()));
            cbDisponible.setChecked(car.isDisponible());
            
            if (car.getImage() != null && !car.getImage().isEmpty()) {
                selectedImageUri = Uri.parse(car.getImage());
                try {
                    ivCar.setImageURI(selectedImageUri);
                } catch (Exception e) {
                    ivCar.setImageResource(R.drawable.ic_car_white);
                }
            }
        }
    }

    private void saveCar() {
        String marque = etMarque.getText().toString().trim();
        String modele = etModele.getText().toString().trim();
        String anneeStr = etAnnee.getText().toString().trim();
        String prixStr = etPrixJour.getText().toString().trim();

        if (marque.isEmpty() || modele.isEmpty() || anneeStr.isEmpty() || prixStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        int annee = Integer.parseInt(anneeStr);
        double prixJour = Double.parseDouble(prixStr);

        if (!ValidationUtils.isValidYear(annee)) {
            Toast.makeText(this, "Année invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidPrice(prixJour)) {
            Toast.makeText(this, "Prix invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagePath = (selectedImageUri != null) ? selectedImageUri.toString() : "";
        Car car = new Car(carId, marque, modele, annee, prixJour, cbDisponible.isChecked(), imagePath);

        dbQueries.open();
        long result;
        if (carId == -1) {
            result = dbQueries.addCar(car);
            if (result != -1) {
                Toast.makeText(this, "Voiture ajoutée", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
            }
        } else {
            result = dbQueries.updateCar(car);
            if (result > 0) {
                Toast.makeText(this, "Voiture modifiée", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
            }
        }
        dbQueries.close();
    }
}