package com.tripndream.comeback;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReporteViewHolder extends RecyclerView.ViewHolder{
    public ImageView ivFotoPerro;
    public TextView tvNombre, tvRaza, tvColonia, tvDescripcion, tvFecha, tvCelular;
    private ReporteAdapter.OnReporteClickListener onReporteClickListener;

    public ReporteViewHolder(@NonNull View itemView, ReporteAdapter.OnReporteClickListener onReporteClickListener) {
        super(itemView);
        ivFotoPerro = itemView.findViewById(R.id.ivFotoPerro);
        tvNombre = itemView.findViewById(R.id.tvNombre);
        tvRaza = itemView.findViewById(R.id.tvRaza);
        tvColonia = itemView.findViewById(R.id.tvColonia);
        tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        tvFecha = itemView.findViewById(R.id.tvFecha);
        tvCelular = itemView.findViewById(R.id.tvCelular);

        this.onReporteClickListener = onReporteClickListener;
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                onReporteClickListener.onReporteClickListener(position);
            }
        });
    }
}
