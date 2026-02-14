package com.example.lavugio_mobile;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lavugio_mobile.services.WebSocketService;
import com.example.lavugio_mobile.services.auth.AuthService;
import com.example.lavugio_mobile.ui.GuestHomePageFragment;


public class MainActivity extends AppCompatActivity {

    private Navbar navbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String LOCAL_IP = BuildConfig.SERVER_IP;

        System.out.println(LOCAL_IP);
        AuthService.init(this, new WebSocketService());

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the navbar
        navbar = new Navbar(this, findViewById(R.id.main));

        // Load GuestHomePageFragment as the initial fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new GuestHomePageFragment())
                    .commit();
        }
    }
}

