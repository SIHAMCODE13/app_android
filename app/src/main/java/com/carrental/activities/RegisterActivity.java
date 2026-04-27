package com.carrental.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.carrental.R;
import com.carrental.database.DatabaseQueries;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Spinner spRole;
    private Button btnRegister;
    private TextView tvError;
    private DatabaseQueries dbQueries;
    private String[] roles = {"Client", "Employé"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbQueries = new DatabaseQueries(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spRole = findViewById(R.id.spRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvError = findViewById(R.id.tvError);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = spRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            tvError.setText("Veuillez remplir tous les champs");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            tvError.setText("Les mots de passe ne correspondent pas");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        if (password.length() < 4) {
            tvError.setText("Le mot de passe doit contenir au moins 4 caractères");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        dbQueries.open();
        if (dbQueries.registerUser(username, password, role)) {
            Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            tvError.setText("Nom d'utilisateur déjà existant");
            tvError.setVisibility(View.VISIBLE);
        }
        dbQueries.close();
    }
}