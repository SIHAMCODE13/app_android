package com.carrental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.carrental.R;
import com.carrental.models.Reservation;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private List<Reservation> reservationList;
    private boolean canEdit;
    private boolean canCancel;
    private boolean canPay;
    private OnItemClickListener editListener;
    private OnItemClickListener cancelListener;
    private OnItemClickListener payListener;

    public interface OnItemClickListener {
        void onItemClick(Reservation reservation);
    }

    public ReservationAdapter(List<Reservation> reservationList, boolean canEdit, boolean canCancel, boolean canPay,
                              OnItemClickListener editListener, OnItemClickListener cancelListener, OnItemClickListener payListener) {
        this.reservationList = reservationList;
        this.canEdit = canEdit;
        this.canCancel = canCancel;
        this.canPay = canPay;
        this.editListener = editListener;
        this.cancelListener = cancelListener;
        this.payListener = payListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.tvClientCar.setText(reservation.getClientName() + " → " + reservation.getCarName());
        holder.tvDates.setText("Du: " + reservation.getDateDebut() + " Au: " + reservation.getDateFin());
        holder.tvPrice.setText(String.format("%.2f DT", reservation.getPrixTotal()));
        holder.tvStatus.setText(reservation.getStatut());

        int statusColor;
        String status = reservation.getStatut();
        if ("ACTIVE".equals(status)) {
            statusColor = holder.itemView.getContext().getColor(android.R.color.holo_green_dark);
        } else if ("PAID".equals(status)) {
            statusColor = holder.itemView.getContext().getColor(android.R.color.holo_blue_dark);
        } else {
            statusColor = holder.itemView.getContext().getColor(android.R.color.holo_red_dark);
        }
        holder.tvStatus.setTextColor(statusColor);

        if (canEdit && "ACTIVE".equals(status)) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> editListener.onItemClick(reservation));
        } else {
            holder.btnEdit.setVisibility(View.GONE);
        }

        if (canCancel && "ACTIVE".equals(status)) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> cancelListener.onItemClick(reservation));
        } else {
            holder.btnCancel.setVisibility(View.GONE);
        }

        if (canPay && "ACTIVE".equals(status)) {
            holder.btnPay.setVisibility(View.VISIBLE);
            holder.btnPay.setOnClickListener(v -> payListener.onItemClick(reservation));
        } else {
            holder.btnPay.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientCar, tvDates, tvPrice, tvStatus;
        ImageButton btnEdit, btnCancel, btnPay;

        ViewHolder(View itemView) {
            super(itemView);
            tvClientCar = itemView.findViewById(R.id.tvClientCar);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnPay = itemView.findViewById(R.id.btnPay);
        }
    }
}