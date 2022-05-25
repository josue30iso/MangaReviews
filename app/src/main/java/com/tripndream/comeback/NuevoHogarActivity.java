package com.tripndream.comeback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class NuevoHogarActivity extends AppCompatActivity implements ReporteAdapter.OnReporteClickListener{
    public ArrayList<Reporte> data;
    private RecyclerView rvNuevoHogar;
    private ReporteAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_hogar);

        data = new ArrayList<>();
        adapter = new ReporteAdapter(data, this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvNuevoHogar = findViewById(R.id.rvNuevoHogar);
        rvNuevoHogar.setAdapter(adapter);
        rvNuevoHogar.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onReporteClickListener(int position) {

    }
}