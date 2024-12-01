package com.isuru.hettiarachchi;


import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_info);

        Button closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> finish());
    }
}
