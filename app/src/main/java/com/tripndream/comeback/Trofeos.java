package com.tripndream.comeback;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;

public class Trofeos extends AppCompatActivity implements View.OnClickListener{
    ImageView t1, t2, t3, t4, t5, t6;
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
                trofeoInfo("Buscaperros", "Trofeo obtenido por haber ayudado en reencontrar a un peludito con su familia. ¡Gracias!");
                break;
            case R.id.t2:
                trofeoInfo("Refugiadog", "Trofeo obtenido por haber ayudado a un perrito a encontrar un hogar. ¡Gracias!");
                break;
            case R.id.t3:
                trofeoInfo("Altruista", "Trofeo obtenido por haber ayudado en reencontrar a tres peluditos con sus respectivas familias. ¡Gracias!");
                break;
            case R.id.t4:
                trofeoInfo("Hospedero perruno", "Gracias por haber encontrado un hogar a al menos tres perritos");
                break;
            case R.id.t5:
                trofeoInfo("Héroe perruno", "No puede ser casualidad. ¡Muchas gracias por tu dedicación y haber ayudado a cinco o más peluditos a reencontrarse con su familia!");
                break;
            case R.id.t6:
                trofeoInfo("Holidog Inn", "¡Muchísimas gracias por salvar de la calle a tantos perritos!");
                break;
        }
    }

    private void trofeoInfo( String nombreLogro, String mensaje) {

        AlertDialog.Builder alert = new AlertDialog.Builder(Trofeos.this, R.style.AlertDialogStyle);
        alert.setTitle(Html.fromHtml("<font color='#E77FB3'>"+nombreLogro+"</font>"))
                .setMessage(Html.fromHtml("<font color='#E77FB3'>"+mensaje+"</font>"))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();;
                    }

                }).show();

    }
}