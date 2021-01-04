package com.sandboxcode.trackerappr2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.sandboxcode.trackerappr2.R;

public class PasswordResetFragment extends DialogFragment {

    private TextInputEditText newPasswordEditText;
    PasswordResetDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_password_reset, null);

        newPasswordEditText = view.findViewById(R.id.password_edit_new);
        builder.setView(view)
            .setPositiveButton(R.string.password_reset_positive, (dialog, which) ->
                    listener.onDialogPositiveClick(PasswordResetFragment.this))
            .setNegativeButton(R.string.password_reset_negative, (dialog, which) ->
                    listener.onDialogNegativeClick(PasswordResetFragment.this));

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (PasswordResetDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public interface PasswordResetDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
}
