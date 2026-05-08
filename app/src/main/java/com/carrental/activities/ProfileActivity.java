package com.carrental.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.carrental.R;
import com.carrental.database.DatabaseQueries;
import com.carrental.models.Client;
import com.carrental.models.User;
import com.carrental.utils.SessionManager;
import com.carrental.utils.ValidationUtils;

public class ProfileActivity extends AppCompatActivity {

    private EditText etUsername, etNewPassword, etNom, etPrenom, etEmail, etTelephone;
    private LinearLayout clientSection;
    private View clientSectionDivider;
    private Button btnUpdate;
    private DatabaseQueries dbQueries;
    private SessionManager sessionManager;
    private User currentUser;
    private Client currentClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbQueries = new DatabaseQueries(this);
        sessionManager = new SessionManager(this);

        etUsername = findViewById(R.id.etUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etEmail = findViewById(R.id.etEmail);
        etTelephone = findViewById(R.id.etTelephone);
        clientSection = findViewById(R.id.clientSection);
        clientSectionDivider = findViewById(R.id.clientSectionDivider);
        btnUpdate = findViewById(R.id.btnUpdate);

        loadProfileData();

        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    private void loadProfileData() {
        dbQueries.open();
        int userId = sessionManager.getUserId();
        currentUser = dbQueries.getUserById(userId);

        if (currentUser != null) {
            etUsername.setText(currentUser.getUsername());
            
            if (sessionManager.isClient()) {
                currentClient = dbQueries.getClientByUserId(userId);
                if (currentClient != null) {
                    etNom.setText(currentClient.getNom());
                    etPrenom.setText(currentClient.getPrenom());
                    etEmail.setText(currentClient.getEmail());
                    etTelephone.setText(currentClient.getTelephone());
                }
                clientSection.setVisibility(View.VISIBLE);
                clientSectionDivider.setVisibility(View.VISIBLE);
            } else {
                clientSection.setVisibility(View.GONE);
                clientSectionDivider.setVisibility(View.GONE);
            }
        }
        dbQueries.close();
    }

    private void updateProfile() {
        String username = etUsername.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Le nom d'utilisateur est requis", Toast.LENGTH_SHORT).show();
            return;
        }

        dbQueries.open();
        
        // Update User info
        currentUser.setUsername(username);
        if (!newPassword.isEmpty()) {
            if (newPassword.length() < 4) {
                Toast.makeText(this, "Le mot de passe doit contenir au moins 4 caractères", Toast.LENGTH_SHORT).show();
                dbQueries.close();
                return;
            }
            currentUser.setPassword(newPassword);
        }

        int userResult = dbQueries.updateUser(currentUser);

        if (sessionManager.isClient() && currentClient != null) {
            String nom = etNom.getText().toString().trim();
            String prenom = etPrenom.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String telephone = etTelephone.getText().toString().trim();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs personnels", Toast.LENGTH_SHORT).show();
                dbQueries.close();
                return;
            }

            if (!ValidationUtils.isValidEmail(email)) {
                Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
                dbQueries.close();
                return;
            }

            currentClient.setNom(nom);
            currentClient.setPrenom(prenom);
            currentClient.setEmail(email);
            currentClient.setTelephone(telephone);

            dbQueries.updateClient(currentClient);
        }

        dbQueries.close();

        if (userResult > 0) {
            Toast.makeText(this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
            // Update session if username changed
            sessionManager.createLoginSession(currentUser.getId(), currentUser.getUsername(), currentUser.getRole(), sessionManager.getClientId());
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
        }
    }
}