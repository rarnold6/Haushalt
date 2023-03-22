package com.example.haushalt.activity;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.haushalt.Fach;
import com.example.haushalt.R;
import com.example.haushalt.data.HaushaltContract;

import java.util.LinkedList;

/*
    eine CursorAdapter-Klasse dient dazu, dass die Daten aus der Datenbank in dem uebergebenen
    Cursor gespeichert sind und diese dann ausgelesen und in die TextViews usw. geschrieben werden
    koennen
 */
public class GefrierschrankBaseAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<Fach> faecher;

    public GefrierschrankBaseAdapter(Context context, LinkedList<Fach> faecher) {
        this.context = context;
        this.faecher = faecher;
    }

    @Override
    public int getCount() {
        return faecher.size();
    }

    @Override
    public Object getItem(int pos) {
        return faecher.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return faecher.get(pos).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.list_element, viewGroup, false);

        Fach fach = (Fach) getItem(i);

        TextView fachTextView = (TextView) view.findViewById(R.id.fach);
        TextView beschriftungTextView = (TextView) view.findViewById(R.id.beschriftung);

        fachTextView.setText(fach.getFachNummer() + ".Fach");
        if(fach.getBeschriftung() == null){
            beschriftungTextView.setText("Unknown");
        } else {
            beschriftungTextView.setText(fach.getBeschriftung());
        }



        return view;
    }
}
