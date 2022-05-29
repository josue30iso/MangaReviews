package com.tripndream.comeback;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tripndream.comeback.utils.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Formulario extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 301;
    private static final int GET_IMAGE_CODE = 202;
    private static final String KEY_GUARDADOS = "key_guardados";

    private EditText etNombrePerro, etDescripcionReporte, etTelefonoReporte, etRecompensa;
    private Spinner spRazaPerro, spTipoReporte, spColonia;
    private CalendarView cvUltimaVista;
    private Button btnCambiarImagen;
    private ImageButton ibGuardarFormulario;

    private ImageView ivPerroAgrega;
    private Bitmap imgPerro;
    private String imgPerroB64 = "";
    private Uri Image = null;

    public OkHttpClient client;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        etNombrePerro = findViewById(R.id.etNombrePerro);
        etDescripcionReporte = findViewById(R.id.etDescripcionReporte);
        etTelefonoReporte = findViewById(R.id.etTelefonoReporte);
        etRecompensa = findViewById(R.id.etRecompensa);

        spRazaPerro = findViewById(R.id.spRazaPerro);
        spTipoReporte = findViewById(R.id.spTipoReporte);
        spColonia = findViewById(R.id.spColonia);

        cvUltimaVista = findViewById(R.id.cvUltimaVista);
        cvUltimaVista.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                view.setDate(c.getTimeInMillis(), true, true);
            }
        });

        ivPerroAgrega = findViewById(R.id.ivPerroAgrega);

        btnCambiarImagen = findViewById(R.id.btnCambiarImagen);
        btnCambiarImagen.setOnClickListener(v -> {
            if(checkPermissionES(Formulario.this)) {
                cambiarImagen();
            }
        });

        ibGuardarFormulario = findViewById(R.id.ibGuardarFormulario);
        ibGuardarFormulario.setOnClickListener(v -> {
            guardarFormulario();
        });

        client = new OkHttpClient();
        sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    }

    private void guardarFormulario() {

        RequestBody formBody = obtenerRequest();
        if (formBody == null) {
            Toast.makeText(Formulario.this, "Debes llenar todos los campos y subir una imagen", Toast.LENGTH_LONG).show();
            return;
        }

        Request request = new Request.Builder()
                .url(WebService.URL_PUB_ADD)
                .post(formBody)
                .build();

        try {
            Response responseHTTP = client.newCall(request).execute();
            JSONObject response = new JSONObject(responseHTTP.body().string());

            boolean success = response.getBoolean("success");
            String mensaje = response.getString("message");

            if (success) {

                Intent intent = new Intent();
                Toast.makeText(Formulario.this, "Se ha publicado tu huellita", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, intent);
                finish();

            } else {
                Log.e("Eror", String.valueOf(response));
                Toast.makeText(Formulario.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    private RequestBody obtenerRequest() {

        RequestBody rb = null;

        String nombrePerro = etNombrePerro.getText().toString().trim();
        String descripcion = etDescripcionReporte.getText().toString().trim();
        String contacto = etTelefonoReporte.getText().toString().trim();
        String recompensa = etRecompensa.getText().toString().trim();

        String raza = String.valueOf(spRazaPerro.getSelectedItemPosition());
        String estatusTipoReporte = String.valueOf(spTipoReporte.getSelectedItemPosition());
        String colonia = String.valueOf(spColonia.getSelectedItemPosition());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ultimaVista = sdf.format(new Date(cvUltimaVista.getDate()));

        if (!nombrePerro.equals("") && !descripcion.equals("") && !contacto.equals("") && !raza.equals("0") && !estatusTipoReporte.equals("0") && !colonia.equals("0") && !ultimaVista.equals("") && !imgPerroB64.equals("")) {

            rb = new FormBody.Builder()
                    .add("foto", imgPerroB64)
                    .add("id_usuario", sp.getString("id", "-1"))
                    .add("titulo", nombrePerro)
                    .add("raza", raza)
                    .add("estatus", estatusTipoReporte)
                    .add("id_colonia", colonia)
                    .add("descripcion", descripcion)
                    .add("numeroContacto", contacto)
                    .add("recompensa", recompensa.equals("") ? "0.00" : recompensa)
                    .add("ultimaVista", ultimaVista)
                    .build();

        }

        return rb;

    }

    private void cambiarImagen() {

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, GET_IMAGE_CODE);

    }

    private boolean checkPermissionES(
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

    private void showDialog(final String msg, final Context context, final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(Html.fromHtml("<font color='#E77FB3'>Permiso necesario</font>"));
        alertBuilder.setMessage(Html.fromHtml("<font color='#E77FB3'>"+ msg +" es necesario</font>"));
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialog, which) -> ActivityCompat.requestPermissions((Activity) context,
                        new String[] { permission },
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GET_IMAGE_CODE) {

            if (data != null) {

                imgPerro = null;
                InputStream inputStream;

                try {

                    Image = data.getData();

                    if (imgPerro != null) {
                        imgPerro.recycle();
                    }

                    inputStream = getContentResolver().openInputStream(data.getData());
                    imgPerro = BitmapFactory.decodeStream(inputStream);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imgPerro.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imgPerroB64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    inputStream.close();
                    ivPerroAgrega.setImageBitmap(imgPerro);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }
}