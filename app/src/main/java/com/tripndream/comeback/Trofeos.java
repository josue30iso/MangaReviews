package com.tripndream.comeback;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Trofeos extends AppCompatActivity implements View.OnClickListener{
    ImageView t1, t2, t3, t4, t5, t6;
    String m1, m2, m3, m4, m5, m6;
    public OkHttpClient client;
    int encontrados, refugiados;

    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trofeos);

        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);
        t5 = findViewById(R.id.t5);
        t6 = findViewById(R.id.t6);

        m1 = "¡Sigue apoyando y ayudando a la comunidad para desbloquear este trofeo!";
        m2 = "¡Sigue apoyando y ayudando a la comunidad para desbloquear este trofeo!";
        m3 = "¡Sigue apoyando y ayudando a la comunidad para desbloquear este trofeo!";
        m4 = "¡Sigue apoyando y ayudando a la comunidad para desbloquear este trofeo!";
        m5 = "¡Sigue apoyando y ayudando a la comunidad para desbloquear este trofeo!";
        m6 = "¡Sigue apoyando y ayudando a la comunidad para desbloquear este trofeo!";

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        sp = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        Log.d("Correo", sp.getString("session", "-1"));
        client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("correo", sp.getString("session", "-1"))
                .build();

        Request request = new Request.Builder()
                .url("http://"+IP.ip+"/comeback/api/public/usuarios/getEncontrados")
                .post(formBody)
                .build();

        try {
            Response responseHTTP = client.newCall(request).execute();
            JSONObject response = new JSONObject(responseHTTP.body().string());;

            boolean success = response.getBoolean("success");
            String message = response.getString("message");

            if (success) {
                encontrados = response.getInt("nEncontrados");
                Log.d("Encontrados", String.valueOf(response.getInt("nEncontrados")));
                if(encontrados>=1){
                    t1.setImageDrawable(getResources().getDrawable(R.drawable.trofeo));
                    m1 = "Trofeo obtenido por haber ayudado en reencontrar a un peludito con su familia. ¡Gracias!";
                    if(encontrados>=3){
                        t3.setImageDrawable(getResources().getDrawable(R.drawable.trofeo));
                        m3 = "Trofeo obtenido por haber ayudado en reencontrar a tres peluditos con sus respectivas familias. ¡Gracias!";
                        if(encontrados>=5){
                            t5.setImageDrawable(getResources().getDrawable(R.drawable.trofeo));
                            m5 = "No puede ser casualidad. ¡Muchas gracias por tu dedicación y haber ayudado a cinco o más peluditos a reencontrarse con su familia!";
                        }
                    }
                }

                refugiados = response.getInt("nRefugiados");
                Log.d("Encontrados", String.valueOf(response.getInt("nRefugiados")));
                if(refugiados>=1){
                    t2.setImageDrawable(getResources().getDrawable(R.drawable.trofeo));
                    m2 = "Trofeo obtenido por haber refugiado a un peludito en un hogar. ¡Gracias!";
                    if(refugiados>=3){
                        t4.setImageDrawable(getResources().getDrawable(R.drawable.trofeo));
                        m4 = "Trofeo obtenido por haberle encontrado un hogar a tres perritos";
                        if(refugiados>=5){
                            t6.setImageDrawable(getResources().getDrawable(R.drawable.trofeo));
                            m6 = "¡Muchísimas gracias por tu labor de salvar a estos hermosos seres de la calle!";
                        }
                    }
                }
            } else {
                Trofeos.this.runOnUiThread(() -> Toast.makeText(Trofeos.this, message, Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        t1.setOnClickListener(this);
        t2.setOnClickListener(this);
        t3.setOnClickListener(this);
        t4.setOnClickListener(this);
        t5.setOnClickListener(this);
        t6.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.t1:
                trofeoInfo("Buscaperros", m1);
                break;
            case R.id.t2:
                trofeoInfo("Refugiadog", m2);
                break;
            case R.id.t3:
                trofeoInfo("Altruista", m3);
                break;
            case R.id.t4:
                trofeoInfo("Hospedero perruno", m4);
                break;
            case R.id.t5:
                trofeoInfo("Héroe perruno", m5);
                break;
            case R.id.t6:
                trofeoInfo("Holidog Inn", m6);
                break;
        }
    }

    private void trofeoInfo( String nombreLogro, String mensaje) {

        AlertDialog.Builder alert = new AlertDialog.Builder(Trofeos.this, R.style.AlertDialogStyle);
        alert.setTitle(Html.fromHtml(nombreLogro))
                .setMessage(Html.fromHtml(mensaje))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();;
                    }

                }).show();

    }
}