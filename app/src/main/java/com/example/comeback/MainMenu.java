package com.example.comeback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {
    ImageButton btnPerdidos, btnReportar, btnEnHogar, btnNuevoHogar, btnTrofeos, btnAvistamientos;
    Button btnCerrarSesion;

    SharedPreferences sp;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        btnTrofeos = findViewById(R.id.btnTrofeos);
        btnReportar = findViewById(R.id.btnReportar);
        btnPerdidos = findViewById(R.id.btnPerdidos);
        btnEnHogar = findViewById(R.id.btnEnHogar);
        btnNuevoHogar = findViewById(R.id.btnNuevoHogar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnAvistamientos = findViewById(R.id.btnAvistamientos);

        btnTrofeos.setOnClickListener(this);
        btnReportar.setOnClickListener(this);
        btnPerdidos.setOnClickListener(this);
        btnEnHogar.setOnClickListener(this);
        btnNuevoHogar.setOnClickListener(this);
        btnCerrarSesion.setOnClickListener(this);
        btnAvistamientos.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
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
            case R.id.btnAvistamientos:
                intent = new Intent(this, AvistamientosActivity.class);
                startActivity(intent);
                break;
            case R.id.btnCerrarSesion:
                AlertDialog.Builder alert = new AlertDialog.Builder(MainMenu.this, R.style.AlertDialogStyle);
                alert.setTitle(Html.fromHtml("<font color='#3246ab'>Está por cerrar sesión</font>"))
                        .setMessage(Html.fromHtml("<font color='#3246ab'>¿Realmente deseas cerrar sesión?</font>"))
                        .setPositiveButton("Aceptar", (dialog, id) -> {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("session", "");
                            editor.putString("id", "");
                            editor.putString("nombre", "");
                            editor.putString("perrosRescatados", "");
                            editor.putString("nEncontrados", "");
                            editor.commit();

                            intent = new Intent(this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss()).show();
        }
    }
}