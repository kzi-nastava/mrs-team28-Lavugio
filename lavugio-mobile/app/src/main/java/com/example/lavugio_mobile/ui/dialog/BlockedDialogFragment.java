package com.example.lavugio_mobile.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lavugio_mobile.R;

public class BlockedDialogFragment extends DialogFragment {

    private static final String ARG_REASON = "reason";

    public static BlockedDialogFragment newInstance(String reason) {
        BlockedDialogFragment fragment = new BlockedDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REASON, reason);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blocked_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvBlockReason = view.findViewById(R.id.tvBlockReason);
        Button btnOk = view.findViewById(R.id.btnOk);

        if (getArguments() != null) {
            tvBlockReason.setText(getArguments().getString(ARG_REASON, "No reason was provided."));
        }

        btnOk.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
