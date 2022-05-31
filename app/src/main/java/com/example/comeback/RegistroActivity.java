package com.example.comeback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etCelular, etPasswd, etConfirmarPasswd;
    private Button btnAccionRegistro;
    private String nombre, correo, celular, passwd, passwdConf;
    public OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        configMaterials();

    }

    private void updateUI() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void configMaterials() {

        etNombre = findViewById(R.id.etNombreRegistro);
        etCorreo = findViewById(R.id.etCorreoRegistro);
        etCelular = findViewById(R.id.etCelularRegistro);
        etPasswd = findViewById(R.id.etPasswdRegistro);
        etConfirmarPasswd = findViewById(R.id.etConfirmarPasswdRegistro);
        client = new OkHttpClient();

        btnAccionRegistro = findViewById(R.id.btnAccionRegistrarse);

        configClickListener();

    }

    private void configClickListener() {

        btnAccionRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registro();
            }
        });

    }

    private void registro() {

        nombre = etNombre.getText().toString().trim();
        correo = etCorreo.getText().toString().trim();
        celular = etCelular.getText().toString().trim();
        passwd = etPasswd.getText().toString().trim();
        passwdConf = etConfirmarPasswd.getText().toString().trim();

        if (camposNoVacios()) {

            RequestBody formBody = new FormBody.Builder()
                    .add("nombre_agrega", nombre)
                    .add("correo_agrega", correo)
                    .add("celular_agrega", celular)
                    .add("password_agrega", passwd)
                    .build();

            Request request = new Request.Builder()
                    .url("http://"+IP.ip+"/comeback/api/public/usuarios/register")
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response responseHTTP) throws IOException {

                    try {

                        if (!responseHTTP.isSuccessful()) throw new IOException("Repuesta inesperada " + responseHTTP);

                        JSONObject response = new JSONObject(responseHTTP.body().string());

                        boolean success = response.getBoolean("success");
                        String mensaje = response.getString("message");

                        if (success) {

                            RegistroActivity.this.runOnUiThread(() -> Toast.makeText(RegistroActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show());

                            updateUI();

                        } else {
                            RegistroActivity.this.runOnUiThread(() -> Toast.makeText(RegistroActivity.this,"Error: " + mensaje, Toast.LENGTH_SHORT).show());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        } else {
            Toast.makeText(RegistroActivity.this, "Rellene todos los campos. Las contraseñas deben coincidir.", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean camposNoVacios() {

        if (!(nombre.equals("") && correo.equals("") && celular.equals("") && passwd.equals("") && passwdConf.equals(""))) {
            if (passwd.equals(passwdConf)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

}