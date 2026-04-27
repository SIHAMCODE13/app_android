package com.carrental.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.carrental.R;
import com.carrental.database.DatabaseQueries;
import com.carrental.models.User;
import com.carrental.utils.NotificationHelper;
import com.carrental.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private TextView tvError;
    private DatabaseQueries dbQueries;
    private SessionManager sessionManager;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbQueries = new DatabaseQueries(this);
        sessionManager = new SessionManager(this);
        notificationHelper = new NotificationHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvError = findViewById(R.id.tvError);

        btnLogin.setOnClickListener(v -> login());
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText("Veuillez remplir tous les champs");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        dbQueries.open();
        if (dbQueries.validateUser(username, password)) {
            User user = dbQueries.getUser(username);

            int clientId = -1;
            if (user.getRole().equals("Client")) {
                com.carrental.models.Client client = dbQueries.getClientByUserId(user.getId());
                if (client != null) {
                    clientId = client.getId();
                } else {
                    Toast.makeText(this, "Erreur: Profil client incomplet", Toast.LENGTH_SHORT).show();
                    dbQueries.close();
                    return;
                }
            }

            sessionManager.createLoginSession(user.getId(), user.getUsername(), user.getRole(), clientId);
            dbQueries.close();

            // Envoyer notification de bienvenue
            notificationHelper.showWelcomeNotification(user.getUsername(), user.getRole());

            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        } else {
            tvError.setText("Nom d'utilisateur ou mot de passe incorrect");
            tvError.setVisibility(View.VISIBLE);
        }
        dbQueries.close();
    }
}