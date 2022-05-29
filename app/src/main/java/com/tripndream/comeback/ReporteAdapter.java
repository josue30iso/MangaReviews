package com.tripndream.comeback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        try {
            String b64 = data.get(position).getImagen();
            byte[] imageBytes = Base64.decode(b64, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.ivFotoPerro.setImageBitmap(decodedImage);
            holder.tvNombre.setText(data.get(position).getNombre());
            holder.tvRaza.setText(data.get(position).getRaza());
            holder.tvColonia.setText(data.get(position).getColonia());
            holder.tvDescripcion.setText(data.get(position).getDescripcion());
            holder.tvRecompensa.setText("$"+String.valueOf(data.get(position).getRecompensa()));
            String fechaSinFormato = data.get(position).getFecha();
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fechaSinFormato);
            holder.tvFecha.setText( new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date) );

            switch (String.valueOf(data.get(position).getEstatus())){
                case "1":
                    holder.tvNombre.setVisibility(View.VISIBLE);
                    if (String.valueOf(data.get(position).getRecompensa()).equals("0.0")){
                        holder.tvRecompensa.setVisibility(View.GONE);
                    }else{
                        holder.tvRecompensa.setVisibility(View.VISIBLE);
                    }
                    holder.bgCard.setBackgroundColor(Color.parseColor("#B5C7E1"));
                    break;
                case "2":
                    holder.tvNombre.setVisibility(View.GONE);
                    holder.tvRecompensa.setVisibility(View.GONE);
                    holder.bgCard.setBackgroundColor(Color.parseColor("#FDE8CB"));

                    break;
                case "3":
                    holder.tvNombre.setVisibility(View.GONE);
                    holder.tvRecompensa.setVisibility(View.GONE);
                    holder.bgCard.setBackgroundColor(Color.parseColor("#FFAAC9"));

                    break;
                case "4":
                    holder.tvNombre.setVisibility(View.VISIBLE);
                    holder.tvRecompensa.setVisibility(View.GONE);
                    holder.bgCard.setBackgroundColor(Color.parseColor("#ABE1AF"));

                    break;
            }

            Log.d("RECOMPENSA ROW", String.valueOf(data.get(position).getRecompensa()));

            holder.tvCelular.setText(data.get(position).getCelular());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void add(Reporte reporte) {

        data.add(reporte);

    }

    public interface OnReporteClickListener{
        void onReporteClickListener(int position);
    }
}
