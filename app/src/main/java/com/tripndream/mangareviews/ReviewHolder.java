package com.tripndream.mangareviews;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewHolder extends RecyclerView.ViewHolder {

    public ImageView ivImagenReviewInicio;
    public TextView tvNombreReviewInicio, tvAutorReviewInicio, tvFechaReviewInicio;
    public ReviewAdapter.OnRecetaClickListener onRecetaClickListener;

    public ImageView getIvImagenReviewInicio() {
        return ivImagenReviewInicio;
    }

    public TextView getTvNombreReviewInicio() {
        return tvNombreReviewInicio;
    }

    public TextView getTvAutorReviewInicio() {
        return tvAutorReviewInicio;
    }

    public TextView getTvFechaReviewInicio() {
        return tvFechaReviewInicio;
    }

    public ReviewHolder(@NonNull View itemView, ReviewAdapter.OnRecetaClickListener onRecetaClickListener) {

        super(itemView);

        ivImagenReviewInicio = itemView.findViewById(R.id.ivImagenRecetaInicio);

        tvNombreReviewInicio = itemView.findViewById(R.id.tvNombreRecetaInicio);
        tvAutorReviewInicio = itemView.findViewById(R.id.tvAutorRecetaInicio);
        tvFechaReviewInicio = itemView.findViewById(R.id.tvFechaRecetaInicio);

        this.onRecetaClickListener = onRecetaClickListener;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                onRecetaClickListener.onRecetaClickListener(position);
            }
        });

    }

}