package com.darkware.simpcalc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.darkware.simpcalc.CodeVault.CodeVaultActivity;

public class PasswordPromptDialog extends DialogFragment {

    Dialog dialog;
    SharedPreferences preferences;
    EditText passwordEditText;
    String passwordEntered;
    String password;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        dialog = builder.setView(inflater.inflate(R.layout.password_prompt, null))
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

                    passwordEditText = getDialog().findViewById(R.id.password);
                    passwordEntered = passwordEditText.getText().toString();
                    password = preferences.getString("vaultPassword", "1234");

                    if (passwordEntered.equals(password)) {
                        Intent intent = new Intent(getActivity(), CodeVaultActivity.class);
                        startActivity(intent);
                    } else {
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(400);
                    }
                }
            })
            .setNegativeButton("Cancel", null)
            .create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }
}
