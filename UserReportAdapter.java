package com.example.medilab.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.activities.UserReportDetailsActivity;
import com.example.medilab.models.LabReport;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class UserReportAdapter extends RecyclerView.Adapter<UserReportAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<LabReport> reports;

    public UserReportAdapter(Context context, ArrayList<LabReport> reports) {
        this.context = context;
        this.reports = reports != null ? reports : new ArrayList<>();
    }

    public void updateList(ArrayList<LabReport> newList) {
        this.reports = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LabReport report = reports.get(position);

        holder.tvReportTestName.setText(report.getTestName() != null ? report.getTestName() : "Laboratory Test");
        holder.tvReportDate.setText(report.getTestDate() != null ? report.getTestDate() : "N/A");
        holder.tvReportCategory.setText(report.getTestCategory() != null ? report.getTestCategory() : "General");
        holder.tvReportId.setText("Report #" + report.getReportId());

        View.OnClickListener clickListener = v -> {
            Intent intent = new Intent(context, UserReportDetailsActivity.class);
            intent.putExtra("report_id", report.getReportId());
            context.startActivity(intent);
        };

        holder.btnViewReport.setOnClickListener(clickListener);
        holder.itemView.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReportTestName, tvReportStatus, tvReportDate, tvReportCategory, tvReportId;
        MaterialButton btnViewReport;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportTestName = itemView.findViewById(R.id.tvReportTestName);
            tvReportStatus = itemView.findViewById(R.id.tvReportStatus);
            tvReportDate = itemView.findViewById(R.id.tvReportDate);
            tvReportCategory = itemView.findViewById(R.id.tvReportCategory);
            tvReportId = itemView.findViewById(R.id.tvReportId);
            btnViewReport = itemView.findViewById(R.id.btnViewReport);
        }
    }
}
