package com.isuru.hettiarachchi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;



public class MainActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) throws Resources.NotFoundException {
        super.onCreate(savedInstanceState);

        // Load saved theme preference
        SharedPreferences sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkMode = sharedPref.getBoolean("isDarkMode", false);

        // Set the appropriate theme before setting the content view
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        // Get references to the buttons
        Button beginnerBtn = findViewById(R.id.beginnerBtn);
        Button advancedBtn = findViewById(R.id.advancedBtn);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView infoButton = findViewById(R.id.infoBtn);
        Button translationBtn = findViewById(R.id.translationBtn);



        infoButton.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intent);
        });
        translationBtn.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, TranslateActivity.class);
            startActivity(intent);
        });

        // Initialize the toggle switch
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Switch themeToggle = findViewById(R.id.themeToggle);
        themeToggle.setChecked(isDarkMode);

        themeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                // Switch to dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                beginnerBtn.setTextColor(getColor(R.color.white));
                advancedBtn.setTextColor(getColor(R.color.white));
                sharedPref.edit().putBoolean("isDarkMode", true).apply();
            } else {
                // Switch to light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPref.edit().putBoolean("isDarkMode", false).apply();
            }
        });

        // Set click listeners for both buttons
        beginnerBtn.setOnClickListener(v -> {
            String jsonPath = "words_3000_encrypted.json";
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("json_path", jsonPath);
            startActivity(intent);
        });

        advancedBtn.setOnClickListener(v -> {
            String jsonPath = "words_2000_encrypted.json";
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("json_path", jsonPath);
            startActivity(intent);
        });
    }
}
