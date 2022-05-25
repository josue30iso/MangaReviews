package com.tripndream.hopeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import java.util.Map;

public class MisPubActivity extends AppCompatActivity implements PubAdapter.OnPubClickListener {

    private static final int KEY_AGREGAR_PUB = 401;
    private static final String KEY_ID_VALIDADA = "key_id_validada";
    private Toolbar toolbar;
    private ImageView btnInicio, btnGuardados, btnMiPerfil, btnLogout;
    private TextView tvTitulo;

    private Button btnAgregarPubMisPub;

    private RecyclerView rvPublicaciones;
    private ArrayList<Publicacion> datos;
    private PubAdapter adapter;
    private LinearLayoutManager llm;

    private Intent intent;

    private SharedPreferences sp;

    private Boolean noClone = false;
    private Boolean guardados = false;

    private static final String KEY_TEXTO_NOMBRE = "key_texto_nombre_desa";
    private static final String KEY_TEXTO_DESCRIPCION = "key_texto_descripcion";
    private static final String KEY_URL_FOTO = "key_url_foto";
    private static final String KEY_ID_ZONA = "key_id_zona";
    private static final String KEY_NOMBRE_ZONA = "key_nombre_zona";
    private static final String KEY_TEXTO_ULTIMA = "key_texto_ultima";
    private static final String KEY_TEXTO_NOMBRE_AUTOR = "key_texto_nombre_autor";
    private static final String KEY_ID_USUARIO = "key_id_usuario";
    private static final String KEY_ID_PUBLICACION = "key_id_pub";
    private static final String KEY_GUARDADOS = "key_guardados";
    private static final String KEY_PRIVADA = "key_privada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_publicaciones);

        guardados = false;

        intent = getIntent();
        if (intent != null) {
            guardados = intent.getBooleanExtra(KEY_GUARDADOS, false);
        }

        configToolbar(guardados);
        configMaterials(guardados);
        listarRecycler(guardados);
    }

    private void configToolbar(Boolean guardados) {
        btnInicio = findViewById(R.id.btnInicio);
        btnGuardados = findViewById(R.id.btnGuardados);
        btnMiPerfil = findViewById(R.id.btnMiPerfil);
        btnLogout = findViewById(R.id.btnLogout);

        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MisPubActivity.this, InicioActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_GUARDADOS, true);
                startActivity(intent);
            }
        });

        if (!guardados) {
            btnMiPerfil.setColorFilter(Color.parseColor("#2f4b63"));
            btnGuardados.clearColorFilter();
            btnGuardados.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MisPubActivity.this, MisPubActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(KEY_GUARDADOS, true);
                    startActivity(intent);
                }
            });
        } else {
            btnGuardados.setColorFilter(Color.parseColor("#2f4b63"));
            btnMiPerfil.clearColorFilter();
            btnMiPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MisPubActivity.this, MisPubActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarLogout();
            }
        });
    }

    private void listarRecycler(Boolean guardados) {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext(), new HurlStack());

        Map map = new HashMap();
        map.put("idUsuario", sp.getString("logedID", ""));
        map.put("guardado", guardados);

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_PUB_LISTBYUSER, data, new Response.Listener<JSONObject>() {
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
                        Toast.makeText(MisPubActivity.this, "No se encontraron registros.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MisPubActivity.this, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;

    }

    private void configMaterials(Boolean guardados) {
        sp = getSharedPreferences("emailUserSession", Context.MODE_PRIVATE);

        btnAgregarPubMisPub = findViewById(R.id.btnAgregarPubM);

        rvPublicaciones = findViewById(R.id.rvPublicaciones);

        adapter = new PubAdapter(datos, this, Integer.parseInt(sp.getString("logedID", "")));
        llm = new LinearLayoutManager(getApplicationContext());

        rvPublicaciones.setAdapter(adapter);
        rvPublicaciones.setLayoutManager(llm);

        datos = new ArrayList<Publicacion>();

        tvTitulo = findViewById(R.id.tvTituloPublicacionesP);
        if (guardados) {
            btnAgregarPubMisPub.setVisibility(View.GONE);
            tvTitulo.setText("Guardados");
        } else {
            tvTitulo.setText("Mis publicaciones");
        }

        configClickListeners();
    }

    private void configClickListeners() {

        btnAgregarPubMisPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MisPubActivity.this, AgregarPubActivity.class);
                startActivityForResult(intent, KEY_AGREGAR_PUB);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == KEY_AGREGAR_PUB){
            listarRecycler(guardados);
        }else if (resultCode == RESULT_CANCELED){

        }
    }

    private void confirmarLogout() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MisPubActivity.this, R.style.AlertDialogStyle);
        alert.setTitle(Html.fromHtml("<font color='#E77FB3'>Está a punto de cerrar sesión</font>"))
                .setMessage(Html.fromHtml("<font color='#E77FB3'>¿Realmente deseas cerrar sesión?</font>"))
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

    private void updateUILogout() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPubClickListener(int position) {

        Intent intent = new Intent(getApplicationContext(), PubActivity.class);

        Publicacion publicacion = datos.get(position);

        intent.putExtra(KEY_TEXTO_NOMBRE, publicacion.getNombreDesaparecido());
        intent.putExtra(KEY_ID_ZONA, publicacion.getIdZona());
        intent.putExtra(KEY_NOMBRE_ZONA, publicacion.getNombreZona());
        intent.putExtra(KEY_TEXTO_NOMBRE_AUTOR, publicacion.getNombreUsuario());
        intent.putExtra(KEY_URL_FOTO, publicacion.getFoto());
        intent.putExtra(KEY_TEXTO_ULTIMA, publicacion.getUltimoVistazo());
        intent.putExtra(KEY_TEXTO_DESCRIPCION, publicacion.getDescripcion());
        intent.putExtra(KEY_ID_USUARIO, publicacion.getIdUsuario());
        intent.putExtra(KEY_ID_PUBLICACION, publicacion.getId());
        intent.putExtra(KEY_ID_VALIDADA, publicacion.getValidada());

        startActivity(intent);

        noClone = true;

    }

    @Override
    protected void onResume() {
        adapter.clear();
        datos.clear();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (noClone) listarRecycler(guardados);
        super.onResume();
    }

    @Override
    protected void onPause() {
        noClone = true;
        super.onPause();
    }
}