package com.tripndream.comeback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tripndream.comeback.utils.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int LOGIN_REQUEST_CODE = 101;
    private static final String KEY_FIREBASE_USER = "key_firebase_user";
    private static final String KEY_EMAIL_USER = "key_email_user";

    private EditText etCorreoLogin, etPasswdLogin;
    private TextView btnRegistro;
    private Button btnLoginNormal;

    private GoogleSignInOptions gso;

    private FirebaseAuth fba;
    private FirebaseUser user;

    private SharedPreferences sp;
    private String correo;

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseUser currentUser = fba.getCurrentUser();

        String emailUser = sp.getString("session", "");

        if (currentUser != null) {
            updateUI(currentUser);
        }

        if (!emailUser.equals("")) {
            updateUI();
        }

    }

    private void updateUI(FirebaseUser currentUser) {
        Intent intent = new Intent(this, InicioActivity.class);
        intent.putExtra(KEY_FIREBASE_USER, user);
        startActivity(intent);
        finish();
    }

    private void updateUI() {
        Intent intent = new Intent(this, InicioActivity.class);
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

        btnRegistro = findViewById(R.id.btnRegistro);
        btnLoginNormal = findViewById(R.id.btnLoginNormal);

        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        fba = FirebaseAuth.getInstance();
        user = fba.getCurrentUser();

        sp = getSharedPreferences("emailUserSession", Context.MODE_PRIVATE);

        if (user != null) {
            updateUI(user);
        } else {
            configClickListener();
        }



    }

    private void configClickListener() {

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        btnLoginNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void login() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
        finish();
        /*RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        correo = etCorreoLogin.getText().toString().trim();
        String passwd = etPasswdLogin.getText().toString().trim();

        Map map = new HashMap();
        map.put("correo", correo);
        map.put("password", passwd);

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_USER_LOGIN, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");
                    String mensaje = response.getString("message");
                    Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();

                    if (success) {

                        String id = response.getString("id");
                        Boolean esAdmin = 1 == Integer.parseInt(response.getString("esAdmin"));
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("session", correo);
                        editor.putString("logedID", id);
                        editor.putBoolean("esAdmin", esAdmin);
                        editor.commit();
                        updateUI();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;
        */
    }

    private void registroBD(GoogleSignInAccount account) {

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

        Map map = new HashMap();

        map.put("id_agrega", account.getId());
        map.put("nombre_agrega", account.getDisplayName());
        map.put("correo_agrega", account.getEmail());
        map.put("celular_agrega", "1111111111");
        map.put("password_agrega", account.getFamilyName());

        JSONObject data = new JSONObject( map );
        Log.i("MainActivity", data.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_USER_REGISTER, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");
                    String mensaje = response.getString("message");

                    if (success) {

                        Toast.makeText(MainActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Este correo electronico ya existe", Toast.LENGTH_SHORT).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;


    }

}