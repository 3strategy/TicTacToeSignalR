package com.example.tictactoe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tictactoe.services.FBRef;
import com.example.tictactoe.R;
import com.example.tictactoe.shell.MenuActivity;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PREFS_NAME";
    private static final String KEY_STAY_CONNECT = "stayConnect";

    private SharedPreferences settings;
    private CheckBox cBstayconnect;
    private EditText eTemail;
    private EditText eTpass;
    private Button btnLogin;
    private boolean loginInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        initViews();

        // Keep the 018a SharedPreferences demo: persist "remember me" checkbox state.
        cBstayconnect.setChecked(settings.getBoolean(KEY_STAY_CONNECT, false));
        cBstayconnect.setOnCheckedChangeListener((buttonView, isChecked) ->
                settings.edit().putBoolean(KEY_STAY_CONNECT, isChecked).apply()
        );

        setLoginInProgress(false);
    }

    private void initViews() {
        cBstayconnect = findViewById(R.id.cBstayconnect);
        eTemail = findViewById(R.id.eTemail);
        eTpass = findViewById(R.id.eTpass);
        btnLogin = findViewById(R.id.btn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean stayConnected = settings.getBoolean(KEY_STAY_CONNECT, false);
        FirebaseUser currentUser = FBRef.refAuth.getCurrentUser();

        if (currentUser != null && stayConnected) {
            FBRef.getUser(currentUser);
            openMenu();
        }
    }

    public void onLoginClick(View view) {
        if (loginInProgress) {
            return;
        }

        String email = eTemail.getText().toString().trim();
        String password = eTpass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            eTemail.setError(getString(R.string.login_error_email_required));
            eTemail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            eTpass.setError(getString(R.string.login_error_password_required));
            eTpass.requestFocus();
            return;
        }

        setLoginInProgress(true);

        FBRef.refAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    setLoginInProgress(false);

                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = FBRef.refAuth.getCurrentUser();
                        if (currentUser == null) {
                            Toast.makeText(this, R.string.login_error_no_user, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        FBRef.getUser(currentUser);
                        settings.edit().putBoolean(KEY_STAY_CONNECT, cBstayconnect.isChecked()).apply();
                        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        openMenu();
                        return;
                    }

                    String errorMessage = task.getException() != null
                            ? task.getException().getLocalizedMessage()
                            : getString(R.string.login_error_failed_generic);

                    Toast.makeText(
                            this,
                            getString(R.string.login_error_failed_with_reason, errorMessage),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void setLoginInProgress(boolean inProgress) {
        loginInProgress = inProgress;
        btnLogin.setEnabled(!inProgress);
        btnLogin.setText(inProgress ? R.string.login_button_loading : R.string.login_button);
    }

    private void openMenu() {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

    public void onGoogleLoginClick(View view) {
        Toast.makeText(this, R.string.login_google_later, Toast.LENGTH_SHORT).show();
    }
}
