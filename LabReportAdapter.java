package com.example.medilab.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.models.LabReport;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
 * RecyclerView Adapter for displaying Lab Reports in MEDILAB.
 * Connects Patient + Test + Date + Result.
 * Provides actions: View Details, Edit Report, Delete Report, and Export PDF.
 */
public class LabReportAdapter extends RecyclerView.Adapter<LabReportAdapter.ReportViewHolder> {

    public interface OnReportActionListener {
        void onViewReport(LabReport report);
        void onEditReport(LabReport report);
        void onDeleteReport(LabReport report);
        void onGeneratePdf(LabReport report);
    }

    private final Context context;
    private ArrayList<LabReport> reportList;
    private final OnReportActionListener listener;

    public LabReportAdapter(Context context, ArrayList<LabReport> reportList, OnReportActionListener listener) {
        this.context = context;
        this.reportList = reportList;
        this.listener = listener;
    }

    public void updateList(ArrayList<LabReport> newList) {
        this.reportList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lab_report_card, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        LabReport report = reportList.get(position);

        holder.tvReportId.setText("REPORT #" + report.getReportId());
        holder.tvReportDate.setText(report.getTestDate() != null ? report.getTestDate() : "N/A");
        holder.tvPatientName.setText(report.getPatientName());
        holder.tvPatientBlood.setText(report.getPatientBloodGroup() != null && !report.getPatientBloodGroup().isEmpty() ? report.getPatientBloodGroup() : "—");
        holder.tvTestName.setText(report.getTestName());

        String resultSnippet = report.getResult() != null ? report.getResult() : "No findings recorded.";
        holder.tvResultPreview.setText(resultSnippet);

        holder.btnView.setOnClickListener(v -> {
            if (listener != null) listener.onViewReport(report);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditReport(report);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteReport(report);
        });

        holder.btnPdf.setOnClickListener(v -> {
            if (listener != null) listener.onGeneratePdf(report);
        });
    }

    @Override
    public int getItemCount() {
        return reportList != null ? reportList.size() : 0;
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvReportId, tvReportDate, tvPatientName, tvPatientBlood, tvTestName, tvResultPreview;
        MaterialButton btnPdf, btnView, btnEdit, btnDelete;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportId = itemView.findViewById(R.id.tv_report_id);
            tvReportDate = itemView.findViewById(R.id.tv_report_date);
            tvPatientName = itemView.findViewById(R.id.tv_patient_name);
            tvPatientBlood = itemView.findViewById(R.id.tv_patient_blood);
            tvTestName = itemView.findViewById(R.id.tv_test_name);
            tvResultPreview = itemView.findViewById(R.id.tv_result_preview);
            btnPdf = itemView.findViewById(R.id.btn_pdf_report);
            btnView = itemView.findViewById(R.id.btn_view_report);
            btnEdit = itemView.findViewById(R.id.btn_edit_report);
            btnDelete = itemView.findViewById(R.id.btn_delete_report);
        }
    }
}
