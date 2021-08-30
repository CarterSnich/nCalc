package com.darkware.simpcalc.VaultSettings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.darkware.simpcalc.R;

import java.util.List;

public class SettingsRecyclerItemAdapter extends RecyclerView.Adapter<SettingsRecyclerItemAdapter.ViewHolder> {

    private final List<String> mTitle;
    private final List<Drawable> mIcon;
    private final List<Boolean[]> mSwitch;
    private final List<CompoundButton.OnCheckedChangeListener> mToggleSwitchListeners;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public SettingsRecyclerItemAdapter(Context context, List<String> title, List<Drawable> icon, List<Boolean[]> enableSwitch, List<CompoundButton.OnCheckedChangeListener> toggleSwitchListeners) {
        this.mInflater = LayoutInflater.from(context);
        this.mTitle = title;
        this.mIcon = icon;
        this.mSwitch = enableSwitch;
        this.mToggleSwitchListeners = toggleSwitchListeners;

        for (String asd : mTitle) {
            System.out.println(asd);
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.settngs_row_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = mTitle.get(position);
        Drawable icon = mIcon.get(position);
        Boolean[] enableSwtich = mSwitch.get(position);

        holder.rowTitle.setText(title);
        holder.rowIcon.setImageDrawable(icon);
        if (enableSwtich[0]) {
            holder.rowSwitch.setChecked(enableSwtich[1]);
            holder.rowSwitch.setOnCheckedChangeListener(mToggleSwitchListeners.get(position));
        } else {
            holder.rowSwitch.setVisibility(View.GONE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mTitle.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView rowTitle;
        ImageView rowIcon;
        SwitchCompat rowSwitch;

        ViewHolder(View itemView) {
            super(itemView);
            rowTitle = itemView.findViewById(R.id.row_title);
            rowIcon = itemView.findViewById(R.id.row_icon);
            rowSwitch = itemView.findViewById(R.id.row_switch);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mTitle.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}