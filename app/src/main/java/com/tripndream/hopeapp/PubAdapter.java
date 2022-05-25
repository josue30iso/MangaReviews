package com.tripndream.hopeapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PubAdapter extends RecyclerView.Adapter<PubHolder> {

    private ArrayList<Publicacion> data;
    private OnPubClickListener onPubClickListener;
    private int currentUserId;

    public PubAdapter(ArrayList<Publicacion> datos, OnPubClickListener onPubClickListener, int currentUserId) {

        this.onPubClickListener = onPubClickListener;
        data = new ArrayList<>();
        this.currentUserId = currentUserId;

    }

    @NonNull
    @Override
    public PubHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pub_row, parent, false);
        return new PubHolder(view, onPubClickListener, data, currentUserId);
    }

    @Override
    public void onBindViewHolder(@NonNull PubHolder holder, int position) {

        holder.tvNombrePubInicio.setText( data.get(position).getNombreDesaparecido());
        holder.tvAutorPubInicio.setText( data.get(position).getNombreUsuario());
        holder.tvZonaPubInicio.setText( data.get(position).getNombreZona());

        try {

            String fecha = data.get(position).getFechaRegistro();
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha);
            holder.tvFechaPubInicio.setText( new SimpleDateFormat("dd/MM/yyyy").format(date) );

            String b64 = data.get(position).getFoto();
            byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.ivImagenPubInicio.setImageBitmap(decodedImage);

            Boolean saved = data.get(position).getGuardado();
            if (saved) {
                holder.ibGuardar.setColorFilter(Color.RED);
            } else {
                holder.ibGuardar.clearColorFilter();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(Publicacion publicacion) {

        data.add(publicacion);
        notifyDataSetChanged();

    }

    public void clear() {

        data.clear();
        notifyDataSetChanged();

    }

    public interface OnPubClickListener {
        void onPubClickListener(int position);
    }
}