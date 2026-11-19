package com.example.medilab.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.models.Appointment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface OnAppointmentActionListener {
        void onReschedule(Appointment appointment);
        void onCancel(Appointment appointment);
    }

    private final Context context;
    private ArrayList<Appointment> appointments;
    private final OnAppointmentActionListener listener;

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointments, OnAppointmentActionListener listener) {
        this.context = context;
        this.appointments = appointments != null ? appointments : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(ArrayList<Appointment> newList) {
        this.appointments = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment apt = appointments.get(position);

        holder.tvAptTestName.setText(apt.getTestName() != null ? apt.getTestName() : "Diagnostic Test");
        holder.tvAptDateTime.setText(apt.getAppointmentDate() + " at " + apt.getAppointmentTime());
        holder.tvAptMode.setText("Mode: " + (apt.getCollectionType() != null ? apt.getCollectionType() : "Lab Visit"));
        holder.tvAptAddress.setText("Address: " + (apt.getAddress() != null && !apt.getAddress().isEmpty() ? apt.getAddress() : "Lab Center"));
        holder.tvAptId.setText("Apt #" + apt.getAppointmentId() + " • " + (apt.getPatientName() != null ? apt.getPatientName() : ""));

        String status = apt.getStatus() != null ? apt.getStatus() : "Pending";
        holder.tvAptStatusBadge.setText(status);

        if (status.equalsIgnoreCase("Confirmed")) {
            holder.tvAptStatusBadge.setTextColor(Color.rgb(0, 150, 136)); // teal
            holder.btnReschedule.setVisibility(View.VISIBLE);
            holder.btnCancelApt.setVisibility(View.VISIBLE);
        } else if (status.equalsIgnoreCase("Cancelled")) {
            holder.tvAptStatusBadge.setTextColor(Color.rgb(211, 47, 47)); // red
            holder.btnReschedule.setVisibility(View.GONE);
            holder.btnCancelApt.setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase("Completed")) {
            holder.tvAptStatusBadge.setTextColor(Color.rgb(56, 142, 60)); // green
            holder.btnReschedule.setVisibility(View.GONE);
            holder.btnCancelApt.setVisibility(View.GONE);
        } else {
            holder.tvAptStatusBadge.setTextColor(Color.rgb(245, 124, 0)); // orange
            holder.btnReschedule.setVisibility(View.VISIBLE);
            holder.btnCancelApt.setVisibility(View.VISIBLE);
        }

        holder.btnReschedule.setOnClickListener(v -> {
            if (listener != null) listener.onReschedule(apt);
        });

        holder.btnCancelApt.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(apt);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAptTestName, tvAptStatusBadge, tvAptDateTime, tvAptMode, tvAptAddress, tvAptId;
        MaterialButton btnReschedule, btnCancelApt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAptTestName = itemView.findViewById(R.id.tvAptTestName);
            tvAptStatusBadge = itemView.findViewById(R.id.tvAptStatusBadge);
            tvAptDateTime = itemView.findViewById(R.id.tvAptDateTime);
            tvAptMode = itemView.findViewById(R.id.tvAptMode);
            tvAptAddress = itemView.findViewById(R.id.tvAptAddress);
            tvAptId = itemView.findViewById(R.id.tvAptId);
            btnReschedule = itemView.findViewById(R.id.btnReschedule);
            btnCancelApt = itemView.findViewById(R.id.btnCancelApt);
        }
    }
}
