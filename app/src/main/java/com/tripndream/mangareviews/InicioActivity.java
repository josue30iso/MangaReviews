package com.tripndream.mangareviews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
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
import com.tripndream.mangareviews.utils.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InicioActivity extends AppCompatActivity implements ReviewAdapter.OnRecetaClickListener {

    private static final String KEY_TEXTO_NOMBRE = "key_texto_nombre_receta";
    private static final String KEY_TEXTO_DESCRIPCION = "key_texto_descripcion_receta";
    private static final String KEY_URL_FOTO = "key_url_foto";
    private static final String KEY_ID_CATEGORIA = "key_id_categoria";
    private static final String KEY_TEXTO_REVIEW = "key_texto_ingredientes_receta";
    private static final String KEY_TEXTO_NOMBRE_AUTOR = "key_texto_nombre_autor";
    private static final String KEY_ID_USUARIO = "key_id_usuario";
    private static final String KEY_ID_REV = "key_id_receta";
    private static final String KEY_PRIVADA = "key_privada";

    private Toolbar toolbar;
    private ImageView btnInicio, btnMisRecetas, btnMiPerfil, btnLogout;
    private Spinner spinnerFiltro;

    private RecyclerView rvRecetas;
    private ArrayList<Review> datos;
    private ReviewAdapter adapter;
    private LinearLayoutManager llm;
    private int posSpinner = 0;

    private Button btnMisRecetasInicio;

    private SharedPreferences sp;

    private Boolean noClone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        configMaterials();

    }

    private void updateUILogout() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void configMaterials() {
        sp = getSharedPreferences("emailUserSession", Context.MODE_PRIVATE);

        btnInicio = findViewById(R.id.btnInicio);
        btnMisRecetas = findViewById(R.id.btnMisReviews);
        btnMisRecetasInicio = findViewById(R.id.btnMisRecetasInicio);
        btnLogout = findViewById(R.id.btnLogout);

        spinnerFiltro = findViewById(R.id.spinnerFiltro);

        rvRecetas = findViewById(R.id.rvRecetas);

        datos = new ArrayList<Review>();

        adapter = new ReviewAdapter(datos, this);
        llm = new LinearLayoutManager(getApplicationContext());

        rvRecetas.setAdapter(adapter);
        rvRecetas.setLayoutManager(llm);

        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        /***************************** TEST RV ***********************************//*
        adapter.add(new Receta(1, "Jorge",2, "Cosa", "Asumakna", "2020-01-01","https://i0.wp.com/goula.lat/wp-content/uploads/2019/12/hamburguesa-beyond-meat-scaled-e1577396155298.jpg", 1));
        adapter.add(new Receta(2, "Capo",2, "Cosa", "Asumakna", "2020-01-01","https://i0.wp.com/goula.lat/wp-content/uploads/2019/12/hamburguesa-beyond-meat-scaled-e1577396155298.jpg", 1));
        adapter.add(new Receta(2, "Children",2, "Cosa", "Asumakna", "2020-01-01","https://i0.wp.com/goula.lat/wp-content/uploads/2019/12/hamburguesa-beyond-meat-scaled-e1577396155298.jpg", 1));
        adapter.add(new Receta(2, "Ese",2, "Cosa", "Asumakna", "2020-01-01","https://i0.wp.com/goula.lat/wp-content/uploads/2019/12/hamburguesa-beyond-meat-scaled-e1577396155298.jpg", 1));
        *//***********************************************************************/

        configClickListeners();

    }

    private void listaPorCategoria(int i) {

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext(), new HurlStack());

        Map map = new HashMap();
        map.put("idCategoria", i);

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_REVIEW_LISTBYCAT, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");
                    String mensaje = response.getString("message");

                    adapter.clear();
                    datos.clear();

                    if (success) {

                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {

                            JSONObject recetaObj = data.getJSONObject(i);

                            //StorageReference imgRef = storageRef.child(recetaObj.getString("foto"));
                            //imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                //@Override
                                //public void onSuccess(Uri uri) {
                                    //String url = uri.toString();

                                    Review review = null;
                                    try {
                                        review = new Review(
                                                recetaObj.getInt("id"),
                                                recetaObj.getString("idUsuario"),
                                                recetaObj.getString("nombreUsuario"),
                                                recetaObj.getInt("idCategoria"),
                                                recetaObj.getString("titulo"),
                                                recetaObj.getString("descripcion"),
                                                recetaObj.getString("fechaRegistro"),
                                                recetaObj.getString("foto"),
                                                recetaObj.getString("review")
                                        );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    adapter.add(review);
                                    datos.add(review);

                                //}
                            //});

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

        btnMisRecetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioActivity.this, MisReviewsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        btnMisRecetasInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InicioActivity.this, MisReviewsActivity.class);
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

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                listaPorCategoria(i);
                posSpinner = i;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

    }

    private void confirmarLogout() {

        AlertDialog.Builder alert = new AlertDialog.Builder(InicioActivity.this, R.style.AlertDialogStyle);
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

    @Override
    public void onRecetaClickListener(int position) {

        Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);

        Review receta = datos.get(position);

        intent.putExtra(KEY_TEXTO_NOMBRE, receta.getTitulo());
        intent.putExtra(KEY_ID_CATEGORIA, receta.getIdCategoria());
        intent.putExtra(KEY_TEXTO_NOMBRE_AUTOR, receta.getNombreUsuario());
        intent.putExtra(KEY_URL_FOTO, receta.getImagen());
        intent.putExtra(KEY_TEXTO_REVIEW, receta.getReview());
        intent.putExtra(KEY_TEXTO_DESCRIPCION, receta.getDescripcion());
        intent.putExtra(KEY_ID_USUARIO, receta.getIdUsuario());
        intent.putExtra(KEY_ID_REV, receta.getId());

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
        if (noClone) listaPorCategoria(posSpinner);
        super.onResume();
    }

    @Override
    protected void onPause() {
        noClone = true;
        super.onPause();
    }
}