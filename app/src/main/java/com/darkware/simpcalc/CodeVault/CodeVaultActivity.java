package com.darkware.simpcalc.CodeVault;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darkware.simpcalc.R;
import com.darkware.simpcalc.VaultSettings.SettingsActivity;
import com.darkware.simpcalc.VaultSettings.VaultSettingsActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeVaultActivity extends AppCompatActivity implements CodeRecyclerItemAdapter.ViewHolder.ClickListener {
    @SuppressWarnings("unused")
    final String TAG = CodeVaultActivity.class.getSimpleName();

    private Toolbar activityToolbar;

    private CodeRecyclerItemAdapter adapter;
    private final ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    private SharedPreferences preferences;
    private final List<CodeItem> codeItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_vault);

        // TODO: set up the toolbar
        activityToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(activityToolbar);

        // TODO: retrieve SharedPreferences of stored codes
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> codeList = preferences.getStringSet("codeList", new HashSet<>());
        for (String code: codeList) {
            codeItems.add(new CodeItem(code, false));
        }

        // TODO: set up the adapter
        adapter = new CodeRecyclerItemAdapter(this,this, codeItems);

        // TODO: set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.code_items);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // TODO: Divider for each item
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vault_menu, menu);

        MenuItem settings = activityToolbar.getMenu().findItem(R.id.settings);
        settings.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return false;
        });

        return true;
    }

    @Override
    public void onItemClick(int position) {
        // TODO: CustomBrowser intent on RecyclerView item OnClick event

        // Toggle selection if Action mode is active
        // else create CustomBrowser intent for browsing the code
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            String url = "https://nhentai.net/g/" + adapter.getItem(position).getCode();

            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
            builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right);

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            customTabsIntent.launchUrl(this, Uri.parse(url));
        }
    }

    @Override
    public void onItemLongClick(int position) {
        // TODO: Handle RecyclerView OnItemLongClick event

        // activate Action mode if actionMode is null
        // else, toggleSelection to the item
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }


    /**
     * Saves codes to SharedPreferences.
     */
    public void editCode (int position, String newCode) {
        adapter.editItem(position, newCode);
    }


    /**
     * Saves codes to SharedPreferences.
     */
    public void saveCodes () {
        Set<String> set = new HashSet<>();

        for (int i = 0; i < adapter.getItemCount(); i++) {
            set.add(adapter.getItem(i).getCode());
        }

        preferences.edit().putStringSet("codeList", set).apply();
    }


    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle("Selected: " + count);
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.vault_selection_menu, menu);
            adapter.setRowMenuVisibility(View.GONE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {

                case R.id.delete_selected:
                    adapter.removeItems(adapter.getSelectedItems());
                    saveCodes();
                    mode.finish();
                    return true;

                case R.id.select_all:
                    adapter.selectAll();
                    actionMode.setTitle(String.valueOf(adapter.getSelectedItemCount()));
                    return true;

                case R.id.select_inverse:
                    adapter.selectInvserse();
                    actionMode.setTitle(String.valueOf(adapter.getSelectedItemCount()));
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            actionMode = null;
            adapter.setRowMenuVisibility(View.VISIBLE);
        }
    }
}

