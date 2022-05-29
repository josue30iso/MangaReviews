package com.tripndream.comeback;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetalleActivity extends AppCompatActivity {

    private static final String KEY_DETALLE_IMAGEN = "KEY_DETALLE_IMAGEN";
    private static final String KEY_DETALLE_NOMBRE = "KEY_DETALLE_NOMBRE";
    private static final String KEY_DETALLE_RAZA = "KEY_DETALLE_RAZA";
    private static final String KEY_DETALLE_COLONIA = "KEY_DETALLE_COLONIA";
    private static final String KEY_DETALLE_FECHA = "KEY_DETALLE_FECHA";
    private static final String KEY_DETALLE_NUMERO = "KEY_DETALLE_NUMERO";
    private static final String KEY_DETALLE_DESCRIPCION = "KEY_DETALLE_DESCRIPCION";

    private ImageView ivDetalleFoto;
    private TextView tvDetalleNombre, tvDetalleRaza, tvDetalleFecha,
            tvDetallePerdidoEn, tvDetalleTelefono, tvDetalleDescripcion,
            tvTipoReporte;
    private Button btnEditar, btnEliminar, btnEncontrado;

    private SharedPreferences sp;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        Log.i("idUsuario", sp.getString("id", "-1"));
        Log.i("Usuario Reporte", intent.getStringExtra("KEY_USUARIO"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        tvTipoReporte = findViewById(R.id.tvTipoReporte);
        Log.i("Estatus", intent.getStringExtra("KEY_ESTATUS"));
        switch (intent.getStringExtra("KEY_ESTATUS")){
            case "1":
                tvTipoReporte.setText("Perdido");
                break;
            case "2":
                tvTipoReporte.setText("En su hogar");
                break;
            case "3":
                tvTipoReporte.setText("Nuevo hogar");
                break;
        }

        ivDetalleFoto = findViewById(R.id.ivDetalleFoto);
        String fotoPerrito = intent.getStringExtra("KEY_IMAGEN");
        byte[] perritoBytes = Base64.decode(fotoPerrito, Base64.DEFAULT);
        Bitmap imagenPerrito = BitmapFactory.decodeByteArray(perritoBytes, 0, perritoBytes.length);
        ivDetalleFoto.setImageBitmap(imagenPerrito);

        tvDetalleNombre = findViewById(R.id.tvDetalleNombre);
        tvDetalleNombre.setText(intent.getStringExtra("KEY_NOMBRE"));

        tvDetalleRaza = findViewById(R.id.tvDetalleRaza);
        tvDetalleRaza.setText(intent.getStringExtra("KEY_RAZA"));

        tvDetalleFecha = findViewById(R.id.tvDetalleFecha);
        String fecha = intent.getStringExtra("KEY_NOMBRE").split(" ")[0];
        tvDetalleFecha.setText(fecha);

        tvDetallePerdidoEn = findViewById(R.id.tvDetallePerdidoEn);
        tvDetallePerdidoEn.setText("Visto última vez en: "+intent.getStringExtra("KEY_COLONIA"));

        tvDetalleTelefono = findViewById(R.id.tvDetalleTelefono);
        tvDetalleTelefono.setText("Contacto: "+intent.getStringExtra("KEY_NUMERO"));

        tvDetalleDescripcion = findViewById(R.id.tvDetalleDescripcion);
        tvDetalleDescripcion.setText(intent.getStringExtra("KEY_DESCRIPCION"));

        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnEncontrado = findViewById(R.id.btnEncontrado);

        if(sp.getString("id", "-1").equals(intent.getStringExtra("KEY_USUARIO"))){
            Log.i("Duenho", "Yes");

            btnEditar.setVisibility(View.VISIBLE);
            btnEliminar.setVisibility(View.VISIBLE);
            btnEncontrado.setVisibility(View.VISIBLE);
        } else{
            Log.i("Duenho", "No");
        }
    }
}