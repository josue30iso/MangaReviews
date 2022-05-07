package com.tripndream.hopeapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewHolder> {

    private ArrayList<Review> data;
    private OnRecetaClickListener onRecetaClickListener;

    public ReviewAdapter(ArrayList<Review> datos, OnRecetaClickListener onRecetaClickListener) {

        this.onRecetaClickListener = onRecetaClickListener;
        data = new ArrayList<>();

    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receta_row, parent, false);
        return new ReviewHolder(view, onRecetaClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {

        holder.tvNombreReviewInicio.setText( data.get(position).getTitulo());
        holder.tvAutorReviewInicio.setText( data.get(position).getNombreUsuario());

        try {

            String fecha = data.get(position).getFechaCreacion();
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha);
            holder.tvFechaReviewInicio.setText( new SimpleDateFormat("dd/MM/yyyy").format(date) );

            String b64 = data.get(position).getImagen();

            byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.ivImagenReviewInicio.setImageBitmap(decodedImage);

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(Review receta) {

        data.add(receta);
        notifyDataSetChanged();

    }

    public void clear() {

        data.clear();
        notifyDataSetChanged();

    }

    public interface OnRecetaClickListener{

        void onRecetaClickListener(int position);

    }

}