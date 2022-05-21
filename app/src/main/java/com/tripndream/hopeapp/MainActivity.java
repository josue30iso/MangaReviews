package com.tripndream.hopeapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.tripndream.hopeapp.utils.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int LOGIN_REQUEST_CODE = 101;
    private static final String KEY_EMAIL_USER = "key_email_user";

    private ConstraintLayout consInicio;
    private ConstraintLayout consLogin;

    private EditText etCorreoLogin, etPasswdLogin;
    private TextView btnRegistro;
    private Button btnLoginNormal;
    private Button btnViewLogin;
    private ImageButton btnViewInicio;

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
        btnViewLogin = findViewById(R.id.btmViewLogin);
        btnViewInicio = findViewById(R.id.ibReturnMain);

        consInicio = findViewById(R.id.consInicio);
        consLogin = findViewById(R.id.consLogin);

        sp = getSharedPreferences("emailUserSession", Context.MODE_PRIVATE);

        configClickListener();

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

        btnViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consInicio.setVisibility(View.GONE);
                consLogin.setVisibility(View.VISIBLE);
            }
        });

        btnViewInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consLogin.setVisibility(View.GONE);
                consInicio.setVisibility(View.VISIBLE);
            }
        });

    }

    private void login() {

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

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

    }

}