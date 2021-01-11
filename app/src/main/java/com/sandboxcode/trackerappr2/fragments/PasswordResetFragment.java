package com.sandboxcode.trackerappr2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.sandboxcode.trackerappr2.R;

public class PasswordResetFragment extends DialogFragment {

    private TextInputEditText emailEditText;
    private TextView passwordErrorText;
    PasswordResetDialogListener listener;

    private String email;

    public PasswordResetFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_password_reset, null);
        emailEditText = view.findViewById(R.id.password_edit_email);
        passwordErrorText = view.findViewById(R.id.password_text_error);

        builder.setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        AlertDialog dialog = (AlertDialog) getDialog();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setOnClickListener(view -> {
            email = emailEditText.getText().toString();
            listener.resetPassword(email);
        });
        negativeButton.setOnClickListener(view -> dialog.dismiss());
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

    public void setPasswordErrorText(String message) {
        passwordErrorText.setText(message);
    }

    public interface PasswordResetDialogListener {
        void resetPassword(String newPassword);
    }

}
