package com.example.medilab.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.models.Payment;
import com.example.medilab.utils.PdfGenerator;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<Payment> payments;
    private final String patientName;

    public PaymentAdapter(Context context, ArrayList<Payment> payments, String patientName) {
        this.context = context;
        this.payments = payments != null ? payments : new ArrayList<>();
        this.patientName = patientName != null ? patientName : "Patient";
    }

    public void updateList(ArrayList<Payment> newList) {
        this.payments = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment pay = payments.get(position);

        holder.tvPayTxnId.setText(pay.getTransactionId());
        holder.tvPayAmount.setText(String.format(Locale.getDefault(), "₹ %.2f", pay.getAmount()));
        holder.tvPayMethod.setText("Method: " + pay.getPaymentMethod());
        holder.tvPayDate.setText("Date: " + pay.getPaymentDate() + " • Apt #" + pay.getAppointmentId());

        String status = pay.getStatus() != null ? pay.getStatus() : "Paid";
        holder.tvPayStatusBadge.setText(status);

        if (status.equalsIgnoreCase("Paid")) {
            holder.tvPayStatusBadge.setTextColor(Color.rgb(0, 150, 136));
        } else {
            holder.tvPayStatusBadge.setTextColor(Color.rgb(245, 124, 0));
        }

        holder.btnDownloadReceipt.setOnClickListener(v -> {
            File pdf = PdfGenerator.generatePaymentInvoicePdf(context, pay, patientName, "Lab Test Consultation", pay.getPaymentDate());
            if (pdf != null) {
                PdfGenerator.openPdfFile(context, pdf);
            } else {
                Toast.makeText(context, "Failed to download receipt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPayTxnId, tvPayAmount, tvPayMethod, tvPayStatusBadge, tvPayDate;
        MaterialButton btnDownloadReceipt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPayTxnId = itemView.findViewById(R.id.tvPayTxnId);
            tvPayAmount = itemView.findViewById(R.id.tvPayAmount);
            tvPayMethod = itemView.findViewById(R.id.tvPayMethod);
            tvPayStatusBadge = itemView.findViewById(R.id.tvPayStatusBadge);
            tvPayDate = itemView.findViewById(R.id.tvPayDate);
            btnDownloadReceipt = itemView.findViewById(R.id.btnDownloadReceipt);
        }
    }
}
