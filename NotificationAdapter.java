package com.example.medilab.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.R;
import com.example.medilab.models.NotificationItem;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<NotificationItem> notifications;

    public NotificationAdapter(Context context, ArrayList<NotificationItem> notifications) {
        this.context = context;
        this.notifications = notifications != null ? notifications : new ArrayList<>();
    }

    public void updateList(ArrayList<NotificationItem> newList) {
        this.notifications = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = notifications.get(position);

        holder.tvNotifTitle.setText(item.getTitle());
        holder.tvNotifMessage.setText(item.getMessage());
        holder.tvNotifDate.setText(item.getCreatedAt());

        if (item.getIsRead() == 0) {
            holder.viewUnreadDot.setVisibility(View.VISIBLE);
        } else {
            holder.viewUnreadDot.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotifTitle, tvNotifMessage, tvNotifDate;
        View viewUnreadDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotifTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvNotifMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvNotifDate = itemView.findViewById(R.id.tvNotifDate);
            viewUnreadDot = itemView.findViewById(R.id.viewUnreadDot);
        }
    }
}
