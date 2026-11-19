package com.example.medilab.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.models.MedicalTest;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
 * RecyclerView Adapter for displaying Medical Test records in MEDILAB.
 * Provides callbacks for View, Edit, and Delete actions on each test card.
 */
public class MedicalTestAdapter extends RecyclerView.Adapter<MedicalTestAdapter.TestViewHolder> {

    public interface OnTestActionListener {
        void onViewTest(MedicalTest test);
        void onEditTest(MedicalTest test);
        void onDeleteTest(MedicalTest test);
    }

    private final Context context;
    private ArrayList<MedicalTest> testList;
    private final OnTestActionListener listener;

    public MedicalTestAdapter(Context context, ArrayList<MedicalTest> testList, OnTestActionListener listener) {
        this.context = context;
        this.testList = testList;
        this.listener = listener;
    }

    public void updateList(ArrayList<MedicalTest> newList) {
        this.testList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medical_test_card, parent, false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        MedicalTest test = testList.get(position);

        holder.tvId.setText("ID #" + test.getId());
        holder.tvName.setText(test.getTestName());
        holder.tvCategory.setText(test.getCategory() != null ? test.getCategory() : "General");
        holder.tvPrice.setText(String.format(java.util.Locale.getDefault(), "₹ %.2f", test.getPrice()));
        holder.tvNormalRange.setText("Ref: " + (test.getNormalRange() != null ? test.getNormalRange() : "N/A"));

        holder.btnView.setOnClickListener(v -> {
            if (listener != null) listener.onViewTest(test);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditTest(test);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteTest(test);
        });
    }

    @Override
    public int getItemCount() {
        return testList != null ? testList.size() : 0;
    }

    static class TestViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvName, tvCategory, tvPrice, tvNormalRange;
        MaterialButton btnView, btnEdit, btnDelete;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_test_id);
            tvName = itemView.findViewById(R.id.tv_test_name);
            tvCategory = itemView.findViewById(R.id.tv_test_category);
            tvPrice = itemView.findViewById(R.id.tv_test_price);
            tvNormalRange = itemView.findViewById(R.id.tv_normal_range);
            btnView = itemView.findViewById(R.id.btn_view_test);
            btnEdit = itemView.findViewById(R.id.btn_edit_test);
            btnDelete = itemView.findViewById(R.id.btn_delete_test);
        }
    }
}
