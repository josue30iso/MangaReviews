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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

    private TextView tvFormularioNombre, tvFormularioRaza, tvFormularioColonia,
            tvFormularioDescripcion, tvFormularioFecha,
            tvFormularioNumero, tvFormularioRecompensa;
    private EditText etNombrePerro, etDescripcionReporte, etTelefonoReporte, etRecompensa;
    private Spinner spRazaPerro, spTipoReporte, spColonia;
    private CalendarView cvUltimaVista;
    private Button btnCambiarImagen;
    private ImageButton ibGuardarFormulario;
    private int tipoReporte;

    private ImageView ivPerroAgrega;
    private Bitmap imgPerro;
    private String imgPerroB64 = "";
    private Uri Image = null;

    public OkHttpClient client;
    private SharedPreferences sp;
    private Calendar currentTime;

    private Boolean modoEdicion;
    private Reporte reporte;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        tvFormularioNombre = findViewById(R.id.tvFormularioNombre);
        tvFormularioRaza = findViewById(R.id.tvFormularioRaza);
        tvFormularioColonia = findViewById(R.id.tvFormularioColonia);
        tvFormularioDescripcion = findViewById(R.id.tvFormularioDescripcion);
        tvFormularioFecha = findViewById(R.id.tvFormularioFecha);
        tvFormularioNumero = findViewById(R.id.tvFormularioNumero);
        tvFormularioRecompensa = findViewById(R.id.tvFormularioRecompensa);
        etNombrePerro = findViewById(R.id.etNombrePerro);
        etDescripcionReporte = findViewById(R.id.etDescripcionReporte);
        etTelefonoReporte = findViewById(R.id.etTelefonoReporte);
        etRecompensa = findViewById(R.id.etRecompensa);

        spRazaPerro = findViewById(R.id.spRazaPerro);

        spTipoReporte = findViewById(R.id.spTipoReporte);
        spTipoReporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("onItemSelected: ", String.valueOf(i));
                switch (String.valueOf(i)){
                    case "0":
                        tipoReporte = 0;
                        ivPerroAgrega.setVisibility(View.GONE);
                        btnCambiarImagen.setVisibility(View.GONE);
                        tvFormularioNombre.setVisibility(View.GONE);
                        etNombrePerro.setVisibility(View.GONE);
                        tvFormularioRaza.setVisibility(View.GONE);
                        spRazaPerro.setVisibility(View.GONE);
                        tvFormularioColonia.setVisibility(View.GONE);
                        spColonia.setVisibility(View.GONE);
                        tvFormularioDescripcion.setVisibility(View.GONE);
                        etDescripcionReporte.setVisibility(View.GONE);
                        tvFormularioFecha.setVisibility(View.GONE);
                        cvUltimaVista.setVisibility(View.GONE);
                        tvFormularioNumero.setVisibility(View.GONE);
                        etTelefonoReporte.setVisibility(View.GONE);
                        tvFormularioRecompensa.setVisibility(View.GONE);
                        etRecompensa.setVisibility(View.GONE);
                        ibGuardarFormulario.setVisibility(View.GONE);
                        break;
                    case "1":
                        tipoReporte = 1;
                        ivPerroAgrega.setVisibility(View.VISIBLE);
                        btnCambiarImagen.setVisibility(View.VISIBLE);
                        tvFormularioNombre.setVisibility(View.VISIBLE);
                        etNombrePerro.setVisibility(View.VISIBLE);
                        tvFormularioRaza.setVisibility(View.VISIBLE);
                        spRazaPerro.setVisibility(View.VISIBLE);
                        tvFormularioColonia.setText("Última vez visto en:");
                        tvFormularioColonia.setVisibility(View.VISIBLE);
                        spColonia.setVisibility(View.VISIBLE);
                        tvFormularioDescripcion.setVisibility(View.VISIBLE);
                        etDescripcionReporte.setVisibility(View.VISIBLE);
                        tvFormularioFecha.setText("Última vez visto el :");
                        tvFormularioFecha.setVisibility(View.VISIBLE);
                        cvUltimaVista.setVisibility(View.VISIBLE);
                        tvFormularioNumero.setVisibility(View.VISIBLE);
                        etTelefonoReporte.setVisibility(View.VISIBLE);
                        tvFormularioRecompensa.setVisibility(View.VISIBLE);
                        etRecompensa.setVisibility(View.VISIBLE);
                        ibGuardarFormulario.setVisibility(View.VISIBLE);
                        break;
                    case "2":
                        tipoReporte = 2;
                        ivPerroAgrega.setVisibility(View.VISIBLE);
                        btnCambiarImagen.setVisibility(View.VISIBLE);
                        tvFormularioNombre.setVisibility(View.GONE);
                        etNombrePerro.setVisibility(View.GONE);
                        tvFormularioRaza.setVisibility(View.VISIBLE);
                        spRazaPerro.setVisibility(View.VISIBLE);
                        tvFormularioColonia.setText("Visto en:");
                        tvFormularioColonia.setVisibility(View.VISIBLE);
                        spColonia.setVisibility(View.VISIBLE);
                        tvFormularioDescripcion.setVisibility(View.VISIBLE);
                        etDescripcionReporte.setVisibility(View.VISIBLE);
                        tvFormularioFecha.setText("Visto el :");
                        tvFormularioFecha.setVisibility(View.VISIBLE);
                        cvUltimaVista.setVisibility(View.VISIBLE);
                        tvFormularioNumero.setVisibility(View.VISIBLE);
                        etTelefonoReporte.setVisibility(View.VISIBLE);
                        tvFormularioRecompensa.setVisibility(View.GONE);
                        etRecompensa.setVisibility(View.GONE);
                        ibGuardarFormulario.setVisibility(View.VISIBLE);
                        break;
                    case "3":
                        tipoReporte = 3;
                        ivPerroAgrega.setVisibility(View.VISIBLE);
                        btnCambiarImagen.setVisibility(View.VISIBLE);
                        tvFormularioNombre.setVisibility(View.GONE);
                        etNombrePerro.setVisibility(View.GONE);
                        tvFormularioRaza.setVisibility(View.VISIBLE);
                        spRazaPerro.setVisibility(View.VISIBLE);
                        tvFormularioColonia.setText("Rescatado en:");
                        tvFormularioColonia.setVisibility(View.VISIBLE);
                        spColonia.setVisibility(View.VISIBLE);
                        tvFormularioDescripcion.setVisibility(View.VISIBLE);
                        etDescripcionReporte.setVisibility(View.VISIBLE);
                        tvFormularioFecha.setText("Fecha de rescate :");
                        tvFormularioFecha.setVisibility(View.VISIBLE);
                        cvUltimaVista.setVisibility(View.VISIBLE);
                        tvFormularioNumero.setVisibility(View.VISIBLE);
                        etTelefonoReporte.setVisibility(View.VISIBLE);
                        tvFormularioRecompensa.setVisibility(View.GONE);
                        etRecompensa.setVisibility(View.GONE);
                        ibGuardarFormulario.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spColonia = findViewById(R.id.spColonia);

        cvUltimaVista = findViewById(R.id.cvUltimaVista);
        cvUltimaVista.setMaxDate(new Date().getTime());
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

        intent = getIntent();
        if (intent != null) {
            modoEdicion = intent.getBooleanExtra("modo_edicion", false);
            if (modoEdicion) {
                reporte = (Reporte) intent.getSerializableExtra("reporte");
                if (reporte != null) {
                    setearDatosFormulario();
                }
            }
        }
    }

    private void guardarFormulario() {

        RequestBody formBody = modoEdicion ? obtenerRequestEdit() : obtenerRequestAgregar();
        if (formBody == null) {
            Toast.makeText(Formulario.this, "Debes llenar todos los campos y subir una imagen", Toast.LENGTH_LONG).show();
            return;
        }


        Log.i("MODO EDICION", ""+modoEdicion);
        String webService = modoEdicion ? WebService.URL_PUB_EDIT : WebService.URL_PUB_ADD;

        Request request = new Request.Builder()
                .url(webService)
                .post(formBody)
                .build();

        try {
            Response responseHTTP = client.newCall(request).execute();
            JSONObject response = new JSONObject(responseHTTP.body().string());

            boolean success = response.getBoolean("success");
            String mensaje = response.getString("message");

            if (success) {

                Intent intent = new Intent();
                Toast.makeText(Formulario.this, modoEdicion ? "Se ha editado tu huellita" : "Se ha publicado tu huellita", Toast.LENGTH_SHORT).show();
                if (modoEdicion) intent.putExtra("reporte", reporte);
                setResult(RESULT_OK, intent);
                finish();

            } else {
                Log.e("Error", String.valueOf(response));
                Toast.makeText(Formulario.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    private RequestBody obtenerRequestAgregar() {

        RequestBody rb = null;

        String nombrePerro = etNombrePerro.getText().toString().trim();
        String descripcion = etDescripcionReporte.getText().toString().trim();
        String contacto = etTelefonoReporte.getText().toString().trim();
        String recompensa = etRecompensa.getText().toString().trim();

        String raza = String.valueOf(spRazaPerro.getSelectedItemPosition());
        String estatusTipoReporte = String.valueOf(spTipoReporte.getSelectedItemPosition());
        String colonia = String.valueOf(spColonia.getSelectedItemPosition());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    private RequestBody obtenerRequestEdit() {

        RequestBody rb = null;

        String nombrePerro = etNombrePerro.getText().toString().trim();
        String descripcion = etDescripcionReporte.getText().toString().trim();
        String contacto = etTelefonoReporte.getText().toString().trim();
        String recompensa = etRecompensa.getText().toString().trim();

        String raza = String.valueOf(spRazaPerro.getSelectedItemPosition());
        String estatusTipoReporte = String.valueOf(spTipoReporte.getSelectedItemPosition());
        String colonia = String.valueOf(spColonia.getSelectedItemPosition());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ultimaVista = sdf.format(new Date(cvUltimaVista.getDate()));

        Log.i("image", imgPerroB64.substring(0,10));
        Log.i("Nombre", nombrePerro);
        Log.i("descripcion", descripcion);
        Log.i("contacto", contacto);
        Log.i("recompensa", recompensa);
        Log.i("raza", raza);
        Log.i("estatusTipoReporte", estatusTipoReporte);
        Log.i("colonia", raza);
        Log.i("ultimaVista", ultimaVista);
        Log.i("id", ""+reporte.getId());

        if (!nombrePerro.equals("") && !descripcion.equals("") && !contacto.equals("") && !raza.equals("0") && !estatusTipoReporte.equals("0") && !colonia.equals("0") && !ultimaVista.equals("") && !imgPerroB64.equals("")) {

            rb = new FormBody.Builder()
                    .add("foto", imgPerroB64)
                    .add("id", String.valueOf(reporte.getId()))
                    .add("titulo", nombrePerro)
                    .add("raza", raza)
                    .add("estatus", estatusTipoReporte)
                    .add("id_colonia", colonia)
                    .add("descripcion", descripcion)
                    .add("numeroContacto", contacto)
                    .add("recompensa", recompensa.equals("") ? "0.00" : recompensa)
                    .add("ultimaVista", ultimaVista)
                    .build();

            reporte = new Reporte();
            reporte.setImagen(imgPerroB64);
            reporte.setId(reporte.getId());
            reporte.setCelular(contacto);
            reporte.setColonia(colonia);
            reporte.setSpRaza(spRazaPerro.getSelectedItemPosition());
            reporte.setRaza((String) spRazaPerro.getSelectedItem());
            reporte.setFecha(ultimaVista);
            reporte.setNombre(nombrePerro);
            reporte.setEstatus(spTipoReporte.getSelectedItemPosition());
            reporte.setDescripcion(descripcion);
            reporte.setIdColonia(spColonia.getSelectedItemPosition());
            reporte.setRecompensa(Double.valueOf(recompensa.equals("") ? "0.00" : recompensa));
            reporte.setUsuario("");

        }

        return rb;

    }

    private void setearDatosFormulario() {

        try {
            etNombrePerro.setText(reporte.getNombre());
            etDescripcionReporte.setText(reporte.getDescripcion());
            etTelefonoReporte.setText(reporte.getCelular());
            etRecompensa.setText(String.valueOf(reporte.getRecompensa()));
            spRazaPerro.setSelection(reporte.getSpRaza());
            spTipoReporte.setSelection(reporte.getEstatus());
            spColonia.setSelection(reporte.getIdColonia());

            String selectedDate = reporte.getFecha().split(" ")[0];
            cvUltimaVista.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate).getTime(), true, true);

            imgPerroB64 = reporte.getImagen();
            byte[] imageBytes = Base64.decode(imgPerroB64, Base64.DEFAULT);
            imgPerro = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivPerroAgrega.setImageBitmap(imgPerro);
        } catch (Exception e) {
            e.printStackTrace();
        }

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