package com.example.tictactoe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tictactoe.R;
import com.example.tictactoe.shell.MenuActivity;

public class LoginActivity extends AppCompatActivity {


    private static final long LOGIN_SHELL_DELAY_MS = 4000;
    private static final String PREFS_NAME = "PREFS_NAME";
    private static final String KEY_STAY_CONNECT = "stayConnect";

    private SharedPreferences settings;
    private CheckBox cBstayconnect;
    private final Handler handler = new Handler(Looper.getMainLooper());

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
        cBstayconnect = findViewById(R.id.cBstayconnect);

        // SharedPreferences demo: restore and save the "remember me" checkbox.
        cBstayconnect.setChecked(settings.getBoolean(KEY_STAY_CONNECT, false));
        cBstayconnect.setOnCheckedChangeListener((buttonView, isChecked) ->
                settings.edit().putBoolean(KEY_STAY_CONNECT, isChecked).apply()
        );
        // Temporary bridge: skip login until Firebase auth lesson is implemented.
        handler.postDelayed(() -> {
            settings.edit().putBoolean(KEY_STAY_CONNECT, cBstayconnect.isChecked()).apply();
            startActivity(new Intent(LoginActivity.this, MenuActivity.class));
            finish();
        }, LOGIN_SHELL_DELAY_MS);
    }

    public void onLoginClick(View view) {
        Toast.makeText(this, "Email/password auth is added in a later lesson", Toast.LENGTH_SHORT).show();
    }

    public void onGoogleLoginClick(View view) {
        Toast.makeText(this, "Google sign-in is an advanced later phase", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}