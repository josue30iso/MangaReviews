package com.tripndream.comeback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteViewHolder> {

    private ArrayList<Reporte> data;
    private OnReporteClickListener onReporteClickListener;

    public ReporteAdapter(ArrayList<Reporte> data, OnReporteClickListener onReporteClickListener) {
        this.data = data;
        this.onReporteClickListener = onReporteClickListener;
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dog_card_row, parent, false);
        return new ReporteViewHolder(view, onReporteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteViewHolder holder, int position) {
        String b64 = data.get(position).getImagen();
        byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.ivFotoPerro.setImageBitmap(decodedImage);
        holder.tvNombre.setText(data.get(position).getNombre());
        holder.tvRaza.setText(data.get(position).getRaza());
        holder.tvGenero.setText(data.get(position).getGenero());
        holder.tvDescripcion.setText(data.get(position).getDescripcion());
        holder.tvFecha.setText(data.get(position).getFecha());
        holder.tvCelular.setText(data.get(position).getCelular());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnReporteClickListener{
        void onReporteClickListener(int position);
    }
}
