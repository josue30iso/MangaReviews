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
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.UUID;

public class AgregarPubActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 301;
    private static final int GET_IMAGE_CODE = 202;
    private static final String KEY_GUARDADOS = "key_guardados";

    private ImageView btnInicio, btnGuardados, btnMiPerfil, btnLogout;

    private ImageView ivAgregaPub;
    private Bitmap imgPub;
    private String imgPubB64;
    private Button btnAgregaImagen, btnAgregaGuardar, btnAgregaCancelar;
    private EditText etAgregaNombre, etAgregaPubb, etAgregaPub;
    private Spinner spnAgregaZona;

    private Intent intent;
    private ArrayAdapter<String> adapterSp;

    private SharedPreferences sp;
    private Uri Image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_pub);

        configToolbar();
        configMaterials();
        configClickListeners();
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
                Intent intent = new Intent(AgregarPubActivity.this, MisPubActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_GUARDADOS, true);
                startActivity(intent);
            }
        });

        btnMiPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AgregarPubActivity.this, MisPubActivity.class);
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

    private void configClickListeners() {
        btnAgregaImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissionES(AgregarPubActivity.this)) {
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

                imgPub = null;
                InputStream inputStream;

                try {

                    Image = data.getData();

                    if (imgPub != null) {
                        imgPub.recycle();
                    }

                    inputStream = getContentResolver().openInputStream(data.getData());
                    imgPub = BitmapFactory.decodeStream(inputStream);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imgPub.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imgPubB64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    inputStream.close();
                    ivAgregaPub.setImageBitmap(imgPub);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    String nombre, usuario, descripcion, review;
    int idZona;
    private void guardar() {

        nombre = etAgregaNombre.getText().toString().trim();
        descripcion = etAgregaPubb.getText().toString().trim();
        review = etAgregaPub.getText().toString().trim();
        usuario = sp.getString("logedID", "");

        idZona = spnAgregaZona.getSelectedItemPosition();

        if (!(nombre.equals("") || descripcion.equals("") || review.equals("") || idZona == 0)) {

            agregaPublicacion(usuario, nombre, descripcion, review, idZona, imgPubB64);

        } else {
            Toast.makeText(AgregarPubActivity.this, "Especifique todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void agregaPublicacion(String usuario, String nombreDesaparecido, String ultimoVistazo, String descripcion, int idZona, String imagen) {

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
        UUID uuid = UUID.randomUUID();

        Map map = new HashMap();

        map.put("id_usuario", usuario);
        map.put("id_zona", idZona);
        map.put("nombreDesaparecido", nombreDesaparecido);
        map.put("descripcion", descripcion);
        map.put("ultimoVistazo", ultimoVistazo);
        map.put("foto", imagen);
        map.put("guid", uuid.toString());

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_PUB_ADD, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");
                    String mensaje = response.getString("message");

                    if (success) {

                        Intent intent = new Intent();
                        Toast.makeText(AgregarPubActivity.this, "Subida exitosa. Un administrador validará su publicacion...", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        if (!mensaje.equals("Bypass")) {
                            Toast.makeText(AgregarPubActivity.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AgregarPubActivity.this, "Ha ocurrido un error2: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
        Intent intent1 = new Intent(AgregarPubActivity.this, MisPubActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        finish();
    }

    private void confirmarLogout() {

        AlertDialog.Builder alert = new AlertDialog.Builder(AgregarPubActivity.this, R.style.AlertDialogStyle);
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
        sp = getSharedPreferences("emailUserSession", Context.MODE_PRIVATE);;

        ivAgregaPub = findViewById(R.id.ivAgregaPublicacion);
        btnAgregaImagen = findViewById(R.id.btnAgregaImagen);
        etAgregaNombre = findViewById(R.id.etAgregaNombreDesa);
        etAgregaPubb = findViewById(R.id.etAgregaDescripcion);
        etAgregaPub = findViewById(R.id.etAgregaReview);
        spnAgregaZona = findViewById(R.id.spnAgregaZona);
        btnAgregaGuardar = findViewById(R.id.btnAgregaGuardar);
        btnAgregaCancelar = findViewById(R.id.btnAgregaCancelar);

        llenarSpinner();
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
                        arraySpinner.add("Todas...");

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

                        spnAgregaZona.setAdapter(adapterSp);
                    } else {
                        Toast.makeText(AgregarPubActivity.this, "Sin resultados.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AgregarPubActivity.this, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;

    }
}