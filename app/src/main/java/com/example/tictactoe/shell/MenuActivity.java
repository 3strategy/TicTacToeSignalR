package com.example.tictactoe.shell;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.tictactoe.R;
import com.example.tictactoe.activities.Main2Activity;
import com.example.tictactoe.activities.MainActivity;
import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                showFragment(new HomeFragment());
            } else if (itemId == R.id.nav_local_game) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (itemId == R.id.nav_rtdb_prep) {
                startActivity(new Intent(this, Main2Activity.class));
            } else if (itemId == R.id.nav_profile) {
                showFragment(new ProfileFragment());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        if (savedInstanceState == null) {
            showFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment)
                .commit();
    }
}
