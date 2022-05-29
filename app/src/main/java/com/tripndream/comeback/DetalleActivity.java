package com.tripndream.comeback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    private Reporte reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        intent = getIntent();
        reporte = new Reporte();

        sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        tvTipoReporte = findViewById(R.id.tvTipoReporte);

        reporte.setId(intent.getIntExtra("KEY_ID", -1));

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
        reporte.setId(intent.getIntExtra("KEY_ID_COLONIA", -1));
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

        if(reporte.getEstatus() == 1 || reporte.getEstatus() == 2 || reporte.getEstatus() == 3){
            if(sp.getString("id", "-1").equals(intent.getStringExtra("KEY_USUARIO"))){
                btnEditar.setVisibility(View.VISIBLE);
                btnEliminar.setVisibility(View.VISIBLE);
                btnEncontrado.setVisibility(View.VISIBLE);

                btnEditar.setOnClickListener(v -> {
                    editar();
                });
            }
        }

        switch (reporte.getEstatus()){
            case 1:
                tvTipoReporte.setText("Perdido");
                tvDetalleNombre.setVisibility(View.VISIBLE);
                tvRecompensa.setVisibility(View.VISIBLE);
                tvLabelRecompensa.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvTipoReporte.setText("Avistado");
                tvDetalleNombre.setVisibility(View.GONE);
                tvRecompensa.setVisibility(View.GONE);
                tvLabelRecompensa.setVisibility(View.GONE);
                break;
            case 3:
                tvTipoReporte.setText("Refugiado");
                tvDetalleNombre.setVisibility(View.GONE);
                tvRecompensa.setVisibility(View.GONE);
                tvLabelRecompensa.setVisibility(View.GONE);
                break;
            case 4:
                tvTipoReporte.setText("Encontrado");
                tvDetalleNombre.setVisibility(View.VISIBLE);
                tvRecompensa.setVisibility(View.GONE);
                tvLabelRecompensa.setVisibility(View.GONE);
                break;
        }
    }

    private void editar() {
        intent = new Intent(this, Formulario.class);
        intent.putExtra("modo_edicion", true);
        intent.putExtra("reporte", reporte);
        startActivityForResult(intent, KEY_EDITAR_REPORTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == KEY_EDITAR_REPORTE && data != null){

            reporte = (Reporte) data.getSerializableExtra("reporte");

            switch (reporte.getEstatus()){
                case 1:
                    tvTipoReporte.setText("Perdido");
                    break;
                case 2:
                    tvTipoReporte.setText("En su hogar");
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