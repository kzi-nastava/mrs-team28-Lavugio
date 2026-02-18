package com.example.lavugio_mobile.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.services.auth.AuthService;

/**
 * Live support chat fragment for regular (non-admin) users.
 *
 * receiverId = 0 means "any admin" – the backend routes the message
 * to the available admin, matching Angular's behaviour.
 *
 * To open this chat, add it to the back stack from your host activity/fragment:
 *
 *   getSupportFragmentManager()
 *       .beginTransaction()
 *       .add(R.id.content_container, new LiveSupportFragment(), "live_support")
 *       .addToBackStack(null)
 *       .commit();
 */
public class LiveSupportFragment extends Fragment {

    // Admin is always receiverId 0 from the user's perspective
    private static final int ADMIN_RECEIVER_ID = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .popBackStack());

        int currentUserId = AuthService.getInstance().getUserId() != null
                ? AuthService.getInstance().getUserId()
                : 0;

        // Embed MessageBoxFragment
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.liveSupportChatContainer,
                            MessageBoxFragment.newInstance(currentUserId))
                    .commit();
        }
    }
}