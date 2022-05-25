package com.tripndream.comeback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class ReportesActivity extends AppCompatActivity implements View.OnClickListener, ReporteAdapter.OnReporteClickListener{
    public ArrayList<Reporte> data;
    private RecyclerView rvReportes;
    private ReporteAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ImageView ivNuevoReporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);
        ivNuevoReporte = findViewById(R.id.ivNuevoReporte);
        ivNuevoReporte.setOnClickListener(this);

        data = new ArrayList<>();
        adapter = new ReporteAdapter(data, this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvReportes = findViewById(R.id.rvReportes);
        rvReportes.setAdapter(adapter);
        rvReportes.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onReporteClickListener(int position) {

    }

    @Override
    public void onClick(View view) {
        Log.e("MSG", "Formulario");
        Intent intent;
        switch(view.getId()) {
            case R.id.ivNuevoReporte:
                intent = new Intent(this, Formulario.class);
                startActivity(intent);
                break;
        }
    }
}