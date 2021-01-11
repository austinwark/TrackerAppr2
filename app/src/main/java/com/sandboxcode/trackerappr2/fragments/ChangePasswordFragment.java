package com.sandboxcode.trackerappr2.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.viewmodels.AuthViewModel;

public class ChangePasswordFragment extends DialogFragment {

    AlertDialog.Builder builder;
    LayoutInflater inflater;
    View view;

//    private TextInputEditText currentPasswordEditText;
    private CoordinatorLayout coordinatorLayout;
    private TextInputEditText newPasswordEditText;
    private TextInputEditText confirmPasswordEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText currentPasswordEditText;
    Button positiveButton;
    Button negativeButton;

    private AuthViewModel viewModel;

    String newPassword;
    String confirmPassword;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        builder = new AlertDialog.Builder(getActivity());

        inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_change_password, null);

        coordinatorLayout = view.findViewById(R.id.change_password_layout_coordinator);
        newPasswordEditText = view.findViewById(R.id.change_password_edit_new);
        confirmPasswordEditText = view.findViewById(R.id.change_password_edit_confirm);
        emailEditText = view.findViewById(R.id.reauthenticate_edit_email);
        currentPasswordEditText = view.findViewById(R.id.reauthenticate_edit_password);

        builder.setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel  = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        viewModel.getPasswordChangeSuccess().observe(requireActivity(), success -> {
            getDialog().dismiss();
        });

        viewModel.getNeedsToReauthenticate().observe(requireActivity(), needsToReauthenticate -> {
            view.findViewById(R.id.change_password_layout_layout).setVisibility(View.GONE);
            view.findViewById(R.id.reauthenticate_layout_layout).setVisibility(View.VISIBLE);
            positiveButton.setOnClickListener(view -> reauthenticate());
        });

        viewModel.getFirebaseError().observe(this, firebaseError -> {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, firebaseError,
                    BaseTransientBottomBar.LENGTH_LONG);
            snackbar.show();
        });

        AlertDialog dialog = (AlertDialog) getDialog();
        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setOnClickListener(view -> changePassword());
        negativeButton.setOnClickListener(view -> dialog.dismiss());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChangePasswordFragment.
     */
    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


    }

    public void changePassword() {
        if (newPasswordEditText.getText() != null && confirmPasswordEditText.getText() != null) {

//            String currentPassword = currentPasswordEditText.getText().toString();
            newPassword = newPasswordEditText.getText().toString();
            confirmPassword = confirmPasswordEditText.getText().toString();

            viewModel.changePassword(newPassword, confirmPassword);
        } else
            Log.d("ChangePasswordFragment", "changePassword else");
    }

    // TODO -- save newPassword and confirmPassword on device rotation
    public void reauthenticate() {
        if (emailEditText.getText() != null && currentPasswordEditText.getText() != null) {

            String email = emailEditText.getText().toString();
            String currentPassword = currentPasswordEditText.getText().toString();

            viewModel.reauthenticate(email, currentPassword, newPassword, confirmPassword);
        } else
            Log.d("ChangePasswordFragment", "reauthenticate else");
    }

}