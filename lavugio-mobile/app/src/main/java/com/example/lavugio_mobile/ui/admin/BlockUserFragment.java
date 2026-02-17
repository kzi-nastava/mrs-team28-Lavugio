package com.example.lavugio_mobile.ui.admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.user.EmailSuggestion;
import com.example.lavugio_mobile.ui.dialog.ConfirmDialogFragment;
import com.example.lavugio_mobile.ui.dialog.ErrorDialogFragment;
import com.example.lavugio_mobile.ui.dialog.SuccessDialogFragment;
import com.example.lavugio_mobile.viewmodel.admin.BlockUserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockUserFragment extends Fragment {

    private AutoCompleteTextView etUserEmail;
    private EditText etBlockReason;
    private Button btnBlockUser;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private ArrayAdapter<String> emailAdapter;

    private BlockUserViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block_user, container, false);

        etUserEmail = view.findViewById(R.id.etUserEmail);
        etBlockReason = view.findViewById(R.id.etBlockReason);
        btnBlockUser = view.findViewById(R.id.btnBlockUser);

        emailAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        );

        viewModel = new ViewModelProvider(this).get(BlockUserViewModel.class);

        viewModel.getSuggestions().observe(getViewLifecycleOwner(), suggestions -> {
            if (suggestions != null) {
                List<String> emails = new ArrayList<>();
                for (EmailSuggestion s : suggestions) {
                    emails.add(s.getEmail());
                }
                emailAdapter.clear();
                emailAdapter.addAll(emails);
                emailAdapter.notifyDataSetChanged();
            }
        });

        etUserEmail.setAdapter(emailAdapter);
        etUserEmail.setThreshold(1);

        etUserEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                debounceSearch(s.toString());
            }
        });

        btnBlockUser.setOnClickListener(v -> blockUser());

        return view;
    }

    private void debounceSearch(String query) {
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }

        searchRunnable = () -> fetchEmailSuggestions(query);
        handler.postDelayed(searchRunnable, 300);
    }

    private void fetchEmailSuggestions(String query) {
        if (query.length() < 2) return;
        viewModel.fetchEmailSuggestions(query);
    }

    private void blockUser() {
        String email = etUserEmail.getText().toString().trim();
        String reason = etBlockReason.getText().toString().trim();

        if (!isValidEmailRegex(email)) {
            ErrorDialogFragment.newInstance("Error", "You have to enter email.")
                    .show(getActivity().getSupportFragmentManager(), "error_dialog");
            return;
        }

        ConfirmDialogFragment.newInstance(
                "Confirm Block",
                "Are you sure you want to block this user?",
                new ConfirmDialogFragment.ConfirmDialogListener() {
                    @Override
                    public void onConfirm() {
                        viewModel.blockUser(email, reason).observe(getViewLifecycleOwner(), success -> {
                            if (success != null && success) {
                                SuccessDialogFragment.newInstance("Success", "User blocked successfully.")
                                        .show(getActivity().getSupportFragmentManager(), "success_dialog");
                                etUserEmail.setText("");
                                etBlockReason.setText("");
                            } else if (success != null) {
                                ErrorDialogFragment.newInstance("Error", "Failed to block user.")
                                        .show(getActivity().getSupportFragmentManager(), "error_dialog");
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                    }
                }
        ).show(getActivity().getSupportFragmentManager(), "confirm_dialog");


    }

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    public static boolean isValidEmailRegex(String email) {
        if (email == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
    }
}
