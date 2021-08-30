package com.darkware.simpcalc.VaultSettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darkware.simpcalc.R;

import java.util.ArrayList;

public class VaultSettingsActivity extends AppCompatActivity implements SettingsRecyclerItemAdapter.ItemClickListener {

    Context _this = this;
    Toolbar activityToolbar;
    SettingsRecyclerItemAdapter adapter;
    RecyclerView recyclerView;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_settings);

        activityToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(activityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        activityToolbar.setNavigationOnClickListener(v -> finish());

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();


        // data to populate the RecyclerView with
        ArrayList<String> settingsItems = new ArrayList<>();
        settingsItems.add("Password");
        settingsItems.add("Backup and restore");

        ArrayList<Drawable> rowIcons = new ArrayList<>();
        rowIcons.add(ContextCompat.getDrawable(this, R.drawable.ic_baseline_password));
        rowIcons.add(ContextCompat.getDrawable(this, R.drawable.ic_baseline_backup_restore));

        ArrayList<Boolean[]> addSwitch = new ArrayList<>();
        addSwitch.add(new Boolean[] {true, preferences.contains("vaultPassword")});
        addSwitch.add(new Boolean[] {false, false});

        ArrayList<CompoundButton.OnCheckedChangeListener> toggleSwitchToggleListener = new ArrayList<>();
        toggleSwitchToggleListener.add((buttonView, isChecked) -> passwordSetup(isChecked, (SwitchCompat) buttonView));
        toggleSwitchToggleListener.add(null);

        // set up the RecyclerView
        recyclerView = findViewById(R.id.setting_items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new SettingsRecyclerItemAdapter(
            this,
            settingsItems,
            rowIcons,
            addSwitch,
            toggleSwitchToggleListener
        );
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case 0:
                LinearLayout row = (LinearLayout) recyclerView.getChildAt(position);
                SwitchCompat toggleSwitch = (SwitchCompat) ((RelativeLayout) row.getChildAt(0)).getChildAt(2);
                toggleSwitch.toggle();
                break;

            case 1:
                // Backup and restore activity

                break;

            case 2:
                // About activity

                break;
        }
    }

    private void passwordSetup (boolean enabled, final SwitchCompat toggleSwitch) {
        if (enabled) {
            AlertDialog passwordDialog = new AlertDialog.Builder(_this)
                .setView(R.layout.password_prompt)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    editor.putString(
                        "vaultPassword",
                        ((EditText) ((AlertDialog) dialog).findViewById(R.id.password)).getText().toString()
                    ).commit();
                    Toast.makeText(
                        getApplicationContext(),
                        "Password saved.",
                        Toast.LENGTH_SHORT
                    ).show();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    toggleSwitch.setChecked(false);
                    dialog.dismiss();
                })
                .create();
            passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            passwordDialog.show();
        } else {
            editor.remove("vaultPassword").commit();

        }
    }

}