package com.tripndream.comeback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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

    private static final String KEY_ID = "KEY_ID";
    private static final String KEY_IMAGEN = "KEY_IMAGEN";
    private static final String KEY_NOMBRE = "KEY_NOMBRE";
    private static final String KEY_RECOMPENSA = "KEY_RECOMPENSA";
    private static final String KEY_ESTATUS = "KEY_ESTATUS";
    private static final String KEY_ID_RAZA = "KEY_ID_RAZA";
    private static final String KEY_RAZA = "KEY_RAZA";
    private static final String KEY_ID_COLONIA = "KEY_ID_COLONIA";
    private static final String KEY_COLONIA = "KEY_COLONIA";
    private static final String KEY_FECHA = "KEY_FECHA";
    private static final String KEY_NUMERO = "KEY_NUMERO";
    private static final String KEY_DESCRIPCION = "KEY_DESCRIPCION";
    private static final String KEY_USUARIO = "KEY_USUARIO";

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
                .add("estatus", "3")
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
                                reporteObj.getDouble("recompensa"),
                                reporteObj.getString("idUsuario"),
                                reporteObj.getInt("estatus"),
                                reporteObj.getString("foto"),
                                reporteObj.getString("titulo"),
                                reporteObj.getInt("raza"),
                                String.valueOf(spFiltroRaza.getItemAtPosition(reporteObj.getInt("raza"))),
                                reporteObj.getInt("idColonia"),
                                reporteObj.getString("nombreColonia"),
                                reporteObj.getString("descripcion"),
                                reporteObj.getString("ultimaVista"),
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
        Reporte reporte = data.get(position);
        Intent intent = new Intent(getApplicationContext(), DetalleActivity.class);

        intent.putExtra(KEY_ID, reporte.getId());
        intent.putExtra(KEY_IMAGEN, reporte.getImagen());
        intent.putExtra(KEY_NOMBRE, reporte.getNombre());
        intent.putExtra(KEY_RECOMPENSA, reporte.getRecompensa());
        Log.i("Estatus", String.valueOf(reporte.getEstatus()));
        intent.putExtra(KEY_ESTATUS, reporte.getEstatus());
        intent.putExtra(KEY_ID_RAZA, reporte.getSpRaza());
        intent.putExtra(KEY_RAZA, reporte.getRaza());
        intent.putExtra(KEY_ID_COLONIA, reporte.getIdColonia());
        intent.putExtra(KEY_COLONIA, reporte.getColonia());
        intent.putExtra(KEY_FECHA, reporte.getFecha());
        intent.putExtra(KEY_NUMERO, reporte.getCelular());
        intent.putExtra(KEY_DESCRIPCION, reporte.getDescripcion());
        intent.putExtra(KEY_USUARIO, reporte.getUsuario());

        startActivity(intent);
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