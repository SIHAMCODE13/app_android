package com.carrental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.carrental.R;
import com.carrental.models.Car;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private List<Car> carList;
    private boolean canEdit;
    private boolean canDelete;
    private OnItemClickListener editListener;
    private OnItemClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(Car car);
    }

    public CarAdapter(List<Car> carList, boolean canEdit, boolean canDelete,
                      OnItemClickListener editListener, OnItemClickListener deleteListener) {
        this.carList = carList;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.tvCarInfo.setText(car.getMarque() + " " + car.getModele() + " (" + car.getAnnee() + ")");
        holder.tvPrice.setText(String.format("%.2f DT/jour", car.getPrixJour()));
        holder.tvStatus.setText(car.isDisponible() ? "Disponible" : "Indisponible");
        holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(
                car.isDisponible() ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));

        // Gérer l'affichage des boutons selon les permissions
        if (canEdit) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> editListener.onItemClick(car));
        } else {
            holder.btnEdit.setVisibility(View.GONE);
        }

        if (canDelete) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> deleteListener.onItemClick(car));
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCarInfo, tvPrice, tvStatus;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvCarInfo = itemView.findViewById(R.id.tvCarInfo);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}