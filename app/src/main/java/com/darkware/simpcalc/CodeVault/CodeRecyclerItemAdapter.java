package com.darkware.simpcalc.CodeVault;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.darkware.simpcalc.EditCodePromptDialog;
import com.darkware.simpcalc.R;

import java.util.Collections;
import java.util.List;

public class CodeRecyclerItemAdapter extends CodeRecyclerViewSelectorAdapter<CodeRecyclerItemAdapter.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = CodeRecyclerItemAdapter.class.getSimpleName();

    /**
     * Class constants
     */
    private static final int TYPE_INACTIVE = 0;
    private static final int TYPE_ACTIVE = 1;
    private static final int SELECTED = Color.parseColor("#AEAEAE");
    private static final int NOT_SELECTED = Color.parseColor("#303030");

    private final Activity parentActivity;

    private int rowMenuVisibility = View.VISIBLE;

    private final List<CodeItem> codeItems;
    private final ViewHolder.ClickListener clickListener;


    // data is passed into the constructor
    public CodeRecyclerItemAdapter(Activity activity, ViewHolder.ClickListener clickListener, List<CodeItem> codeItems) {
        super();

        this.parentActivity = activity;
        this.clickListener = clickListener;
        this.codeItems = codeItems;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.code_row_item, parent, false);
        return new ViewHolder(view, clickListener);
    }

    // binds the data to the TextView in each row
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CodeItem codeItem = codeItems.get(position);

        holder.rowCode.setText(codeItem.getCode());
        holder.recyclerRow.setBackgroundColor(isSelected(position) ? SELECTED : NOT_SELECTED);
        holder.actionMenu.setVisibility(rowMenuVisibility);
        holder.actionMenu.setOnClickListener(v -> {
            final PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(R.menu.code_row_menu);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {

                    case R.id.copy_code:
                        ClipData data = ClipData.newPlainText("CODE_COPY", codeItem.getCode());
                        ((ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(data);
                        Toast.makeText(
                            v.getContext(),
                            codeItem.getCode() + " copied to clipboard",
                            Toast.LENGTH_SHORT
                        ).show();
                        return true;

                    case R.id.edit_code:
                        DialogFragment editCodePromptDialog = EditCodePromptDialog.newInstance(position, codeItem.getCode());
                        editCodePromptDialog.show(parentActivity.getFragmentManager(), "editCodePromptDialog");
                        return true;

                    case R.id.delete_code:
                        removeItem(position);
                        Toast.makeText(parentActivity.getBaseContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                        ((CodeVaultActivity) parentActivity).saveCodes();
                        return true;

                }
                return false;
            });
            popup.show();
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return codeItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        final CodeItem codeItem = codeItems.get(position);
        return codeItem.isActive() ? TYPE_ACTIVE : TYPE_INACTIVE;
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @SuppressWarnings("unused")
        private static final String TAG = ViewHolder.class.getSimpleName();

        TextView rowCode;
        RelativeLayout recyclerRow;
        ImageButton actionMenu;

        private final ClickListener listener;

        ViewHolder(final View itemView, ClickListener listener) {
            super(itemView);

            rowCode = itemView.findViewById(R.id.row_code);
            recyclerRow = itemView.findViewById(R.id.recycler_row);
            actionMenu = itemView.findViewById(R.id.action_menu);

            this.listener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) listener.onItemClick(getBindingAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (listener != null) listener.onItemLongClick(getBindingAdapterPosition());
            return false;
        }

        public interface ClickListener {
            void onItemClick (int position);
            void onItemLongClick (int position);
        }
    }

    // convenience method for getting data at click position
    CodeItem getItem(int id) {
        return codeItems.get(id);
    }

    public void editItem (int position, String newCode) {
        codeItems.get(position).setCode(newCode);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        codeItems.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, (lhs, rhs) -> rhs - lhs);

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                if (count > 0) {
                    positions.subList(0, count).clear();
                }

            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            codeItems.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    void setRowMenuVisibility (int visibility) {
        rowMenuVisibility = visibility;
        notifyDataSetChanged();
    }

}