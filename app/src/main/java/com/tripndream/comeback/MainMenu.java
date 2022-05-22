package com.tripndream.comeback;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnPerdidos, btnReportar, btnEnHogar, btnNuevoHogar, btnTrofeos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnTrofeos = findViewById(R.id.btnTrofeos);

        btnTrofeos.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnTrofeos:
                Intent intent = new Intent(this, Trofeos.class);
                startActivity(intent);
                break;
        }
    }
}