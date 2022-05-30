package com.tripndream.comeback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tripndream.comeback.utils.WebService;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int LOGIN_REQUEST_CODE = 101;
    private static final String KEY_EMAIL_USER = "key_email_user";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public OkHttpClient client;

    private EditText etCorreoLogin, etPasswdLogin;
    private TextView btnRegistro;
    private Button btnLoginNormal;
    private Button btnRecovery;

    private SharedPreferences sp;
    private String correo;

    @Override
    protected void onStart() {

        super.onStart();

        String emailUser = sp.getString("session", "");

        if (!emailUser.equals("")) {
            updateUI();
        }

    }

    private void updateUI() {
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra(KEY_EMAIL_USER, correo);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configMaterials();

    }

    private void configMaterials() {

        etCorreoLogin = findViewById(R.id.etCorreoLogin);
        etPasswdLogin = findViewById(R.id.etPasswdLogin);
        client = new OkHttpClient();

        btnRegistro = findViewById(R.id.btnRegistro);
        btnLoginNormal = findViewById(R.id.btnLoginNormal);
        btnRecovery = findViewById(R.id.btnRecuperarCuenta);

        sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        configClickListener();

    }

    private void configClickListener() {

        btnRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        btnLoginNormal.setOnClickListener(v -> login());

        btnRecovery.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecoveryActivity.class);
            startActivity(intent);
        });

    }

    private void login() {

        correo = etCorreoLogin.getText().toString().trim();
        String passwd = etPasswdLogin.getText().toString().trim();

        if (correo.equals("") || passwd.equals("")) {
            Toast.makeText(MainActivity.this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
        }

        RequestBody formBody = new FormBody.Builder()
                .add("correo", correo)
                .add("password", passwd)
                .build();

        Request request = new Request.Builder()
                .url(WebService.URL_USER_LOGIN)
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

                    MainActivity.this.runOnUiThread(() -> Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show());

                    if (success) {

                        String id = response.getString("id");
                        String nombre = response.getString("nombre");
                        String perrosRescatados = response.getString("perrosRescatados");
                        String nEncontrados = response.getString("nEncontrados");

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("session", correo);
                        editor.putString("id", id);
                        editor.putString("nombre", nombre);
                        editor.putString("perrosRescatados", perrosRescatados);
                        editor.putString("nEncontrados", nEncontrados);
                        editor.commit();
                        updateUI();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

}