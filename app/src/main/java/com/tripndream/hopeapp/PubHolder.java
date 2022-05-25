package com.tripndream.hopeapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PubHolder extends RecyclerView.ViewHolder {

    public ImageView ivImagenPubInicio;
    public ImageButton ibGuardar;
    public TextView tvNombrePubInicio, tvZonaPubInicio, tvAutorPubInicio, tvFechaPubInicio;
    public PubAdapter.OnPubClickListener onPublicacionClickListener;
    public ArrayList<Publicacion> data;
    public SharedPreferences sp;

    public ImageView getIvImagenPubInicio() {
        return ivImagenPubInicio;
    }

    public TextView getTvNombrePubInicio() {
        return tvNombrePubInicio;
    }

    public TextView getTvAutorPubInicio() {
        return tvAutorPubInicio;
    }

    public TextView getTvFechaPubInicio() {
        return tvFechaPubInicio;
    }

    public PubHolder(@NonNull View itemView, PubAdapter.OnPubClickListener onPublicacionClickListener, ArrayList<Publicacion> data, int currendUserId) {

        super(itemView);

        this.data = data;

        ivImagenPubInicio = itemView.findViewById(R.id.ivImagenPub);
        ibGuardar = itemView.findViewById(R.id.ibGuardar);

        tvNombrePubInicio = itemView.findViewById(R.id.tvNombreDesaparecido);
        tvZonaPubInicio = itemView.findViewById(R.id.tvZonaPub);
        tvAutorPubInicio = itemView.findViewById(R.id.tvAutorPub);
        tvFechaPubInicio = itemView.findViewById(R.id.tvFechaPub);

        this.onPublicacionClickListener = onPublicacionClickListener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                onPublicacionClickListener.onPubClickListener(position);
            }
        });

         ibGuardar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 guardarPublicacion(data.get(getAdapterPosition()).getId(), currendUserId, v.getContext());
             }
         });
    }

    private void guardarPublicacion(int idPublicacion, int currendUserId, Context context) {

        RequestQueue rq = Volley.newRequestQueue(context, new HurlStack());

        Map map = new HashMap();
        map.put("idPublicacion", idPublicacion);
        map.put("idUsuario", currendUserId);

        JSONObject data = new JSONObject( map );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, WebService.URL_PUB_SAVE, data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    boolean success = response.getBoolean("success");

                    if (success) {

                        Boolean guardado = response.getBoolean("guardado");
                        if (guardado) {
                            ibGuardar.setColorFilter(Color.RED);
                        } else {
                            ibGuardar.clearColorFilter();
                        }

                    } else {
                        Toast.makeText(context, "Sin resultados.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Ha ocurrido un error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        rq.add(jsonObjectRequest).setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}