package com.tripndream.comeback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class PerdidosActivity extends AppCompatActivity implements ReporteAdapter.OnReporteClickListener{
    public ArrayList<Reporte> data;
    private RecyclerView rvPerdidos;
    private ReporteAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perdidos);

        data = new ArrayList<>();
        adapter = new ReporteAdapter(data, this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvPerdidos = findViewById(R.id.rvPerdidos);
        rvPerdidos.setAdapter(adapter);
        rvPerdidos.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onReporteClickListener(int position) {

    }
}