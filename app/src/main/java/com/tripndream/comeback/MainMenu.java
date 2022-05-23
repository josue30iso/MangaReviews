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
        btnReportar = findViewById(R.id.btnReportar);

        btnTrofeos.setOnClickListener(this);
        btnReportar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.btnTrofeos:
                intent = new Intent(this, Trofeos.class);
                startActivity(intent);
                break;
            case R.id.btnReportar:
                intent = new Intent(this, Formulario.class);
                startActivity(intent);
                break;
        }
    }
}