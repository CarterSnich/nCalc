package com.darkware.simpcalc.VaultSettings;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.darkware.simpcalc.R;

public class AboutFragment extends PreferenceFragmentCompat {

    long versionCode;
    String versionName;
    String completeVersionDisplay;

    public AboutFragment() {
        // Required empty public constructor
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.about_preference_fragment, rootKey);

        try {
            versionCode = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0).getLongVersionCode();
            versionName = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0).versionName;
            completeVersionDisplay = String.format("%s (%d)", versionName, versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Preference versionDisplay = findPreference("version_display");
        Preference appUpdates = findPreference("app_updates");
        Preference licenses = findPreference("licenses");

        assert versionDisplay != null;
        versionDisplay.setSummary(completeVersionDisplay);
        versionDisplay.setOnPreferenceClickListener(preference -> {
            ClipData data = ClipData.newPlainText("", completeVersionDisplay);
            ((ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(data);

            Toast.makeText(
                getContext(),
                "Copied to clipboard",
                Toast.LENGTH_SHORT
            ).show();

            return false;
        });

        assert appUpdates != null;
        appUpdates.setOnPreferenceClickListener(preference -> {
            Toast.makeText(
                getContext(),
                "Coming soon",
                Toast.LENGTH_SHORT
            ).show();

            return false;
        });

        assert licenses != null;
        licenses.setOnPreferenceClickListener(preference -> {
            Toast.makeText(
                getContext(),
                "Coming soon",
                Toast.LENGTH_SHORT
            ).show();

            return false;
        });

    }
}