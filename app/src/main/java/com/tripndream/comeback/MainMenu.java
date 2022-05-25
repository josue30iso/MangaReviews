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
        btnPerdidos = findViewById(R.id.btnPerdidos);
        btnEnHogar = findViewById(R.id.btnEnHogar);
        btnNuevoHogar = findViewById(R.id.btnNuevoHogar);

        btnTrofeos.setOnClickListener(this);
        btnReportar.setOnClickListener(this);
        btnPerdidos.setOnClickListener(this);
        btnEnHogar.setOnClickListener(this);
        btnNuevoHogar.setOnClickListener(this);
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
                intent = new Intent(this, ReportesActivity.class);
                startActivity(intent);
                break;
            case R.id.btnEnHogar:
                intent = new Intent(this, EnSuHogarActivity.class);
                startActivity(intent);
                break;
            case R.id.btnNuevoHogar:
                intent = new Intent(this, NuevoHogarActivity.class);
                startActivity(intent);
                break;
            case R.id.btnPerdidos:
                intent = new Intent(this, PerdidosActivity.class);
                startActivity(intent);
                break;
        }
    }
}