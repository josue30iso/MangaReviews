package com.tripndream.hopeapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.tripndream.hopeapp.utils.WebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RecoveryActivity extends AppCompatActivity {

    private TextView tvInstruccion, tvE;
    private Button btnRecovery;
    private EditText etCorreoCodPass;
    private EditText etPasswd;
    private EditText etPasswdConf;
    private LinearLayout llPasswords;

    private int paso = 1;
    private String correoUsuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

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

                RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

                Map map = new HashMap();
                map.put("password", passwd);
                map.put("correo", correoUsuario);

                JSONObject data = new JSONObject( map );

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_USER_CAMPAS, data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            boolean success = response.getBoolean("success");
                            String mensaje = response.getString("message");

                            if (success) {

                                Toast.makeText(RecoveryActivity.this, "Contraseña cambiada con exito", Toast.LENGTH_LONG).show();
                                updateUI();

                            } else {
                                Toast.makeText(RecoveryActivity.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_LONG).show();
                                Log.e("Error", response.toString());
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RecoveryActivity.this, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

                rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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

            RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

            Map map = new HashMap();
            map.put("codigo", codigo);
            map.put("correo", correoUsuario);

            JSONObject data = new JSONObject( map );

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_USER_VERIF, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        boolean success = response.getBoolean("success");
                        String mensaje = response.getString("message");

                        if (success) {

                            paso = 3;
                            tvInstruccion.setText("Ingrese su nueva contraseña");
                            btnRecovery.setText("Cambiar contraseña");
                            etCorreoCodPass.setVisibility(View.GONE);
                            tvE.setVisibility(View.GONE);
                            llPasswords.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(RecoveryActivity.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_LONG).show();
                            Log.e("Error", response.toString());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RecoveryActivity.this, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;



        } else {
            Toast.makeText(RecoveryActivity.this, "Debes especificar un codigo de SEIS digitos", Toast.LENGTH_LONG).show();
        }

    }

    private void recuperar() {

        String correo = etCorreoCodPass.getText().toString().trim();

        if (!correo.equals("") && isEmailValid(correo)) {

            RequestQueue rq = Volley.newRequestQueue(getApplicationContext());

            Map map = new HashMap();
            map.put("correo", correo);

            Log.i("correo", correo);

            JSONObject data = new JSONObject( map );

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_USER_RECOV, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        boolean success = response.getBoolean("success");
                        String mensaje = response.getString("message");

                        if (success) {

                            correoUsuario = correo;
                            paso = 2;
                            tvInstruccion.setText("Te hemos enviado un codigo de verificacion. Ponerlo en el siguiente recuadro para recuperar su cuenta");
                            btnRecovery.setText("Verificar");
                            etCorreoCodPass.setText("");
                            tvE.setText("Codigo verificacion");

                        } else {
                            Toast.makeText(RecoveryActivity.this, "Ha ocurrido un error: " + mensaje, Toast.LENGTH_LONG).show();
                            Log.e("Error", response.toString());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RecoveryActivity.this, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;



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