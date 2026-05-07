package com.carrental.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.carrental.R;
import com.carrental.database.DatabaseQueries;
import com.carrental.models.Client;
import com.carrental.models.User;
import com.carrental.utils.SessionManager;
import com.carrental.utils.ValidationUtils;

public class ClientFormActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etNom, etPrenom, etEmail, etTelephone, etSolde;
    private TextView tvHeader;
    private Button btnSave;
    private DatabaseQueries dbQueries;
    private SessionManager sessionManager;
    private int clientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_form);

        sessionManager = new SessionManager(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etEmail = findViewById(R.id.etEmail);
        etTelephone = findViewById(R.id.etTelephone);
        etSolde = findViewById(R.id.etSolde);
        btnSave = findViewById(R.id.btnSave);
        tvHeader = findViewById(R.id.tvUserAccountHeader);

        dbQueries = new DatabaseQueries(this);

        if (getIntent().hasExtra("client_id")) {
            clientId = getIntent().getIntExtra("client_id", -1);
            loadClient();
            // In edit mode, we hide username/password for simplicity in this version
            etUsername.setVisibility(View.GONE);
            etPassword.setVisibility(View.GONE);
            tvHeader.setVisibility(View.GONE);
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
            etSolde.setText(String.valueOf(client.getSolde()));
        }
    }

    private void saveClient() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String soldeStr = etSolde.getText().toString().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs personnels", Toast.LENGTH_SHORT).show();
            return;
        }

        if (clientId == -1 && (username.isEmpty() || password.isEmpty())) {
            Toast.makeText(this, "Veuillez saisir un nom d'utilisateur et un mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }

        double solde = 0;
        if (!soldeStr.isEmpty()) {
            try {
                solde = Double.parseDouble(soldeStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Solde invalide", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!ValidationUtils.isValidEmail(email)) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidPhone(telephone)) {
            Toast.makeText(this, "Téléphone invalide (8+ chiffres)", Toast.LENGTH_SHORT).show();
            return;
        }

        dbQueries.open();

        if (clientId == -1) {
            // Create user account first
            if (dbQueries.registerUser(username, password, "Client")) {
                int userId = dbQueries.getLastInsertedUserId();
                Client client = new Client(0, nom, prenom, email, telephone, userId, solde);
                long result = dbQueries.addClient(client);
                if (result != -1) {
                    Toast.makeText(this, "Client et compte créés", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Erreur lors de la création du client", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Nom d'utilisateur déjà utilisé", Toast.LENGTH_SHORT).show();
            }
        } else {
            Client existingClient = dbQueries.getClient(clientId);
            int userId = existingClient != null ? existingClient.getUserId() : -1;

            Client client = new Client(clientId, nom, prenom, email, telephone, userId, solde);
            int result = dbQueries.updateClient(client);
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