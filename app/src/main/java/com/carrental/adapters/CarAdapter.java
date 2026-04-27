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
    private OnItemClickListener editListener;
    private OnItemClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(Car car);
    }

    public CarAdapter(List<Car> carList, OnItemClickListener editListener, OnItemClickListener deleteListener) {
        this.carList = carList;
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

        holder.btnEdit.setOnClickListener(v -> editListener.onItemClick(car));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onItemClick(car));
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