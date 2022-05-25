package com.tripndream.comeback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class EnSuHogarActivity extends AppCompatActivity implements ReporteAdapter.OnReporteClickListener{
    public ArrayList<Reporte> data;
    private RecyclerView rvEnSuHogar;
    private ReporteAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_su_hogar);

        data = new ArrayList<>();
        adapter = new ReporteAdapter(data, this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvEnSuHogar = findViewById(R.id.rvEnSuHogar);
        rvEnSuHogar.setAdapter(adapter);
        rvEnSuHogar.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onReporteClickListener(int position) {

    }
}