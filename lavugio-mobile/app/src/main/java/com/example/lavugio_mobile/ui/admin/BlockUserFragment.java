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

import com.example.lavugio_mobile.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockUserFragment extends Fragment {

    private AutoCompleteTextView etUserEmail;
    private EditText etBlockReason;
    private Button btnBlockUser;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private ArrayAdapter<String> emailAdapter;

    private final List<String> allEmails = Arrays.asList(
            "admin@lavugio.com",
            "driver1@gmail.com",
            "driver2@gmail.com",
            "user1@yahoo.com",
            "user2@hotmail.com",
            "marko.petrovic@gmail.com",
            "ivan.jovanovic@yahoo.com",
            "stefan.nikolic@outlook.com",
            "test.user@gmail.com"
    );

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

        List<String> filtered = new ArrayList<>();

        for (String email : allEmails) {
            if (email.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(email);
            }
        }

        requireActivity().runOnUiThread(() -> {
            emailAdapter.clear();
            emailAdapter.addAll(filtered);
            emailAdapter.notifyDataSetChanged();
        });
    }

    private void blockUser() {
        String email = etUserEmail.getText().toString().trim();
        String reason = etBlockReason.getText().toString().trim();

        if (email.isEmpty()) {
            etUserEmail.setError("Email je obavezan");
            return;
        }

        if (reason.isEmpty()) {
            etBlockReason.setError("Razlog je obavezan");
            return;
        }

        Toast.makeText(requireContext(),
                "Blokiran korisnik: " + email,
                Toast.LENGTH_SHORT).show();

        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
    }
}
