package com.example.comeback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetalleActivity extends AppCompatActivity {

    private static final String KEY_DETALLE_IMAGEN = "KEY_DETALLE_IMAGEN";
    private static final String KEY_DETALLE_NOMBRE = "KEY_DETALLE_NOMBRE";
    private static final String KEY_DETALLE_RAZA = "KEY_DETALLE_RAZA";
    private static final String KEY_DETALLE_COLONIA = "KEY_DETALLE_COLONIA";
    private static final String KEY_DETALLE_FECHA = "KEY_DETALLE_FECHA";
    private static final String KEY_DETALLE_NUMERO = "KEY_DETALLE_NUMERO";
    private static final String KEY_DETALLE_DESCRIPCION = "KEY_DETALLE_DESCRIPCION";
    private static final int KEY_EDITAR_REPORTE = 667;

    private ImageView ivDetalleFoto;
    private TextView tvDetalleNombre, tvDetalleRaza, tvDetalleFecha,
            tvDetallePerdidoEn, tvDetalleTelefono, tvDetalleDescripcion,
            tvTipoReporte, tvRecompensa, tvLabelRecompensa;
    private Button btnEditar, btnEliminar, btnEncontrado;

    private SharedPreferences sp;
    private Intent intent;

    public OkHttpClient client;

    private Reporte reporte;
    private int idReporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        intent = getIntent();
        reporte = new Reporte();

        sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        tvTipoReporte = findViewById(R.id.tvTipoReporte);

        reporte.setId(intent.getIntExtra("KEY_ID", -1));
        idReporte = reporte.getId();
        Log.i("ReporteID", String.valueOf(reporte.getId()));

        reporte.setEstatus(intent.getIntExtra("KEY_ESTATUS", -1));


        ivDetalleFoto = findViewById(R.id.ivDetalleFoto);
        String fotoPerrito = intent.getStringExtra("KEY_IMAGEN");
        reporte.setImagen(fotoPerrito);
        byte[] perritoBytes = Base64.decode(fotoPerrito, Base64.DEFAULT);
        Bitmap imagenPerrito = BitmapFactory.decodeByteArray(perritoBytes, 0, perritoBytes.length);
        ivDetalleFoto.setImageBitmap(imagenPerrito);

        tvDetalleNombre = findViewById(R.id.tvDetalleNombre);
        reporte.setNombre(intent.getStringExtra("KEY_NOMBRE"));
        tvDetalleNombre.setText(reporte.getNombre());

        tvDetalleRaza = findViewById(R.id.tvDetalleRaza);
        reporte.setSpRaza(intent.getIntExtra("KEY_ID_RAZA", -1));
        reporte.setRaza(intent.getStringExtra("KEY_RAZA"));
        tvDetalleRaza.setText(reporte.getRaza());

        tvDetalleFecha = findViewById(R.id.tvDetalleFecha);
        String fecha = intent.getStringExtra("KEY_FECHA").split(" ")[0];
        reporte.setFecha(fecha);
        tvDetalleFecha.setText(fecha);

        tvDetallePerdidoEn = findViewById(R.id.tvDetallePerdidoEn);
        reporte.setIdColonia(intent.getIntExtra("KEY_ID_COLONIA", -1));
        reporte.setColonia(intent.getStringExtra("KEY_COLONIA"));
        tvDetallePerdidoEn.setText("Colonia " + reporte.getColonia());

        tvDetalleTelefono = findViewById(R.id.tvDetalleTelefono);
        reporte.setCelular(intent.getStringExtra("KEY_NUMERO"));
        tvDetalleTelefono.setText(reporte.getCelular());

        tvDetalleDescripcion = findViewById(R.id.tvDetalleDescripcion);
        reporte.setDescripcion(intent.getStringExtra("KEY_DESCRIPCION"));
        tvDetalleDescripcion.setText(reporte.getDescripcion());

        tvLabelRecompensa = findViewById(R.id.tvLabelRecompensa);

        tvRecompensa = findViewById(R.id.tvDetalleRecompensa);
        reporte.setRecompensa(intent.getDoubleExtra("KEY_RECOMPENSA", 0));
        tvRecompensa.setText("$" + String.valueOf(reporte.getRecompensa()));

        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnEncontrado = findViewById(R.id.btnEncontrado);

        switch (reporte.getEstatus()){
            case 1:
                tvTipoReporte.setText("Perdido");
                tvDetalleNombre.setVisibility(View.VISIBLE);
                tvRecompensa.setVisibility(View.VISIBLE);
                tvLabelRecompensa.setVisibility(View.VISIBLE);
                if(sp.getString("id", "-1").equals(intent.getStringExtra("KEY_USUARIO"))){
                    btnEditar.setVisibility(View.VISIBLE);
                    btnEliminar.setVisibility(View.VISIBLE);
                    btnEncontrado.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                tvTipoReporte.setText("Avistado");
                tvDetalleNombre.setVisibility(View.GONE);
                tvRecompensa.setVisibility(View.GONE);
                tvLabelRecompensa.setVisibility(View.GONE);
                if(sp.getString("id", "-1").equals(intent.getStringExtra("KEY_USUARIO"))){
                    btnEditar.setVisibility(View.VISIBLE);
                    btnEliminar.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                tvTipoReporte.setText("Refugiado");
                tvDetalleNombre.setVisibility(View.GONE);
                tvRecompensa.setVisibility(View.GONE);
                tvLabelRecompensa.setVisibility(View.GONE);
                if(sp.getString("id", "-1").equals(intent.getStringExtra("KEY_USUARIO"))){
                    btnEditar.setVisibility(View.VISIBLE);
                    btnEliminar.setVisibility(View.VISIBLE);

                }
                break;
            case 4:
                tvTipoReporte.setText("Encontrado");
                tvDetalleNombre.setVisibility(View.VISIBLE);
                tvRecompensa.setVisibility(View.GONE);
                tvLabelRecompensa.setVisibility(View.GONE);
                break;
        }

        btnEditar.setOnClickListener(v -> {
            editar();
        });
        btnEliminar.setOnClickListener(v -> {
            eliminar();
        });
        btnEncontrado.setOnClickListener(v -> {
            encontrado();
        });
    }

    private void encontrado(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();


        dialogBuilder.setView(inflater.inflate(R.layout.alert_perrito_encontrado, null));
        AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();

        EditText etEmailAyudador = (EditText) alertDialog.findViewById(R.id.etEmailAyudador);
        Button btnAceptarEncontrado = (Button) alertDialog.findViewById(R.id.btnAceptarEncontrado);
        Button btnOmitirEncontrado = (Button) alertDialog.findViewById(R.id.btnOmitirEncontrado);

        btnOmitirEncontrado.setOnClickListener( view -> perritoEncontrado());

        btnAceptarEncontrado.setOnClickListener(v -> {
            if (!etEmailAyudador.getText().equals("")){
                client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("correo", etEmailAyudador.getText().toString())
                        .build();

                Request request = new Request.Builder()
                        .url("http://"+IP.ip+"/comeback/api/public/usuarios/plusOne")
                        .post(formBody)
                        .build();

                try {
                    Response responseHTTP = client.newCall(request).execute();
                    JSONObject response = new JSONObject(responseHTTP.body().string());;

                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");

                    if (success) {
                        Log.i("Recompensa enviada", "Yes "+etEmailAyudador.getText().toString());
                        perritoEncontrado();

                    } else {
                        DetalleActivity.this.runOnUiThread(() -> Toast.makeText(DetalleActivity.this, message, Toast.LENGTH_SHORT).show());
                        Log.i("Recompensa enviada", "No "+etEmailAyudador.getText().toString());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(DetalleActivity.this, "Ingrese un correo u omita", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void editar() {
        intent = new Intent(this, Formulario.class);
        reporte.setId(idReporte);
        intent.putExtra("modo_edicion", true);
        intent.putExtra("reporte", reporte);
        startActivityForResult(intent, KEY_EDITAR_REPORTE);
    }

    private void eliminar() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DetalleActivity.this, R.style.AlertDialogStyle);
        alert.setTitle(Html.fromHtml("Eliminar reporte"))
                .setMessage(Html.fromHtml("¿Realmente deseas eliminar este reporte?"))
                .setPositiveButton("Aceptar", (dialog, id) -> {
                    client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("id", String.valueOf(idReporte))
                            .build();

                    Request request = new Request.Builder()
                            .url("http://"+IP.ip+"/comeback/api/public/publicaciones/deletePublicacion")
                            .post(formBody)
                            .build();

                    try {
                        Response responseHTTP = client.newCall(request).execute();
                        JSONObject response = new JSONObject(responseHTTP.body().string());;

                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");

                        if (success) {
                            DetalleActivity.this.runOnUiThread(() -> Toast.makeText(DetalleActivity.this, "Reporte Eliminado", Toast.LENGTH_SHORT).show());
                            Log.i("Reporte eliminado", "Yes "+String.valueOf(reporte.getId()));
                            Intent intent = new Intent();
                            intent.putExtra("ELIMINADO", "eliminado");
                            setResult(RESULT_OK, intent);
                            finish();

                        } else {
                            DetalleActivity.this.runOnUiThread(() -> Toast.makeText(DetalleActivity.this, message, Toast.LENGTH_SHORT).show());
                            Log.i("Reporte eliminado", "No "+String.valueOf(reporte.getId()));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss()).show();
    }

    private void perritoEncontrado(){
        client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("id", String.valueOf(reporte.getId()))
                .build();

        Request request = new Request.Builder()
                .url("http://"+IP.ip+"/comeback/api/public/publicaciones/perroEncontrado")
                .post(formBody)
                .build();

        try {
            Response responseHTTP = client.newCall(request).execute();
            JSONObject response = new JSONObject(responseHTTP.body().string());;

            boolean success = response.getBoolean("success");
            String message = response.getString("message");

            if (success) {
                DetalleActivity.this.runOnUiThread(() -> Toast.makeText(DetalleActivity.this, "Reporte actualizado", Toast.LENGTH_SHORT).show());
                Log.i("Reporte actualizado", "Yes "+String.valueOf(reporte.getId()));
                Intent intent = new Intent();
                intent.putExtra("ACTUALIZADO", "actualizado");
                setResult(RESULT_OK, intent);
                finish();

            } else {
                DetalleActivity.this.runOnUiThread(() -> Toast.makeText(DetalleActivity.this, message, Toast.LENGTH_SHORT).show());
                Log.i("Reporte actualizado", "No "+String.valueOf(reporte.getId()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == KEY_EDITAR_REPORTE && data != null){

            this.reporte = (Reporte) data.getSerializableExtra("reporte");

            switch (reporte.getEstatus()){
                case 1:
                    tvTipoReporte.setText("Perdido");
                    break;
                case 2:
                    tvTipoReporte.setText("Avistado");
                    break;
                case 3:
                    tvTipoReporte.setText("Nuevo hogar");
                    break;
                case 4:
                    tvTipoReporte.setText("Encontrado");
                    break;
            }

            String fotoPerrito = reporte.getImagen();
            byte[] perritoBytes = Base64.decode(fotoPerrito, Base64.DEFAULT);
            Bitmap imagenPerrito = BitmapFactory.decodeByteArray(perritoBytes, 0, perritoBytes.length);
            ivDetalleFoto.setImageBitmap(imagenPerrito);

            tvDetalleNombre.setText(reporte.getNombre());

            tvDetalleRaza.setText(reporte.getRaza());

            tvDetalleFecha = findViewById(R.id.tvDetalleFecha);
            String fecha = reporte.getFecha().split(" ")[0];
            tvDetalleFecha.setText("Fecha de desaparecido: " + fecha);

            tvDetallePerdidoEn.setText("Colonia " + reporte.getColonia());

            tvDetalleTelefono.setText("Contacto: " + reporte.getCelular());

            tvDetalleDescripcion.setText(reporte.getDescripcion());

            tvRecompensa.setText("$" + String.valueOf(reporte.getRecompensa()));

        }
    }
}