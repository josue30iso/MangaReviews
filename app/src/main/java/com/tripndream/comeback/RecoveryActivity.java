package com.tripndream.comeback;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RecoveryActivity extends AppCompatActivity {

    private EditText etCorreo;
    private Button btnRecovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        configMaterials();

    }

    private void configMaterials() {

        etCorreo = findViewById(R.id.etCorreoRecovery);
        btnRecovery = findViewById(R.id.btnActionRecovery);

    }

    private void updateUI() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}