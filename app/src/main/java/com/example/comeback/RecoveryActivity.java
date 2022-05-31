package com.example.comeback;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecoveryActivity extends AppCompatActivity {

    private TextView tvMensajeRecovery, tvMensajeRecoveryFooter;
    private Button btnRecovery;
    private EditText etCorreoRecovery, etPasswd, etPasswdConf, etCodigoRecovery;
    public OkHttpClient client;
    private String correoUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        tvMensajeRecovery = findViewById(R.id.tvMensajeRecovery);
        tvMensajeRecoveryFooter = findViewById(R.id.tvMensajeRecoveryFooter);
        etCodigoRecovery = findViewById(R.id.etCodigoRecovery);
        etCorreoRecovery = findViewById(R.id.etCorreoRecovery);
        btnRecovery = findViewById(R.id.btnRecovery);
        etPasswdConf = findViewById(R.id.etPasswd);
        etPasswd = findViewById(R.id.etPasswdConf);

        client = new OkHttpClient();


        btnRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Recovery", etCorreoRecovery.getVisibility()+"");
                if((etCorreoRecovery.getVisibility()+"").equals("0")){
                    enviarCodigo(etCorreoRecovery.getText().toString().trim());
                } else if((etCodigoRecovery.getVisibility()+"").equals("0")){
                    verificarCodigo(etCodigoRecovery.getText().toString().trim());
                } else if ((etPasswd.getVisibility()+"").equals("0")){
                    elegirPass(etPasswd.getText().toString().trim(), etPasswdConf.getText().toString().trim());
                }
                /*
                switch (paso) {
                    case 1:
                        recuperar();
                        break;
                    case 2:
                        verificar();
                        break;
                    case 3:
                        cambiarContraseña();
                        break;
                }
                */
            }
        });

    }


    private void enviarCodigo(String correo) {
        if (!correo.equals("") && isEmailValid(correo)) {
            RequestBody formBody = new FormBody.Builder()
                    .add("correo", correo)
                    .build();
            Request request = new Request.Builder()
                    .url("http://"+IP.ip+"/comeback/api/public/usuarios/recovery")
                    .post(formBody)
                    .build();
            try {
                Response responseHTTP = client.newCall(request).execute();
                JSONObject response = new JSONObject(responseHTTP.body().string());

                boolean success = response.getBoolean("success");
                String message = response.getString("message");

                if (success) {

                    etCorreoRecovery.setVisibility(View.GONE);
                    tvMensajeRecoveryFooter.setVisibility(View.GONE);
                    tvMensajeRecovery.setText("A continuación escriba el código de verificación enviado a "+correo);
                    correoUsuario = correo;
                    etCodigoRecovery.setVisibility(View.VISIBLE);
                    btnRecovery.setText("Verificar Código");

                } else {
                    Toast.makeText(RecoveryActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(RecoveryActivity.this, "Debes especificar un correo", Toast.LENGTH_SHORT).show();
        }

    }

    private void verificarCodigo( String codigoVerificacion) {

        if (!codigoVerificacion.equals("")) {

            RequestBody formBody = new FormBody.Builder()
                    .add("codigo", codigoVerificacion)
                    .add("correo", correoUsuario)
                    .build();

            Request request = new Request.Builder()
                    .url("http://"+IP.ip+"/comeback/api/public/usuarios/verificar")
                    .post(formBody)
                    .build();

            try {
                Response responseHTTP = client.newCall(request).execute();
                JSONObject response = new JSONObject(responseHTTP.body().string());;

                boolean success = response.getBoolean("success");
                String message = response.getString("message");

                if (success) {

                    etCodigoRecovery.setVisibility(View.GONE);
                    etPasswd.setVisibility(View.VISIBLE);
                    etPasswdConf.setVisibility(View.VISIBLE);
                    tvMensajeRecovery.setText("A continuación ingrese su nueva contraseña");
                    btnRecovery.setText("Recuperar contraseña");

                } else {
                    Toast.makeText(RecoveryActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    Log.e("Error", response.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(RecoveryActivity.this, "Debes especificar un codigo de SEIS digitos", Toast.LENGTH_LONG).show();
        }

    }

    private void elegirPass(String passwd, String passwdConf) {
        if (!passwd.equals("") && !passwdConf.equals("")) {
            if (passwd.equals(passwdConf)) {
                RequestBody formBody = new FormBody.Builder()
                        .add("password", passwd)
                        .add("correo", correoUsuario)
                        .build();
                Request request = new Request.Builder()
                        .url("http://"+IP.ip+"/comeback/api/public/usuarios/cambiarPasswd")
                        .post(formBody)
                        .build();

                try {
                    Response responseHTTP = client.newCall(request).execute();
                    JSONObject response = new JSONObject(responseHTTP.body().string());;

                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");

                    if (success) {

                        Toast.makeText(RecoveryActivity.this, "Contraseña cambiada", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(RecoveryActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        Log.e("Error", response.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(RecoveryActivity.this, "Las contraseñas deben de coincidir", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(RecoveryActivity.this, "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}