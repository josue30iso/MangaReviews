package com.tripndream.hopeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tripndream.hopeapp.utils.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InicioActivity extends AppCompatActivity implements PubAdapter.OnPubClickListener {

    private static final String KEY_TEXTO_NOMBRE = "key_texto_nombre_desa";
    private static final String KEY_ID_PUBLICACION  = "key_id_pub";
    private static final String KEY_TEXTO_DESCRIPCION = "key_texto_descripcion";
    private static final String KEY_URL_FOTO = "key_url_foto";
    private static final String KEY_ID_ZONA = "key_id_zona";
    private static final String KEY_TEXTO_REVIEW = "key_texto_ultima";
    private static final String KEY_TEXTO_NOMBRE_AUTOR = "key_texto_nombre_autor";
    private static final String KEY_ID_USUARIO = "key_id_usuario";
    private static final String KEY_ID_ULTIMA = "key_id_ultima";
    private static final String KEY_GUARDADOS = "key_guardados";
    private static final String KEY_ID_VALIDADA = "key_id_validada";
    private static final String KEY_NOMBRE_ZONA = "key_nombre_zona";

    private Toolbar toolbar;
    private ImageView btnInicio, btnGuardados, btnMiPerfil, btnLogout;

    Button btnMostrarPendientes;

    private Spinner spinnerFiltro;
    private ArrayAdapter<String> adapterSp;
    private List<Integer> sIds;
    private int posSpinner = 0;

    private RecyclerView rvPublicaciones;
    private ArrayList<Publicacion> datos;
    private PubAdapter adapter;
    private LinearLayoutManager llm;

    private SharedPreferences sp;

    private Boolean noClone = false, muestraPendientes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        configToolbar();
        configMaterials();

    }

    private void configToolbar() {
        btnInicio = findViewById(R.id.btnInicio);
        btnGuardados = findViewById(R.id.btnGuardados);
        btnMiPerfil = findViewById(R.id.btnMiPerfil);
        btnLogout = findViewById(R.id.btnLogout);

        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        btnGuardados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioActivity.this, MisPubActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_GUARDADOS, true);
                startActivity(intent);
            }
        });

        btnMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InicioActivity.this, MisPubActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarLogout();
            }
        });
    }

    private void updateUILogout() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void configMaterials() {
        sp = getSharedPreferences("emailUserSession", Context.MODE_PRIVATE);
        Boolean esAdmin = sp.getBoolean("esAdmin", false);

        spinnerFiltro = findViewById(R.id.spinnerFiltro);

        rvPublicaciones = findViewById(R.id.rvPublicaciones);

        datos = new ArrayList<Publicacion>();

        adapter = new PubAdapter(datos, this, Integer.parseInt(sp.getString("logedID", "")));
        llm = new LinearLayoutManager(getApplicationContext());

        rvPublicaciones.setAdapter(adapter);
        rvPublicaciones.setLayoutManager(llm);

        btnMostrarPendientes = findViewById(R.id.btnMostrarPendientes);

        if (!esAdmin) {
            btnMostrarPendientes.setVisibility(View.GONE);
        } else {
            btnMostrarPendientes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    muestraPendientes = !muestraPendientes;
                    btnMostrarPendientes.setText(muestraPendientes ? "Mostrar Validadas" : "Mostrar Pendientes");
                    listaPorZona(posSpinner, muestraPendientes);
                }
            });
        }

        llenarSpinner();
        configClickListeners();
    }

    private void llenarSpinner() {

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext(), new HurlStack());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_PUB_LISTALLZONES, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");
                    String mensaje = response.getString("message");

                    if (success) {
                        JSONArray data = response.getJSONArray("data");

                        ArrayList<String> arraySpinner = new ArrayList<>();
                        sIds = new ArrayList<>();
                        arraySpinner.add("Todas...");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject zonaObj = data.getJSONObject(i);
                            try {
                                sIds.add(zonaObj.getInt("id"));
                                arraySpinner.add(zonaObj.getString("nombre"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        adapterSp = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arraySpinner);
                        adapterSp.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                        spinnerFiltro.setAdapter(adapterSp);
                    } else {
                        Toast.makeText(InicioActivity.this, "Sin resultados.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(InicioActivity.this, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;

    }

    private void listaPorZona(int i, Boolean muestraPendientes) {

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext(), new HurlStack());

        Map map = new HashMap();
        map.put("idZona", i);
        map.put("idUsuario", sp.getString("logedID", ""));
        map.put("mostrarPendientes", muestraPendientes);

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_PUB_LISTBYCAT, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");

                    adapter.clear();
                    datos.clear();

                    if (success) {

                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {

                            JSONObject pubObj = data.getJSONObject(i);

                            Publicacion publicacion = null;
                            try {
                                publicacion = new Publicacion(
                                        pubObj.getInt("id"),
                                        pubObj.getInt("idUsuario"),
                                        pubObj.getString("nombreUsuario"),
                                        pubObj.getInt("idZona"),
                                        pubObj.getString("nombreZona"),
                                        pubObj.getString("nombreDesaparecido"),
                                        pubObj.getString("descripcion"),
                                        pubObj.getString("ultimoVistazo"),
                                        pubObj.getString("fechaRegistro"),
                                        pubObj.getString("foto"),
                                        pubObj.getInt("validada"),
                                        1 == pubObj.getInt("guardado")
                                );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            adapter.add(publicacion);
                            datos.add(publicacion);

                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        Toast.makeText(InicioActivity.this, "Sin resultados.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(InicioActivity.this, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;

    }

    private void configClickListeners() {

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                listaPorZona(i, muestraPendientes);
                posSpinner = i;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

    }

    private void confirmarLogout() {

        AlertDialog.Builder alert = new AlertDialog.Builder(InicioActivity.this, R.style.AlertDialogStyle);
        alert.setTitle(Html.fromHtml("<font color='#3a3c63'>Está a punto de cerrar sesión</font>"))
                .setMessage(Html.fromHtml("<font color='#3a3c63'>¿Realmente deseas cerrar sesión?</font>"))
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("session", "");
                        editor.putString("logedID", "");
                        editor.commit();
                        updateUILogout();
                    }

                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();

    }

    @Override
    public void onPubClickListener(int position) {

        Intent intent = new Intent(getApplicationContext(), PubActivity.class);

        Publicacion publicacion = datos.get(position);

        intent.putExtra(KEY_ID_PUBLICACION, publicacion.getId());
        intent.putExtra(KEY_TEXTO_NOMBRE, publicacion.getNombreDesaparecido());
        intent.putExtra(KEY_ID_ZONA, publicacion.getIdZona());
        intent.putExtra(KEY_NOMBRE_ZONA, publicacion.getNombreZona());
        intent.putExtra(KEY_TEXTO_NOMBRE_AUTOR, publicacion.getNombreUsuario());
        intent.putExtra(KEY_URL_FOTO, publicacion.getFoto());
        intent.putExtra(KEY_TEXTO_REVIEW, publicacion.getUltimoVistazo());
        intent.putExtra(KEY_TEXTO_DESCRIPCION, publicacion.getDescripcion());
        intent.putExtra(KEY_ID_USUARIO, publicacion.getIdUsuario());
        intent.putExtra(KEY_ID_ULTIMA, publicacion.getId());
        intent.putExtra(KEY_ID_VALIDADA, publicacion.getValidada());

        noClone = true;
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        datos.clear();
        adapter.clear();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (noClone) listaPorZona(posSpinner, muestraPendientes);
        super.onResume();
    }

    @Override
    protected void onPause() {
        noClone = true;
        super.onPause();
    }
}