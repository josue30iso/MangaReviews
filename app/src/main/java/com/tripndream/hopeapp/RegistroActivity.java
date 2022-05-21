package com.tripndream.hopeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tripndream.hopeapp.utils.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etNombreUsuario, etPasswd, etConfirmarPasswd;
    private Button btnAccionRegistro;
    private String nombre, correo, nombreUsuario, passwd, passwdConf;

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
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etPasswd = findViewById(R.id.etPasswdRegistro);
        etConfirmarPasswd = findViewById(R.id.etConfirmarPasswdRegistro);

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
        nombreUsuario = etNombreUsuario.getText().toString().trim();
        passwd = etPasswd.getText().toString().trim();
        passwdConf = etConfirmarPasswd.getText().toString().trim();

        if (camposNoVacios()) {

            RequestQueue rq = Volley.newRequestQueue(getApplicationContext(), new HurlStack());

            JSONObject data = new JSONObject( construirMapa() );
            Log.i("RegistroActivity", data.toString());

            JsonObjectRequest jsonObjectRequest = (JsonObjectRequest) new JsonObjectRequest(Request.Method.POST, WebService.URL_USER_REGISTER, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        boolean success = response.getBoolean("success");
                        String mensaje = response.getString("message");

                        if (success) {

                            Toast.makeText(RegistroActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                            updateUI();

                        } else {
                            Toast.makeText(RegistroActivity.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegistroActivity.this, "Ha ocurrido un error2: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*4,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;

        } else {
            Toast.makeText(RegistroActivity.this, "Rellene todos los campos. Las contraseñas deben coincidir.", Toast.LENGTH_SHORT).show();
        }

    }

    private Map construirMapa() {

        Map map = new HashMap();
        map.put("id_agrega", UUID.randomUUID().toString().replaceAll("-", ""));
        map.put("nombre_agrega", nombre);
        map.put("correo_agrega", correo);
        map.put("nombreUsuario_agrega", nombreUsuario);
        map.put("password_agrega", passwd);

        return map;

    }

    private boolean camposNoVacios() {

        if (!(nombre.equals("") && correo.equals("") && nombreUsuario.equals("") && passwd.equals("") && passwdConf.equals(""))) {
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