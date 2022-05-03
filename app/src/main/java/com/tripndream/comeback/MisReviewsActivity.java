package com.tripndream.comeback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tripndream.comeback.utils.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MisReviewsActivity extends AppCompatActivity implements ReviewAdapter.OnRecetaClickListener {
    private static final int KEY_AGREGAR_RECETA = 401;
    private Toolbar toolbar;
    private ImageView btnInicio, btnMisRecetas, btnMiPerfil, btnLogout;

    private Button btnAgregarRecetaMisRecetas;

    private RecyclerView rvRecetas;
    private ArrayList<Review> datos;
    private ReviewAdapter adapter;
    private LinearLayoutManager llm;

    private FirebaseAuth fba;
    private FirebaseUser user;

    private SharedPreferences sp;

    private StorageReference storageRef;

    private Boolean noClone = false;

    private static final String KEY_TEXTO_NOMBRE = "key_texto_nombre_receta";
    private static final String KEY_TEXTO_DESCRIPCION = "key_texto_descripcion_receta";
    private static final String KEY_URL_FOTO = "key_url_foto";
    private static final String KEY_ID_CATEGORIA = "key_id_categoria";
    private static final String KEY_TEXTO_REVIEW = "key_texto_ingredientes_receta";
    private static final String KEY_TEXTO_NOMBRE_AUTOR = "key_texto_nombre_autor";
    private static final String KEY_ID_USUARIO = "key_id_usuario";
    private static final String KEY_ID_RECETA = "key_id_receta";
    private static final String KEY_PRIVADA = "key_privada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_recetas);

        configMaterials();
        listarRecycler();
    }

    private void listarRecycler() {
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext(), new HurlStack());

        Map map = new HashMap();
        map.put("idUsuario", sp.getString("logedID", ""));

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_REVIEW_LISTBYUSER, data, new Response.Listener<JSONObject>() {
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

                                    Review receta = null;
                                    try {
                                        receta = new Review(
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

                                    adapter.add(receta);
                                    datos.add(receta);

                                }
                            //});

                        //}

                    } else {
                        Toast.makeText(MisReviewsActivity.this, "No tienes recetas. Prueba agregando una.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MisReviewsActivity.this, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;

    }

    private void configMaterials() {
        sp = getSharedPreferences("emailUserSession", Context.MODE_PRIVATE);

        storageRef = FirebaseStorage.getInstance().getReference();

        btnInicio = findViewById(R.id.btnInicio);
        btnMisRecetas = findViewById(R.id.btnMisReviews);
        btnAgregarRecetaMisRecetas = findViewById(R.id.btnAgregarRecetaMisRecetas);
        btnLogout = findViewById(R.id.btnLogout);

        rvRecetas = findViewById(R.id.rvRecetas);

        datos = new ArrayList<Review>();

        adapter = new ReviewAdapter(datos, this);
        llm = new LinearLayoutManager(getApplicationContext());

        rvRecetas.setAdapter(adapter);
        rvRecetas.setLayoutManager(llm);

        fba = FirebaseAuth.getInstance();

        user = fba.getCurrentUser();

        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        configClickListeners();
    }

    private void configClickListeners() {
        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MisReviewsActivity.this, InicioActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnMisRecetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainActivity", "Mis recetas");
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarLogout();
            }
        });

        btnAgregarRecetaMisRecetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MisReviewsActivity.this, AgregarReviewActivity.class);
                startActivityForResult(intent, KEY_AGREGAR_RECETA);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == KEY_AGREGAR_RECETA){
            listarRecycler();
        }else if (resultCode == RESULT_CANCELED){

        }
    }

    private void confirmarLogout() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MisReviewsActivity.this, R.style.AlertDialogStyle);
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
        intent.putExtra(KEY_ID_RECETA, receta.getId());

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
        if (noClone) listarRecycler();
        super.onResume();
    }

    @Override
    protected void onPause() {
        noClone = true;
        super.onPause();
    }
}