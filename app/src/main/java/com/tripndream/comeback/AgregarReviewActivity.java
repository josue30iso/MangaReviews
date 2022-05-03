package com.tripndream.comeback;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AgregarReviewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 301;
    private static final int GET_IMAGE_CODE = 202;

    private ImageView btnInicio, btnMisReviews, btnLogout;

    private ImageView ivAgregaReceta;
    private Bitmap imgReview;
    private String imgReviewB64;
    private Button btnAgregaImagen, btnAgregaGuardar, btnAgregaCancelar;
    private EditText etAgregaNombre, etAgregaDescripcion, etAgregaReview;
    private Spinner spnAgregaCategoria;

    private Intent intent;

    private FirebaseAuth fba;
    private FirebaseUser user;
    private SharedPreferences sp;
    private Uri Image = null;
    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_receta);

        configMaterials();
        configClickListeners();

    }

    private void configClickListeners() {
        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUIInicio();
            }
        });


        btnMisReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUIMisReviews();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarLogout();
            }
        });

        btnAgregaImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissionES(AgregarReviewActivity.this)) {
                    cambiarImagen();
                }
            }
        });

        btnAgregaGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        btnAgregaCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GET_IMAGE_CODE) {

            if (data != null) {

                imgReview = null;
                InputStream inputStream;

                try {

                    Image = data.getData();

                    if (imgReview != null) {
                        imgReview.recycle();
                    }

                    inputStream = getContentResolver().openInputStream(data.getData());
                    imgReview = BitmapFactory.decodeStream(inputStream);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imgReview.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imgReviewB64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    inputStream.close();
                    ivAgregaReceta.setImageBitmap(imgReview);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    String nombre, usuario, descripcion, review;
    int idCategoria;
    private void guardar() {

        nombre = etAgregaNombre.getText().toString().trim();
        descripcion = etAgregaDescripcion.getText().toString().trim();
        review = etAgregaReview.getText().toString().trim();
        usuario = sp.getString("logedID", "");

        idCategoria = spnAgregaCategoria.getSelectedItemPosition();

        if (!(nombre.equals("") || descripcion.equals("") || review.equals("") || idCategoria == 0)) {

            agregaReceta(usuario, nombre, descripcion, review, idCategoria, imgReviewB64);

        } else {
            Toast.makeText(AgregarReviewActivity.this, "Especifique todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void agregaReceta(String usuario, String nombre, String review, String preparacion, int idCategoria, String imagen) {

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
        UUID uuid = UUID.randomUUID();

        Map map = new HashMap();

        map.put("id_usuario", usuario);
        map.put("id_categoria", idCategoria);
        map.put("titulo", nombre);
        map.put("descripcion", preparacion);
        map.put("review", review);
        map.put("foto", imagen);
        map.put("guid", uuid.toString());

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_REVIEW_ADD, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");
                    String mensaje = response.getString("message");
                    Log.i("ImgSubida", String.valueOf(response));

                    if (success) {

                        Intent intent = new Intent();
                        Toast.makeText(AgregarReviewActivity.this, "Review publicada. Espere un momento...", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        if (!mensaje.equals("Bypass")) {
                            Toast.makeText(AgregarReviewActivity.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AgregarReviewActivity.this, "Ha ocurrido un error2: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void cancelar() {

        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();

    }

    private void cambiarImagen() {

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, GET_IMAGE_CODE);

    }

    private void updateUIInicio() {

        Intent intent = new Intent();
        intent.putExtra("return_home", true);
        setResult(Activity.RESULT_CANCELED, intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();

    }

    private void updateUIMisReviews(){
        Intent intent = new Intent();
        intent.putExtra("return_home", true);
        setResult(Activity.RESULT_CANCELED, intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent intent1 = new Intent(AgregarReviewActivity.this, MisReviewsActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        finish();
    }

    private void confirmarLogout() {

        AlertDialog.Builder alert = new AlertDialog.Builder(AgregarReviewActivity.this, R.style.AlertDialogStyle);
        alert.setTitle(Html.fromHtml("<font color='#E77FB3'>Está a punto de cerrar sesión</font>"))
                .setMessage(Html.fromHtml("<font color='#E77FB3'>¿Realmente deseas cerrar sesión?</font>"))
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("session", "");
                        editor.putString("logedID", "");
                        editor.commit();

                        Intent intent = new Intent();
                        intent.putExtra("logout", true);
                        setResult(Activity.RESULT_CANCELED, intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();

    }

    public boolean checkPermissionES(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("Permiso para leer ext ", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(Html.fromHtml("<font color='#E77FB3'>Permiso necesario</font>"));
        alertBuilder.setMessage(Html.fromHtml("<font color='#E77FB3'>"+ msg +" es necesario</font>"));
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void configMaterials() {
        sp = getSharedPreferences("emailUserSession", Context.MODE_PRIVATE);

        storageRef = FirebaseStorage.getInstance().getReference();
        btnInicio = findViewById(R.id.btnInicio);
        btnMisReviews = findViewById(R.id.btnMisReviews);
        btnLogout = findViewById(R.id.btnLogout);

        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        setTitle(null);

        ivAgregaReceta = findViewById(R.id.ivAgregaReceta);
        btnAgregaImagen = findViewById(R.id.btnAgregaImagen);
        etAgregaNombre = findViewById(R.id.etAgregaNombre);
        etAgregaDescripcion = findViewById(R.id.etAgregaDescripcion);
        etAgregaReview = findViewById(R.id.etAgregaReview);
        spnAgregaCategoria = findViewById(R.id.spnAgregaCategoria);
        btnAgregaGuardar = findViewById(R.id.btnAgregaGuardar);
        btnAgregaCancelar = findViewById(R.id.btnAgregaCancelar);

    }
}