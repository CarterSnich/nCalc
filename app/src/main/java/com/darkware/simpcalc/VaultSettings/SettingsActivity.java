package com.darkware.simpcalc.VaultSettings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.darkware.simpcalc.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar activityToolbar = findViewById(R.id.activity_toolbar);
        setSupportActionBar(activityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activityToolbar.setNavigationOnClickListener(v -> finish());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();

            SwitchPreference password = findPreference("password");

            assert password != null;
            password.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((boolean) newValue) {
                    AlertDialog passwordDialog = new AlertDialog.Builder(getContext())
                        .setView(R.layout.password_prompt)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            editor.putString(
                                "vaultPassword",
                                ((AlertDialog) dialog).<EditText>findViewById(R.id.password).getText().toString()
                            ).apply();
                            Toast.makeText(
                                getContext(),
                                "Password saved.",
                                Toast.LENGTH_SHORT
                            ).show();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                            password.setChecked(false);
                            dialog.dismiss();
                        })
                        .create();
                    passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    passwordDialog.show();
                } else {
                    editor.remove("vaultPassword").apply();
                }

                return true;
            });

        }
    }
}