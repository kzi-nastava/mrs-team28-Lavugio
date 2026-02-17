package com.example.lavugio_mobile.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.MainActivity;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.auth.AuthCallback;
import com.example.lavugio_mobile.services.auth.AuthService;
import com.example.lavugio_mobile.services.WebSocketService;
import com.example.lavugio_mobile.models.auth.RegistrationRequest;

import java.io.File;
import java.io.IOException;

public class RegisterFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private EditText nameInput;
    private EditText surnameInput;
    private EditText addressInput;
    private EditText phoneInput;
    private ImageButton passwordToggle;
    private ImageButton confirmPasswordToggle;
    private Button registerButton;
    private TextView loginLink;
    private Button profilePictureButton;
    private Button removeProfileButton;
    private ImageView profilePicturePreview;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private AuthService authService;
    private File profilePictureFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize AuthService here where requireContext() is safe
        WebSocketService wsService = new WebSocketService();
        authService = AuthService.getInstance();

        // Initialize views
        emailInput = view.findViewById(R.id.register_email);
        passwordInput = view.findViewById(R.id.register_password);
        confirmPasswordInput = view.findViewById(R.id.register_confirm_password);
        nameInput = view.findViewById(R.id.register_name);
        surnameInput = view.findViewById(R.id.register_surname);
        addressInput = view.findViewById(R.id.register_address);
        phoneInput = view.findViewById(R.id.register_phone);
        passwordToggle = view.findViewById(R.id.register_password_toggle);
        confirmPasswordToggle = view.findViewById(R.id.register_confirm_password_toggle);
        registerButton = view.findViewById(R.id.register_button);
        loginLink = view.findViewById(R.id.login_link);
        profilePictureButton = view.findViewById(R.id.register_profile_button);
        removeProfileButton = view.findViewById(R.id.register_remove_profile);
        profilePicturePreview = view.findViewById(R.id.register_profile_preview);

        if (emailInput == null || passwordInput == null || confirmPasswordInput == null ||
                nameInput == null || surnameInput == null || addressInput == null || phoneInput == null ||
                passwordToggle == null || confirmPasswordToggle == null || registerButton == null || loginLink == null ||
                profilePictureButton == null || removeProfileButton == null || profilePicturePreview == null) {
            Toast.makeText(getContext(), "Error: Some views could not be found", Toast.LENGTH_SHORT).show();
            return;
        }

        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        confirmPasswordToggle.setOnClickListener(v -> toggleConfirmPasswordVisibility());
        registerButton.setOnClickListener(v -> handleRegister());
        loginLink.setOnClickListener(v -> navigateToLogin());
        profilePictureButton.setOnClickListener(v -> selectProfilePicture());
        removeProfileButton.setOnClickListener(v -> removeProfilePicture());
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        passwordInput.setSelection(passwordInput.getText().length());
    }

    private void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible;

        if (isConfirmPasswordVisible) {
            confirmPasswordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            confirmPasswordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        confirmPasswordInput.setSelection(confirmPasswordInput.getText().length());
    }

    private void handleRegister() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String surname = surnameInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        // Validation
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() ||
                surname.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(getContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent double-tap
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");

        RegistrationRequest request = new RegistrationRequest(
                email, password, name, surname, phone, address
        );

        // Use registerWithFile if profile picture is selected
        if (profilePictureFile != null) {
            authService.registerWithFile(request, profilePictureFile, new AuthCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    if (!isAdded()) return;

                    Toast.makeText(getContext(), "Registration successful! Please check your email for verification.",
                            Toast.LENGTH_SHORT).show();

                    // Navigate to login after 3 seconds
                    registerButton.postDelayed(() -> {
                        if (isAdded()) {
                            navigateToLogin();
                        }
                    }, 3000);
                }

                @Override
                public void onError(int code, String message) {
                    if (!isAdded()) return;

                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                    Toast.makeText(getContext(), "Registration failed: " + message,
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            authService.register(request, new AuthCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    if (!isAdded()) return;

                    Toast.makeText(getContext(), "Registration successful! Please check your email for verification.",
                            Toast.LENGTH_SHORT).show();

                    // Navigate to login after 3 seconds
                    registerButton.postDelayed(() -> {
                        if (isAdded()) {
                            navigateToLogin();
                        }
                    }, 3000);
                }

                @Override
                public void onError(int code, String message) {
                    if (!isAdded()) return;

                    registerButton.setEnabled(true);
                    registerButton.setText("Register");
                    Toast.makeText(getContext(), "Registration failed: " + message,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateToLogin() {
        if (getActivity() instanceof MainActivity) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void selectProfilePicture() {
        // Check permissions for Android 13+ (API 33+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES instead of READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
                return;
            }
        } else {
            // Older Android versions
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
                return;
            }
        }

        openImagePicker();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        // Specify accepted MIME types
        String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png", "image/webp", "image/heic", "image/heif"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        try {
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error opening image picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeProfilePicture() {
        profilePictureFile = null;
        profilePicturePreview.setImageBitmap(null);
        profilePicturePreview.setVisibility(View.GONE);
        removeProfileButton.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        // Get file from URI
                        String filePath = getFilePathFromUri(selectedImageUri);
                        if (filePath != null) {
                            profilePictureFile = new File(filePath);

                            // Validate file size (max 5MB)
                            if (profilePictureFile.length() > 5 * 1024 * 1024) {
                                Toast.makeText(getContext(), "Image size must be less than 5MB", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Validate file type by extension
                            String fileName = profilePictureFile.getName().toLowerCase();
                            if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") &&
                                !fileName.endsWith(".png") && !fileName.endsWith(".webp") &&
                                !fileName.endsWith(".heic") && !fileName.endsWith(".heif")) {
                                Toast.makeText(getContext(), "Only JPG, PNG, WebP, HEIC, and HEIF images are allowed", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Show preview
                            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                            profilePicturePreview.setImageBitmap(bitmap);
                            profilePicturePreview.setVisibility(View.VISIBLE);
                            removeProfileButton.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error selecting image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(getContext(), "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFilePathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        android.database.Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }
}