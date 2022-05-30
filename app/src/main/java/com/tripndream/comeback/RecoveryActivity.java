package com.tripndream.comeback;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tripndream.comeback.utils.WebService;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecoveryActivity extends AppCompatActivity {

    private TextView tvInstruccion, tvE;
    private Button btnRecovery;
    private EditText etCorreoCodPass;
    private EditText etPasswd;
    private EditText etPasswdConf;
    private LinearLayout llPasswords;
    public OkHttpClient client;

    private int paso = 1;
    private String correoUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        configMaterials();

    }

    private void configMaterials() {

        etCorreoCodPass = findViewById(R.id.etCorreoRecovery);
        btnRecovery = findViewById(R.id.btnActionRecovery);
        etPasswdConf = findViewById(R.id.etPasswd);
        etPasswd = findViewById(R.id.etPasswdConf);
        llPasswords = findViewById(R.id.llPasswords);

        tvInstruccion = findViewById(R.id.tvIntruccion);
        tvE = findViewById(R.id.tvE);
        client = new OkHttpClient();

        btnRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

    }

    private void cambiarContraseña() {

        String passwd = etPasswd.getText().toString().trim();
        String passwdConf = etPasswdConf.getText().toString().trim();

        if (!passwd.equals("") && !passwdConf.equals("")) {

            if (passwd.equals(passwdConf)) {

                RequestBody formBody = new FormBody.Builder()
                        .add("password", passwd)
                        .add("correo", correoUsuario)
                        .build();

                Request request = new Request.Builder()
                        .url(WebService.URL_CAMBIARPASS)
                        .post(formBody)
                        .build();

                try {
                    Response responseHTTP = client.newCall(request).execute();
                    JSONObject response = new JSONObject(responseHTTP.body().string());;

                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");

                    if (success) {

                        Toast.makeText(RecoveryActivity.this, "Contraseña cambiada con exito", Toast.LENGTH_LONG).show();
                        updateUI();

                    } else {
                        Toast.makeText(RecoveryActivity.this, "Ha ocurrido un error: " + message, Toast.LENGTH_LONG).show();
                        Log.e("Error", response.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(RecoveryActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(RecoveryActivity.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void verificar() {

        String codigo = etCorreoCodPass.getText().toString().trim();

        if (!codigo.equals("") && codigo.length() == 6) {

            RequestBody formBody = new FormBody.Builder()
                    .add("codigo", codigo)
                    .add("correo", correoUsuario)
                    .build();

            Request request = new Request.Builder()
                    .url(WebService.URL_VERIFICAR)
                    .post(formBody)
                    .build();

            try {
                Response responseHTTP = client.newCall(request).execute();
                JSONObject response = new JSONObject(responseHTTP.body().string());;

                boolean success = response.getBoolean("success");
                String message = response.getString("message");

                if (success) {

                    paso = 3;
                    tvInstruccion.setText("Ingrese su nueva contraseña");
                    btnRecovery.setText("Cambiar contraseña");
                    etCorreoCodPass.setVisibility(View.GONE);
                    tvE.setVisibility(View.GONE);
                    llPasswords.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(RecoveryActivity.this, "Ha ocurrido un error: " + message, Toast.LENGTH_LONG).show();
                    Log.e("Error", response.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(RecoveryActivity.this, "Debes especificar un codigo de SEIS digitos", Toast.LENGTH_LONG).show();
        }

    }

    private void recuperar() {

        String correo = etCorreoCodPass.getText().toString().trim();

        if (!correo.equals("") && isEmailValid(correo)) {

            RequestBody formBody = new FormBody.Builder()
                    .add("correo", correo)
                    .build();

            Request request = new Request.Builder()
                    .url(WebService.URL_RECOVERY)
                    .post(formBody)
                    .build();

            try {
                Response responseHTTP = client.newCall(request).execute();
                JSONObject response = new JSONObject(responseHTTP.body().string());;

                boolean success = response.getBoolean("success");
                String message = response.getString("message");

                if (success) {

                    correoUsuario = correo;
                    paso = 2;
                    tvInstruccion.setText("Te hemos enviado un codigo de verificacion. Ponerlo en el siguiente recuadro para recuperar su cuenta");
                    btnRecovery.setText("Verificar");
                    etCorreoCodPass.setText("");
                    tvE.setText("Codigo verificacion");

                } else {
                    Toast.makeText(RecoveryActivity.this, "Ha ocurrido un error: " + message, Toast.LENGTH_LONG).show();
                    Log.e("Error", response.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(RecoveryActivity.this, "Debes especificar un correo", Toast.LENGTH_LONG).show();
        }

    }

    private void updateUI() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}