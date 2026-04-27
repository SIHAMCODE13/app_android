package com.carrental.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.carrental.R;
import com.carrental.database.DatabaseQueries;
import com.carrental.models.Client;
import com.carrental.utils.ValidationUtils;

public class ClientFormActivity extends AppCompatActivity {

    private EditText etNom, etPrenom, etEmail, etTelephone;
    private Button btnSave;
    private DatabaseQueries dbQueries;
    private int clientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_form);

        // Initialisation des vues
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etEmail = findViewById(R.id.etEmail);
        etTelephone = findViewById(R.id.etTelephone);
        btnSave = findViewById(R.id.btnSave);

        dbQueries = new DatabaseQueries(this);

        // Vérifier si c'est une modification
        if (getIntent().hasExtra("client_id")) {
            clientId = getIntent().getIntExtra("client_id", -1);
            loadClient();
        }

        btnSave.setOnClickListener(v -> saveClient());
    }

    private void loadClient() {
        dbQueries.open();
        Client client = dbQueries.getClient(clientId);
        dbQueries.close();

        if (client != null) {
            etNom.setText(client.getNom());
            etPrenom.setText(client.getPrenom());
            etEmail.setText(client.getEmail());
            etTelephone.setText(client.getTelephone());
        }
    }

    private void saveClient() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidPhone(telephone)) {
            Toast.makeText(this, "Téléphone invalide (8+ chiffres)", Toast.LENGTH_SHORT).show();
            return;
        }

        Client client = new Client(clientId, nom, prenom, email, telephone);

        dbQueries.open();
        long result;
        if (clientId == -1) {
            result = dbQueries.addClient(client);
            if (result != -1) {
                Toast.makeText(this, "Client ajouté", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Email déjà existant", Toast.LENGTH_SHORT).show();
            }
        } else {
            result = dbQueries.updateClient(client);
            if (result > 0) {
                Toast.makeText(this, "Client modifié", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
            }
        }
        dbQueries.close();
    }
}