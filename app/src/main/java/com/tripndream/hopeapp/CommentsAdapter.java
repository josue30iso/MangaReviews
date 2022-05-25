package com.tripndream.hopeapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsHolder> {

    private ArrayList<Comentario> data;
    private int currentIdUser;

    public CommentsAdapter(ArrayList<Comentario> datos, int currentIdUser) {

        data = new ArrayList<Comentario>();
        this.currentIdUser = currentIdUser;

    }

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false);
        return new CommentsHolder(view, data);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsHolder holder, int position) {
        try {

            Comentario comentario = data.get(position);

            if (comentario.getIdUsuario() == currentIdUser) {
                holder.tvAutorComentario.setText("YO - " + comentario.getNombreUsuario() + ":");
                holder.tvAutorComentario.setTextColor(Color.parseColor("#4AA066"));
            } else {
                if (comentario.getIdUsuarioPublicacion() == comentario.getIdUsuario()) {
                    holder.tvAutorComentario.setText("AUTOR - " + comentario.getNombreUsuario() + ":");
                    holder.tvAutorComentario.setTextColor(Color.parseColor("#993646"));
                } else {
                    holder.tvAutorComentario.setText(comentario.getNombreUsuario() + ":");
                }
            }

            String fecha = comentario.getFechaRegistro();
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fecha);
            holder.tvFechaComentario.setText( new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date) );

            holder.tvComentario.setText(comentario.getMensaje());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(Comentario comentario) {

        data.add(comentario);
        notifyDataSetChanged();

    }

    public void clear() {

        data.clear();
        notifyDataSetChanged();

    }
}