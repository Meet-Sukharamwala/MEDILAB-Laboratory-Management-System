package com.example.medilab.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.models.Patient;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
 * RecyclerView Adapter for displaying Patient records in MEDILAB.
 * Provides callbacks for View, Edit, and Delete actions on each card.
 */
public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {

    public interface OnPatientActionListener {
        void onViewPatient(Patient patient);
        void onEditPatient(Patient patient);
        void onDeletePatient(Patient patient);
    }

    private final Context context;
    private ArrayList<Patient> patientList;
    private final OnPatientActionListener listener;

    public PatientAdapter(Context context, ArrayList<Patient> patientList, OnPatientActionListener listener) {
        this.context = context;
        this.patientList = patientList;
        this.listener = listener;
    }

    public void updateList(ArrayList<Patient> newList) {
        this.patientList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_patient_card, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);

        holder.tvId.setText("ID #" + patient.getId());
        holder.tvName.setText(patient.getName());
        holder.tvBloodGroup.setText(patient.getBloodGroup() != null && !patient.getBloodGroup().isEmpty() ? patient.getBloodGroup() : "N/A");

        String ageGender = patient.getAge() + " yrs • " + (patient.getGender() != null ? patient.getGender() : "N/A");
        holder.tvAgeGender.setText(ageGender);
        holder.tvPhone.setText(patient.getPhone() != null ? patient.getPhone() : "");

        holder.btnView.setOnClickListener(v -> {
            if (listener != null) listener.onViewPatient(patient);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditPatient(patient);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeletePatient(patient);
        });
    }

    @Override
    public int getItemCount() {
        return patientList != null ? patientList.size() : 0;
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvName, tvBloodGroup, tvAgeGender, tvPhone;
        MaterialButton btnView, btnEdit, btnDelete;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_patient_id);
            tvName = itemView.findViewById(R.id.tv_patient_name);
            tvBloodGroup = itemView.findViewById(R.id.tv_blood_group);
            tvAgeGender = itemView.findViewById(R.id.tv_age_gender);
            tvPhone = itemView.findViewById(R.id.tv_patient_phone);
            btnView = itemView.findViewById(R.id.btn_view_patient);
            btnEdit = itemView.findViewById(R.id.btn_edit_patient);
            btnDelete = itemView.findViewById(R.id.btn_delete_patient);
        }
    }
}
