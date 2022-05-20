package com.tripndream.comeback;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

public class MainMenu extends AppCompatActivity {
    ImageButton btnPerdidos, btnReportar, btnEnHogar, btnNuevoHogar, btnTrofeos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }
}