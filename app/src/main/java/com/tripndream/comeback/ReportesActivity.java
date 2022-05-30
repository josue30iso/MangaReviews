package com.tripndream.comeback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

public class ReportesActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ReporteAdapter.OnReporteClickListener{

    private final int KEY_AGREGAR_REPORTE = 666;
    private final int KEY_DETALLE_REPORTE = 777;

    public ArrayList<Reporte> data;
    private RecyclerView rvReportes;
    private ReporteAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ImageView ivNuevoReporte;

    private Spinner spFiltroRaza, spFiltroPerdido, spFiltroTipo;
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
        setContentView(R.layout.activity_reportes);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        ivNuevoReporte = findViewById(R.id.ivNuevoReporte);
        ivNuevoReporte.setOnClickListener(this);

        data = new ArrayList<>();
        adapter = new ReporteAdapter(data, this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvReportes = findViewById(R.id.rvReportes);
        rvReportes.setAdapter(adapter);
        rvReportes.setLayoutManager(linearLayoutManager);

        client = new OkHttpClient();

        spFiltroRaza = findViewById(R.id.spFiltroRazaReportes);
        spFiltroPerdido = findViewById(R.id.spFiltroPerdidoReportes);
        spFiltroTipo = findViewById(R.id.spFiltroTipoReportes);

        spFiltroRaza.setOnItemSelectedListener(this);
        spFiltroPerdido.setOnItemSelectedListener(this);
        spFiltroTipo.setOnItemSelectedListener(this);

        sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    }

    public void consumePerros() {

        RequestBody formBody = new FormBody.Builder()
                .add("idUsuario", sp.getString("id", "-1"))
                .add("raza", String.valueOf(spFiltroRaza.getSelectedItemPosition()))
                .add("colonia", String.valueOf(spFiltroPerdido.getSelectedItemPosition()))
                .add("tieneRecompensa", "-1")
                .add("estatus", String.valueOf(spFiltroTipo.getSelectedItemPosition()))
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
                ReportesActivity.this.runOnUiThread(() -> Toast.makeText(ReportesActivity.this, message, Toast.LENGTH_SHORT).show());
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

        startActivityForResult(intent, KEY_DETALLE_REPORTE);
    }

    @Override
    public void onClick(View view) {
        Log.e("MSG", "Formulario");
        Intent intent;
        switch(view.getId()) {
            case R.id.ivNuevoReporte:
                intent = new Intent(this, Formulario.class);
                startActivityForResult(intent, KEY_AGREGAR_REPORTE);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        consumePerros();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == KEY_AGREGAR_REPORTE){
            spFiltroPerdido.setSelection(0);
            spFiltroRaza.setSelection(0);
            spFiltroTipo.setSelection(0);
            consumePerros();
        }

        if (resultCode == RESULT_OK && requestCode == KEY_DETALLE_REPORTE){
            consumePerros();
        }

    }
}