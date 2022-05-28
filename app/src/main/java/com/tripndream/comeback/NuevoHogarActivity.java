package com.tripndream.comeback;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tripndream.comeback.utils.WebService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NuevoHogarActivity extends AppCompatActivity implements ReporteAdapter.OnReporteClickListener, AdapterView.OnItemSelectedListener{
    public ArrayList<Reporte> data;
    private RecyclerView rvNuevoHogar;
    private ReporteAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private Spinner spFiltroRaza;
    public OkHttpClient client;
    private SharedPreferences sp;
    private Reporte reporteMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_hogar);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        data = new ArrayList<>();
        adapter = new ReporteAdapter(data, this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvNuevoHogar = findViewById(R.id.rvNuevoHogar);
        rvNuevoHogar.setAdapter(adapter);
        rvNuevoHogar.setLayoutManager(linearLayoutManager);

        client = new OkHttpClient();

        spFiltroRaza = findViewById(R.id.spFiltroRazaNuevoH);
        spFiltroRaza.setOnItemSelectedListener(this);

        sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    }

    private void consumePerros() {

        RequestBody formBody = new FormBody.Builder()
                .add("idUsuario", "-1")
                .add("raza", String.valueOf(spFiltroRaza.getSelectedItemPosition()))
                .add("colonia", "-1")
                .add("tieneRecompensa", "-1")
                .add("estatus", "4")
                .build();

        Request request = new Request.Builder()
                .url(WebService.URL_PUB_LIST)
                .post(formBody)
                .build();

        try {
            Response responseHTTP = client.newCall(request).execute();
            JSONObject response = new JSONObject(responseHTTP.body().string());;

            boolean success = response.getBoolean("success");
            String message = response.getString("message");

            adapter.clear();

            if (success) {
                JSONArray data = response.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {

                    JSONObject reporteObj = data.getJSONObject(i);

                    reporteMain = null;
                    try {
                        reporteMain = new Reporte(
                                reporteObj.getInt("id"),
                                reporteObj.getString("foto"),
                                reporteObj.getString("titulo"),
                                String.valueOf(spFiltroRaza.getItemAtPosition(reporteObj.getInt("raza"))),
                                reporteObj.getString("nombreColonia"),
                                reporteObj.getString("descripcion"),
                                reporteObj.getString("fechaRegistro"),
                                reporteObj.getString("numeroContacto")
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    adapter.add(reporteMain);
                }

                adapter.notifyDataSetChanged();

            } else {
                NuevoHogarActivity.this.runOnUiThread(() -> Toast.makeText(NuevoHogarActivity.this, message, Toast.LENGTH_LONG).show());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReporteClickListener(int position) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        consumePerros();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }
}