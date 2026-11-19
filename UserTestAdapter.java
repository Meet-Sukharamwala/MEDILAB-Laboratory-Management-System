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
import com.example.medilab.activities.BookTestActivity;
import com.example.medilab.activities.UserTestDetailsActivity;
import com.example.medilab.models.MedicalTest;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Locale;

public class UserTestAdapter extends RecyclerView.Adapter<UserTestAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<MedicalTest> tests;

    public UserTestAdapter(Context context, ArrayList<MedicalTest> tests) {
        this.context = context;
        this.tests = tests != null ? tests : new ArrayList<>();
    }

    public void updateList(ArrayList<MedicalTest> newList) {
        this.tests = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_test, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicalTest test = tests.get(position);

        holder.tvTestName.setText(test.getTestName());
        holder.tvCategoryBadge.setText(test.getCategory());
        holder.tvTestPrice.setText(String.format(Locale.getDefault(), "₹ %.2f", test.getPrice()));
        holder.tvTestDescription.setText(test.getDescription() != null && !test.getDescription().isEmpty()
                ? test.getDescription() : "Standard clinical pathology assessment.");
        holder.tvNormalRange.setText("Ref: " + (test.getNormalRange() != null ? test.getNormalRange() : "Standard"));

        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserTestDetailsActivity.class);
            intent.putExtra("test_id", test.getTestId());
            context.startActivity(intent);
        });

        holder.btnBookTest.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookTestActivity.class);
            intent.putExtra("selected_test_id", test.getTestId());
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserTestDetailsActivity.class);
            intent.putExtra("test_id", test.getTestId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryBadge, tvTestPrice, tvTestName, tvTestDescription, tvNormalRange;
        MaterialButton btnViewDetails, btnBookTest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryBadge = itemView.findViewById(R.id.tvCategoryBadge);
            tvTestPrice = itemView.findViewById(R.id.tvTestPrice);
            tvTestName = itemView.findViewById(R.id.tvTestName);
            tvTestDescription = itemView.findViewById(R.id.tvTestDescription);
            tvNormalRange = itemView.findViewById(R.id.tvNormalRange);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnBookTest = itemView.findViewById(R.id.btnBookTest);
        }
    }
}
