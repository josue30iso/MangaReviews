package com.tripndream.comeback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tripndream.comeback.utils.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {

    private static final String KEY_URL_FOTO = "key_url_foto";
    private static final String KEY_ID_RECETA = "key_id_receta";
    private static final String KEY_NOMBRE = "key_nombre";
    private static final String KEY_ID_CATEGORIA = "key_id_categoria";
    private static final String KEY_DESCRIPCION = "key_ingredientes";
    private static final String KEY_REVIEW = "key_preparacion";
    private static final String KEY_PRIVADA = "key_privada";

    private final int KEY_EDIT_REVIEW = 101;

    private Toolbar toolbar;
    private ImageView btnInicio, btnMisRecetas, btnMiPerfil, btnLogout;

    private ImageView ivManga;
    private Button btnEditar, btnEliminar;
    private TextView tvNombre, tvCategoria, tvAutor, tvDescripcion, tvReview;

    private SharedPreferences sp;

    private FirebaseAuth fba;
    private FirebaseUser user;

    private Intent intent;

    public int idReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        configMaterials();
    }

    private void configMaterials() {

        sp = getSharedPreferences("emailUserSession", Context.MODE_PRIVATE);

        btnInicio = findViewById(R.id.btnInicio);
        btnMisRecetas = findViewById(R.id.btnMisReviews);
        btnLogout = findViewById(R.id.btnLogout);

        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        ivManga = findViewById(R.id.ivEditAlimento);

        btnEditar = findViewById(R.id.btnEditGuardar);
        btnEliminar = findViewById(R.id.btnEditCancelar);

        tvNombre = findViewById(R.id.etNombre);
        tvCategoria = findViewById(R.id.etCategoria);
        tvAutor = findViewById(R.id.etAutorComida);
        tvDescripcion = findViewById(R.id.etDescripcion);
        tvReview = findViewById(R.id.etReview);

        configIntent();
        configClickListeners();

    }

    private void configIntent() {

        intent = getIntent();

        String b64 = intent.getStringExtra("key_url_foto");

        byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        ivManga.setImageBitmap(decodedImage);

        tvNombre.setText(intent.getStringExtra("key_texto_nombre_receta"));

        Resources res = getResources();
        String s[] = res.getStringArray(R.array.categories);
        String categoria = s[intent.getIntExtra("key_id_categoria",0)];
        tvCategoria.setText(categoria);

        tvAutor.setText(intent.getStringExtra("key_texto_nombre_autor"));

        tvDescripcion.setText(intent.getStringExtra("key_texto_ingredientes_receta"));

        tvReview.setText(intent.getStringExtra("key_texto_descripcion_receta"));

        idReview = intent.getIntExtra("key_id_receta",-1);

        String idUsuario = intent.getStringExtra("key_id_usuario");

        if (!idUsuario.equals(sp.getString("logedID", ""))) {
            btnEditar.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);
        }

    }

    private void configClickListeners() {

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUIInicio();
            }
        });

        btnMisRecetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, MisReviewsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarLogout();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ReviewActivity.this, R.style.AlertDialogStyle);
                alert.setTitle(Html.fromHtml("<font color='#E77FB3'>Está a punto de eliminar esta receta</font>"))
                        .setMessage(Html.fromHtml("<font color='#E77FB3'>¿Realmente deseas eliminarla?</font>"))
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                eliminar();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editar();
            }
        });

    }

    private void eliminar() {

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        Map map = new HashMap();
        map.put("id", String.valueOf(idReview));

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_REVIEW_DELETE, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");
                    String mensaje = response.getString("message");
                    Log.i("RecetaActivity", String.valueOf(response));

                    if (success) {

                        Toast.makeText(ReviewActivity.this, "Eliminación exitosa", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(ReviewActivity.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ReviewActivity.this, "Ha ocurrido un error2: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;


    }

    private void editar() {

        Intent intent = new Intent(this, EditReviewActivity.class);

        intent.putExtra(KEY_ID_RECETA, this.intent.getIntExtra("key_id_receta", -1));
        intent.putExtra(KEY_URL_FOTO, this.intent.getStringExtra("key_url_foto"));
        intent.putExtra(KEY_NOMBRE, this.intent.getStringExtra("key_texto_nombre_receta"));
        intent.putExtra(KEY_ID_CATEGORIA, this.intent.getIntExtra("key_id_categoria", -1));
        intent.putExtra(KEY_DESCRIPCION, this.intent.getStringExtra("key_texto_ingredientes_receta"));
        intent.putExtra(KEY_REVIEW, this.intent.getStringExtra("key_texto_descripcion_receta"));
        intent.putExtra(KEY_PRIVADA, this.intent.getIntExtra("key_privada", -1));

        startActivityForResult(intent, KEY_EDIT_REVIEW);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == KEY_EDIT_REVIEW && resultCode == RESULT_OK && data != null) {

            if (data.getParcelableExtra("key_edit_imagen") != null) {
                ivManga.setImageBitmap((Bitmap) data.getParcelableExtra("key_edit_imagen"));
            }
            tvNombre.setText(data.getStringExtra("key_edit_nombre"));
            tvDescripcion.setText(data.getStringExtra("key_edit_ingredientes"));
            tvReview.setText(data.getStringExtra("key_edit_preparacion"));

            Resources res = getResources();
            String s[] = res.getStringArray(R.array.categories);
            String categoria = s[data.getIntExtra("key_id_categoria",0)];
            tvCategoria.setText(categoria);

        } else if (resultCode == RESULT_CANCELED && requestCode == KEY_EDIT_REVIEW) {
            if (data.getBooleanExtra("return_home", false)) {
                finish();
            } else if (data.getBooleanExtra("logout", false)) {
                updateUILogout();
            }
        }

    }

    private void confirmarLogout() {

        AlertDialog.Builder alert = new AlertDialog.Builder(ReviewActivity.this, R.style.AlertDialogStyle);
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

    private void updateUIInicio() {

        Intent intent = new Intent(this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    private void updateUILogout() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}