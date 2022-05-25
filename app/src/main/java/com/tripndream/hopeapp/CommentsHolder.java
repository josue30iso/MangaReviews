package com.tripndream.hopeapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentsHolder extends RecyclerView.ViewHolder {

    public TextView tvAutorComentario, tvFechaComentario, tvComentario;
    public ArrayList<Comentario> data;

    public TextView getTvAutorComentario() {
        return tvAutorComentario;
    }

    public TextView getTvFechaComentario() {
        return tvFechaComentario;
    }

    public TextView getTvComentario() {
        return tvComentario;
    }

    public CommentsHolder(@NonNull View itemView, ArrayList<Comentario> data) {

        super(itemView);

        this.data = data;

        tvAutorComentario = itemView.findViewById(R.id.tvAutorComentario);
        tvFechaComentario = itemView.findViewById(R.id.tvFechaComentario);
        tvComentario = itemView.findViewById(R.id.tvComentario);

    }

}