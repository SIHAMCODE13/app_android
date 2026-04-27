package com.carrental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.carrental.R;
import com.carrental.models.Client;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {

    private List<Client> clientList;
    private boolean canEdit;
    private boolean canDelete;
    private OnItemClickListener editListener;
    private OnItemClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(Client client);
    }

    public ClientAdapter(List<Client> clientList, boolean canEdit, boolean canDelete,
                         OnItemClickListener editListener, OnItemClickListener deleteListener) {
        this.clientList = clientList;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Client client = clientList.get(position);
        holder.tvClientName.setText(client.getPrenom() + " " + client.getNom());
        holder.tvEmail.setText(client.getEmail());
        holder.tvPhone.setText(client.getTelephone());

        if (canEdit) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> editListener.onItemClick(client));
        } else {
            holder.btnEdit.setVisibility(View.GONE);
        }

        if (canDelete) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> deleteListener.onItemClick(client));
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvEmail, tvPhone;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}