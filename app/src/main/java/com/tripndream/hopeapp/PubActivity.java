package com.tripndream.hopeapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.tripndream.hopeapp.utils.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PubActivity extends AppCompatActivity {

    private static final String KEY_URL_FOTO = "key_url_foto";
    private static final String KEY_ID_USUARIO = "key_id_usario";
    private static final String KEY_ID_PUB = "key_id_pub";
    private static final String KEY_NOMBRE = "key_texto_nombre_desa";
    private static final String KEY_ID_ZONA = "key_id_zona";
    private static final String KEY_NOMBRE_ZONA = "key_nombre_zona";
    private static final String KEY_DESCRIPCION = "key_texto_descripcion";
    private static final String KEY_ULTIMA = "key_texto_ultima";
    private static final String KEY_PRIVADA = "key_privada";
    private static final String KEY_GUARDADOS = "key_guardados";

    private final int KEY_EDIT_REVIEW = 101;

    private Toolbar toolbar;
    private ImageView btnInicio, btnGuardados, btnMiPerfil, btnLogout;

    private ImageView ivPub;
    private Button btnEditar, btnEliminar;
    private TextView tvNombre, tvZona, tvAutor, tvDescripcion, tvUltimoVistazo;

    private SharedPreferences sp;

    private Intent intent;

    public int idPublicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUIInicio();
            }
        });

        btnGuardados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PubActivity.this, MisPubActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_GUARDADOS, true);
                startActivity(intent);
            }
        });

        btnMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PubActivity.this, MisPubActivity.class);
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

    private void configMaterials() {

        sp = getSharedPreferences("emailUserSession", Context.MODE_PRIVATE);

        ivPub = findViewById(R.id.ivFotoDesaparecido);

        btnEditar = findViewById(R.id.btnEditGuardar);
        btnEliminar = findViewById(R.id.btnEditEliminar);

        tvNombre = findViewById(R.id.etNombreDesaparecido);
        tvZona = findViewById(R.id.etZonaPub);
        tvAutor = findViewById(R.id.etAutorPub);
        tvDescripcion = findViewById(R.id.etDescripcion);
        tvUltimoVistazo = findViewById(R.id.etUltimoVistazo);

        configIntent();
        configClickListeners();

    }

    private void configIntent() {

        intent = getIntent();

        String b64 = intent.getStringExtra("key_url_foto");

        byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        ivPub.setImageBitmap(decodedImage);

        tvNombre.setText(intent.getStringExtra("key_texto_nombre_desa"));

        String zona = intent.getStringExtra("key_nombre_zona");
        tvZona.setText(zona);

        tvAutor.setText(intent.getStringExtra("key_texto_nombre_autor"));

        tvDescripcion.setText(intent.getStringExtra("key_texto_descripcion"));

        tvUltimoVistazo.setText(intent.getStringExtra("key_texto_ultima"));

        idPublicacion = intent.getIntExtra("key_id_pub",-1);

        String idUsuario = String.valueOf(intent.getIntExtra("key_id_usuario", -1));

        if (!idUsuario.equals(sp.getString("logedID", ""))) {
            btnEditar.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);
        }

    }

    private void configClickListeners() {

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(PubActivity.this, R.style.AlertDialogStyle);
                alert.setTitle(Html.fromHtml("<font color='#E77FB3'>Está a punto de eliminar esta publicacion</font>"))
                        .setMessage(Html.fromHtml("<font color='#E77FB3'>¿Realmente deseas eliminarla? No podras deshacer esta acción</font>"))
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
        map.put("id", String.valueOf(idPublicacion));

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_PUB_DELETE, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");
                    String mensaje = response.getString("message");
                    Log.i("RecetaActivity", String.valueOf(response));

                    if (success) {

                        Toast.makeText(PubActivity.this, "Eliminación exitosa", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Toast.makeText(PubActivity.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PubActivity.this, "Ha ocurrido un error2: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;


    }

    private void editar() {

        Intent intent = new Intent(this, EditPubActivity.class);

        intent.putExtra(KEY_ID_PUB, this.intent.getIntExtra("key_id_pub", -1));
        intent.putExtra(KEY_URL_FOTO, this.intent.getStringExtra("key_url_foto"));
        intent.putExtra(KEY_NOMBRE, this.intent.getStringExtra("key_texto_nombre_desa"));
        intent.putExtra(KEY_ID_ZONA, this.intent.getIntExtra("key_id_zona", 0));
        intent.putExtra(KEY_NOMBRE_ZONA, this.intent.getStringExtra("key_nombre_zona"));
        intent.putExtra(KEY_DESCRIPCION, this.intent.getStringExtra("key_texto_descripcion"));
        intent.putExtra(KEY_ULTIMA, this.intent.getStringExtra("key_texto_ultima"));
        intent.putExtra(KEY_PRIVADA, this.intent.getIntExtra("key_privada", -1));

        startActivityForResult(intent, KEY_EDIT_REVIEW);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == KEY_EDIT_REVIEW && resultCode == RESULT_OK && data != null) {

            String b64 = data.getStringExtra("key_edit_imagen");
            byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivPub.setImageBitmap(decodedImage);

            tvNombre.setText(data.getStringExtra("key_edit_nombre"));
            tvDescripcion.setText(data.getStringExtra("key_edit_descripcion"));
            tvUltimoVistazo.setText(data.getStringExtra("key_edit_ultima"));

            tvZona.setText(data.getStringExtra("key_edit_zona"));

            this.intent.putExtra("key_url_foto", b64);
            this.intent.putExtra("key_texto_nombre_desa", data.getStringExtra("key_edit_nombre"));
            this.intent.putExtra("key_id_zona", data.getIntExtra("key_edit_id_zona", -1));
            this.intent.putExtra("key_nombre_zona", data.getStringExtra("key_edit_zona"));
            this.intent.putExtra("key_texto_descripcion", data.getStringExtra("key_edit_descripcion"));
            this.intent.putExtra("key_texto_ultima", data.getStringExtra("key_edit_ultima"));
            this.intent.putExtra("key_privada", data.getIntExtra("key_privada", -1));

        } else if (resultCode == RESULT_CANCELED && requestCode == KEY_EDIT_REVIEW) {
            if (data.getBooleanExtra("return_home", false)) {
                finish();
            } else if (data.getBooleanExtra("logout", false)) {
                updateUILogout();
            }
        }

    }

    private void confirmarLogout() {

        AlertDialog.Builder alert = new AlertDialog.Builder(PubActivity.this, R.style.AlertDialogStyle);
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