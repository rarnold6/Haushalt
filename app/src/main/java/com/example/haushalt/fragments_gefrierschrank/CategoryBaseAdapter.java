package com.example.haushalt.fragments_gefrierschrank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.haushalt.Gefrierschrank;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.R;
import com.example.haushalt.UnitsAndCategories;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;

public class CategoryBaseAdapter extends BaseAdapter {

    private LinkedList<GefrierschrankElement> gefrierschrankElts;
    private Context context;
    private long categoryID;

    public CategoryBaseAdapter(Context context, long categoryID, LinkedList<GefrierschrankElement> gefrierschrankElements, Gefrierschrank gefrierschrank){
        this.context = context;
        this.categoryID = categoryID;
        this.gefrierschrankElts = new LinkedList<GefrierschrankElement>();
        Iterator<GefrierschrankElement> it = gefrierschrankElements.iterator();
        while(it.hasNext()){
            GefrierschrankElement nextElt = it.next();
            if (gefrierschrank != null) {
                if(nextElt.getFood().getKategorie_id() == categoryID && nextElt.getFach().getGefrierschrank() == gefrierschrank){
                    this.gefrierschrankElts.add(nextElt);
                }
            } else {
                if(nextElt.getFood().getKategorie_id() == categoryID){
                    this.gefrierschrankElts.add(nextElt);
                }
            }

        }
    }

    @Override
    public int getCount() {
        return this.gefrierschrankElts.size();
    }

    @Override
    public Object getItem(int i) {
        return this.gefrierschrankElts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.gefrierschrankElts.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.kategorien_element, viewGroup, false);

        TextView foodName = (TextView) view.findViewById(R.id.category_foodname);
        TextView amount = (TextView) view.findViewById(R.id.category_amount);
        TextView unit = (TextView) view.findViewById(R.id.category_unit);
        TextView date = (TextView) view.findViewById(R.id.category_date);
        TextView place = (TextView) view.findViewById(R.id.category_place);

        GefrierschrankElement gefrierschrankElement = (GefrierschrankElement) getItem(i);

        foodName.setText(gefrierschrankElement.getFood().getEssensname());
        amount.setText(String.valueOf(gefrierschrankElement.getAnzahl()));
        unit.setText(UnitsAndCategories.getUnit(gefrierschrankElement.getFood().getEinheit_id()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date.setText("Eingeräumt am " + sdf.format(gefrierschrankElement.getDate()));
        place.setText("in Fach " + String.valueOf(gefrierschrankElement.getFach().getFachNummer()));

        return view;
    }
}
