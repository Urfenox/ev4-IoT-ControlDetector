package com.crizacio.controldeteccion;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListaEvento extends ArrayAdapter<Evento> {
    private Activity context;
    List<Evento> eventos;

    public ListaEvento(Activity context, List<Evento> eventos) {
        super(context, R.layout.device_name, eventos);
        this.context = context;
        this.eventos = eventos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
         * RESUMEN:
         * Esto permite generar un objeto el cual tiene un Titulo y un Subtitulo
         * Esto queda bonito.
         * */
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.device_name, null, true);

        TextView txt_Titulo = (TextView) listViewItem.findViewById(R.id.txtTitulo);
        TextView txt_Descripcion = (TextView) listViewItem.findViewById(R.id.txtSubtitulo);

        Evento evento = eventos.get(position);
        txt_Titulo.setText(""+evento.gettitulo());
        txt_Descripcion.setText(""+evento.getdescripcion());

        return listViewItem;
    }
}