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
import com.carrental.adapters.ClientAdapter;
import com.carrental.database.DatabaseQueries;
import com.carrental.models.Client;
import java.util.List;

public class ClientListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClientAdapter adapter;
    private DatabaseQueries dbQueries;
    private List<Client> clientList;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbQueries = new DatabaseQueries(this);

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ClientListActivity.this, ClientFormActivity.class);
            startActivity(intent);
        });

        loadClients();
    }

    private void loadClients() {
        dbQueries.open();
        clientList = dbQueries.getAllClients();
        dbQueries.close();

        adapter = new ClientAdapter(clientList, this::onEditClick, this::onDeleteClick);
        recyclerView.setAdapter(adapter);
    }

    private void onEditClick(Client client) {
        Intent intent = new Intent(ClientListActivity.this, ClientFormActivity.class);
        intent.putExtra("client_id", client.getId());
        startActivity(intent);
    }

    private void onDeleteClick(Client client) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le client")
                .setMessage("Êtes-vous sûr de vouloir supprimer " + client.getPrenom() + " " + client.getNom() + "?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    dbQueries.open();
                    int result = dbQueries.deleteClient(client.getId());
                    dbQueries.close();

                    if (result == -1) {
                        Toast.makeText(this, "Impossible de supprimer: client a des réservations", Toast.LENGTH_LONG).show();
                    } else if (result > 0) {
                        Toast.makeText(this, "Client supprimé", Toast.LENGTH_SHORT).show();
                        loadClients();
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
        loadClients();
    }
}