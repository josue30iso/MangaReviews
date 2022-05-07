package com.tripndream.hopeapp;

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

import androidx.annotation.NonNull;
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
import com.tripndream.hopeapp.utils.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditReviewActivity extends AppCompatActivity {

    private static final int GET_IMAGE_CODE = 201;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 301;

    private Toolbar toolbar;
    private ImageView btnInicio, btnMisReviews, btnMiPerfil, btnLogout;

    private SharedPreferences sp;

    private FirebaseAuth fba;
    private FirebaseUser user;

    private Bitmap imgReview = null;
    private String imgReviewB64 = null;

    private ImageView ivReview;
    private Button btnCambiarImagen, btnEditGuardar, btnEditCancelar;
    private EditText etNombre, etDescripcion, etReview;

    private Spinner editCategoria;

    private Intent intent;
    private Uri selectedImage = null;

    public int idReceta;

    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_receta);

        configMaterials();

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

        ivReview = findViewById(R.id.ivEditAlimento);

        btnCambiarImagen = findViewById(R.id.btnCambiarImagen);
        btnEditCancelar = findViewById(R.id.btnEditCancelar);
        btnEditGuardar = findViewById(R.id.btnEditGuardar);

        etDescripcion = findViewById(R.id.etDescripcion);
        etNombre = findViewById(R.id.etNombre);
        etReview = findViewById(R.id.etReview);

        editCategoria = findViewById(R.id.spinnerEditCategoria);

        configIntent();
        configClickListeners();

    }

    private void configIntent() {

        intent = getIntent();

        idReceta = intent.getIntExtra("key_id_receta",-1);

        imgReviewB64 = intent.getStringExtra("key_url_foto");

        byte[] imageBytes = Base64.decode(imgReviewB64, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        ivReview.setImageBitmap(decodedImage);

        etNombre.setText(intent.getStringExtra("key_nombre"));
        editCategoria.setSelection(intent.getIntExtra("key_id_categoria", 0));
        etDescripcion.setText(intent.getStringExtra("key_ingredientes"));
        etReview.setText(intent.getStringExtra("key_preparacion"));

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
                Intent intent = new Intent(EditReviewActivity.this, MisReviewsActivity.class);
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

        btnCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissionES(EditReviewActivity.this)) {
                    cambiarImagen();
                }
            }
        });

        btnEditCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });

        btnEditGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

    }

    String nombre;
    String descripcion;
    String review;
    int idCategoria;
    private void guardar() {

        nombre = etNombre.getText().toString().trim();
        descripcion = etDescripcion.getText().toString().trim();
        review = etReview.getText().toString().trim();

        idCategoria = editCategoria.getSelectedItemPosition();

        if (!(nombre.equals("") || descripcion.equals("") || review.equals("") || idCategoria == 0)) {

            sendImgToServer();

        } else {
            Toast.makeText(EditReviewActivity.this, "Especifique todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private Boolean uploadFlag = false;

    private void sendImgToServer() {

        String name = String.valueOf(idReceta);
        Log.i("ImgSubida", "Name: " + name);
        editRecipe(nombre, descripcion, review, idCategoria, imgReviewB64);

    }

    private void editRecipe(String nombre, String review, String preparacion, int idCategoria, String foto) {

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext(), new HurlStack());

        Map map = new HashMap();
        map.put("id", idReceta);
        map.put("id_categoria", String.valueOf(idCategoria));
        map.put("titulo", nombre);
        map.put("descripcion", preparacion);
        map.put("review", review);
        map.put("foto", foto);

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_REVIEW_EDIT, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");
                    String mensaje = response.getString("message");
                    Log.i("ImgSubida", String.valueOf(response));

                    if (success) {

                        Intent intent = new Intent();
                        intent.putExtra("key_edit_imagen", imgReview);
                        intent.putExtra("key_edit_nombre", nombre);
                        intent.putExtra("key_edit_ingredientes", review);
                        intent.putExtra("key_edit_preparacion", preparacion);
                        intent.putExtra("key_edit_id_categoria", idCategoria);
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        Toast.makeText(EditReviewActivity.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditReviewActivity.this, "Ha ocurrido un error2: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;

    }

    private void cancelar() {

        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();

    }

    private void confirmarLogout() {

        AlertDialog.Builder alert = new AlertDialog.Builder(EditReviewActivity.this, R.style.AlertDialogStyle);
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

    private void updateUIInicio() {

        Intent intent = new Intent();
        intent.putExtra("return_home", true);
        setResult(Activity.RESULT_CANCELED, intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();

    }

    private void cambiarImagen() {

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, GET_IMAGE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GET_IMAGE_CODE) {

            if (data != null) {

                imgReview = null;
                InputStream inputStream;

                try {

                    selectedImage = data.getData();

                    if (imgReview != null) {
                        imgReview.recycle();
                    }

                    inputStream = getContentResolver().openInputStream(data.getData());
                    imgReview = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    ivReview.setImageBitmap(imgReview);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imgReview.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imgReviewB64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

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
        alertBuilder.setTitle("Permiso necesario");
        alertBuilder.setMessage(msg + " es necesario");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Toast.makeText(EditReviewActivity.this, "Acceso denegado",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }


    }
}