package com.darkware.simpcalc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.darkware.simpcalc.CodeVault.CodeVaultActivity;

public class EditCodePromptDialog extends DialogFragment {

    public static EditCodePromptDialog newInstance(int itemPosition, String currentCode) {
        EditCodePromptDialog frag = new EditCodePromptDialog();
        Bundle args = new Bundle();
        args.putInt("itemPosition", itemPosition);
        args.putString("itemCurrentCode", currentCode);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // get the position of the RecyclerView item
        final int itemPosition = getArguments().getInt("itemPosition");
        final String itemCurrentCode = getArguments().getString("itemCurrentCode");

        // Get the layout inflater and return view
        final View rootView = getActivity().getLayoutInflater().inflate(R.layout.edit_code_prompt, null);
        final EditText codeEntry = rootView.findViewById(R.id.code_entry);

        // set current code value as the placeholder on EditText
        codeEntry.setText(itemCurrentCode);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
            .setView(rootView)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((CodeVaultActivity) getActivity()).editCode(
                        itemPosition,
                        codeEntry.getText().toString()
                    );
                    ((CodeVaultActivity) getActivity()).saveCodes();
                }
            })
            .setNegativeButton("Cancel", null)
            .create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        codeEntry.setSelection(0, codeEntry.getText().length());

        return dialog;
    }

}
