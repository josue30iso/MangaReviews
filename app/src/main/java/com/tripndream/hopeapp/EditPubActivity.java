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
import android.widget.ArrayAdapter;
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
import com.tripndream.hopeapp.utils.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditPubActivity extends AppCompatActivity {

    private static final int GET_IMAGE_CODE = 201;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 301;
    private static final String KEY_GUARDADOS = "key_guardados";

    private Toolbar toolbar;
    private ImageView btnInicio, btnGuardados, btnMiPerfil, btnLogout;

    private SharedPreferences sp;

    private Bitmap imgReview = null;
    private String imgReviewB64 = null;

    private ImageView ivPublicacion;
    private Button btnCambiarImagen, btnEditGuardar, btnEditCancelar;
    private EditText etNombre, etDescripcion, etUltimoVistazo;

    private Spinner spinnerEditZona;

    private Intent intent;
    private Uri selectedImage = null;
    private ArrayAdapter<String> adapterSp;

    public int idPublicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pub);

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
                Intent intent = new Intent(EditPubActivity.this, MisPubActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_GUARDADOS, true);
                startActivity(intent);
            }
        });

        btnMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPubActivity.this, MisPubActivity.class);
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

        ivPublicacion = findViewById(R.id.ivFotoDesaparecidoEdit);

        btnCambiarImagen = findViewById(R.id.btnCambiarImagen);
        btnEditCancelar = findViewById(R.id.btnEditEliminarE);
        btnEditGuardar = findViewById(R.id.btnEditGuardarE);

        etDescripcion = findViewById(R.id.etDescripcionE);
        etNombre = findViewById(R.id.etNombreDesaparecidoE);
        etUltimoVistazo = findViewById(R.id.etUltimoVistazoE);

        spinnerEditZona = findViewById(R.id.spinnerEditZona);

        llenarSpinner();

        configIntent();

        configClickListeners();
        Log.i("ASUMAKINA", "Cabron");
    }

    private void llenarSpinner() {
        try {

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
                            arraySpinner.add("Seleccione zona...");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject zonaObj = data.getJSONObject(i);
                                try {
                                    arraySpinner.add(zonaObj.getString("nombre"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            adapterSp = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arraySpinner);
                            adapterSp.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                            spinnerEditZona.setAdapter(adapterSp);
                        } else {
                            Toast.makeText(EditPubActivity.this, "Sin resultados.", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EditPubActivity.this, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;

        } catch (Exception e) {
            Log.i("ASUMAKINA", e.getMessage());
        }
    }

    private void configIntent() {
        try {
            intent = getIntent();
            if (intent != null) {
                idPublicacion = intent.getIntExtra("key_id_pub",-1);

                imgReviewB64 = intent.getStringExtra("key_url_foto");

                byte[] imageBytes = Base64.decode(imgReviewB64, Base64.DEFAULT);
                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                ivPublicacion.setImageBitmap(decodedImage);

                etNombre.setText(intent.getStringExtra("key_texto_nombre_desa"));
                //spinnerEditZona.setSelection(intent.getIntExtra("key_id_zona", 0));

                etDescripcion.setText(intent.getStringExtra("key_texto_descripcion"));
                etUltimoVistazo.setText(intent.getStringExtra("key_texto_ultima"));
            }
        } catch (Exception e) {
            Log.i("ASUMAKINA", e.getMessage());
        }
    }

    private void configClickListeners() {

        btnCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissionES(EditPubActivity.this)) {
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
    String ultimoVistazo;
    int idZona;
    private void guardar() {

        nombre = etNombre.getText().toString().trim();
        descripcion = etDescripcion.getText().toString().trim();
        ultimoVistazo = etUltimoVistazo.getText().toString().trim();

        idZona = spinnerEditZona.getSelectedItemPosition();

        if (!(nombre.equals("") || descripcion.equals("") || ultimoVistazo.equals("") || idZona == 0)) {

            send();

        } else {
            Toast.makeText(EditPubActivity.this, "Especifique todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private Boolean uploadFlag = false;

    private void send() {

        editPublicacion(nombre, descripcion, ultimoVistazo, idZona, imgReviewB64);

    }

    private void editPublicacion(String nombreDesaparecido, String ultimoVistazo, String descripcion, int idZona, String foto) {

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext(), new HurlStack());

        this.nombre = nombreDesaparecido;
        this.descripcion = descripcion;
        this.ultimoVistazo = ultimoVistazo;
        this.idZona = idZona;

        Map map = new HashMap();
        map.put("id", idPublicacion);
        map.put("id_zona", String.valueOf(idZona));
        map.put("nombreDesaparecido", nombreDesaparecido);
        map.put("descripcion", descripcion);
        map.put("ultimoVistazo", ultimoVistazo);
        map.put("foto", foto);

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_PUB_EDIT, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");
                    String mensaje = response.getString("message");
                    Log.i("ImgSubida", String.valueOf(response));

                    if (success) {

                        Intent intent = new Intent();
                        intent.putExtra("key_edit_imagen", imgReviewB64);
                        intent.putExtra("key_edit_nombre", nombreDesaparecido);
                        intent.putExtra("key_edit_descripcion", descripcion);
                        intent.putExtra("key_edit_ultima", ultimoVistazo);
                        intent.putExtra("key_edit_id_zona", spinnerEditZona.getSelectedItemPosition());
                        intent.putExtra("key_edit_zona", spinnerEditZona.getSelectedItem().toString());
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        Toast.makeText(EditPubActivity.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditPubActivity.this, "Ha ocurrido un error2: " + error.getMessage(), Toast.LENGTH_LONG).show();
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

        AlertDialog.Builder alert = new AlertDialog.Builder(EditPubActivity.this, R.style.AlertDialogStyle);
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
                    ivPublicacion.setImageBitmap(imgReview);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imgReview.compress(Bitmap.CompressFormat.JPEG, 15, baos);
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
                    Toast.makeText(EditPubActivity.this, "Acceso denegado",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }


    }
}