package com.example.haushalt.suche;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.haushalt.EinkaufszettelElement;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.R;
import com.example.haushalt.UnitsAndCategories;
import com.example.haushalt.activity.ObjectCollections;
import com.example.haushalt.data.HaushaltContract;
import com.example.haushalt.einkaufszettel.EinkaufszettelFragment;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class AutoFillBaseAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private LinkedList<?> dataList;
    private LinkedList<?> dataListCopy;
    private boolean gefrierschrankSuche;

    public AutoFillBaseAdapter(Context context, LinkedList<?> dataList) {
        super();
        this.context = context;
        // um rauszubekommen, ob auf dem Einkaufszettel oder im Gefrierschrank gesucht werden soll

        try{
            if(dataList.size() > 0) {
                GefrierschrankElement gefrierschrankElement = (GefrierschrankElement) dataList.getFirst();
            }
            this.dataList = (LinkedList<GefrierschrankElement>) dataList.clone();
            this.dataListCopy = (LinkedList<?>) dataList.clone();
            this.gefrierschrankSuche = true;
        } catch (ClassCastException cce) {
            this.dataList = (LinkedList<EinkaufszettelElement>) dataList.clone();
            this.dataListCopy = (LinkedList<?>) dataList.clone();
            this.gefrierschrankSuche = false;
        }

    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return gefrierschrankSuche ? ((LinkedList<GefrierschrankElement>) dataList).get(i).getId() : ((LinkedList<EinkaufszettelElement>) dataList).get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.suche_element, viewGroup, false);

        TextView essensnameView = view.findViewById(R.id.suche_essensname);
        TextView anzahlView = view.findViewById(R.id.suche_anzahl);
        TextView einheitView = view.findViewById(R.id.suche_einheit);
        TextView gegenstandView = view.findViewById(R.id.suche_gegenstand);
        TextView fachView = view.findViewById(R.id.suche_fach);
        TextView kategorieView = view.findViewById(R.id.suche_kategorie);
        TextView datumView = view.findViewById(R.id.suche_datum);

        if(gefrierschrankSuche){
            GefrierschrankElement gefrierschrankElement = (GefrierschrankElement) getItem(i);
            essensnameView.setText(gefrierschrankElement.getFood().getEssensname());
            anzahlView.setText(String.valueOf(gefrierschrankElement.getAnzahl()));
            einheitView.setText(UnitsAndCategories.getUnit(gefrierschrankElement.getFood().getEinheit_id()));
            gegenstandView.setText("Im " + gefrierschrankElement.getFach().getGefrierschrank().getLabel());
            fachView.setText("in Fach: " + gefrierschrankElement.getFach().getFachNummer());
            kategorieView.setText(UnitsAndCategories.getCategory(gefrierschrankElement.getFood().getKategorie_id()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            datumView.setText(sdf.format(gefrierschrankElement.getDate()));
        } else {
            EinkaufszettelElement einkaufszettelElement = (EinkaufszettelElement) getItem(i);
            essensnameView.setText(einkaufszettelElement.getFood().getEssensname());
            anzahlView.setText(String.valueOf(einkaufszettelElement.getAnzahlWunsch()));
            einheitView.setText(UnitsAndCategories.getUnit(einkaufszettelElement.getFood().getEinheit_id()));
            gegenstandView.setText("Auf dem Einkaufszettel ");
            kategorieView.setText(UnitsAndCategories.getCategory(einkaufszettelElement.getFood().getKategorie_id()));
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter(){

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if(charSequence == null || charSequence.length() == 0) {
                results.values = null;
                results.count = 0;
            } else {
                if (gefrierschrankSuche) {
                    LinkedList<GefrierschrankElement> foundElements = new LinkedList<>();

                    for (GefrierschrankElement gefrierschrankElement : (LinkedList<GefrierschrankElement>) dataListCopy) {
                        if (gefrierschrankElement.getFood().getEssensname().toLowerCase().matches(charSequence.toString().toLowerCase() + ".*")) {
                            foundElements.add(gefrierschrankElement);
                        }
                    }
                    results.values = foundElements;
                    results.count = foundElements.size();
                } else {
                    LinkedList<EinkaufszettelElement> foundElements = new LinkedList<>();

                    for(EinkaufszettelElement einkaufszettelElement : (LinkedList<EinkaufszettelElement>)dataListCopy){
                        if(einkaufszettelElement.getFood().getEssensname().toLowerCase().matches(charSequence.toString().toLowerCase() + ".*")){
                            foundElements.add(einkaufszettelElement);
                        }
                    }
                    results.values = foundElements;
                    results.count = foundElements.size();
                }

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if(filterResults.count > 0) {
                dataList.clear();
                dataList.addAll((List) filterResults.values);
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }


        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return gefrierschrankSuche ? ((GefrierschrankElement) resultValue).getFood().getEssensname() : ((EinkaufszettelElement) resultValue).getFood().getEssensname();
        }
    };


}
