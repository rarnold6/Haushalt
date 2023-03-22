package com.example.haushalt.einkaufszettel;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.haushalt.EinkaufszettelElement;
import com.example.haushalt.R;
import com.example.haushalt.UnitsAndCategories;
import com.example.haushalt.activity.ObjectCollections;
import com.example.haushalt.data.HaushaltContract;

import java.io.ObjectStreamException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;


public class EinkaufszettelBaseAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<EinkaufszettelElement> einkaufszettelElements;
    private int j = 0;
    private EinkaufszettelElement focusElement;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public EinkaufszettelBaseAdapter(Context context, LinkedList<EinkaufszettelElement> einkaufszettelElements, EinkaufszettelElement focusElement) {
        this.context = context;

        this.einkaufszettelElements = (LinkedList<EinkaufszettelElement>) einkaufszettelElements.clone();
        //dummy data, so that the list gets longer and the FloatingActionButton is not in the foreground for the last element
        //this.einkaufszettelElements = (LinkedList<EinkaufszettelElement>) ObjectCollections.getEinkaufszettelElements().clone();

        if(this.einkaufszettelElements.size() > 10 && this.einkaufszettelElements.getLast().getId() != -1) {
            for (int i = 1; i <= 3; i++) {
                this.einkaufszettelElements.add(new EinkaufszettelElement());
            }
        }

        this.einkaufszettelElements.sort(new Comparator<EinkaufszettelElement>() {
            @Override
            public int compare(EinkaufszettelElement einkaufszettelElement, EinkaufszettelElement t1) {
                return einkaufszettelElement.compareTo(t1);
            }
        });

        this.focusElement = focusElement;

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for(Iterator<EinkaufszettelElement> it = einkaufszettelElements.iterator(); it.hasNext();){

            EinkaufszettelElement currentElement = it.next();
            if(currentElement.getAnzahlWunsch() == 0 && currentElement.getFood() != null){
                it.remove();
            }
            if(currentElement.getFood() == null && ObjectCollections.getEinkaufszettelElements().size() <= 10){
                it.remove();
            }
        }
    }

    //<3

    @Override
    public int getCount() {
        return this.einkaufszettelElements.size();
    }

    @Override
    public Object getItem(int i) {
        return this.einkaufszettelElements.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.einkaufszettelElements.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (getItemId(i) != -1) {

            view = LayoutInflater.from(context).inflate(R.layout.einkaufsliste_element, viewGroup, false);

            final EinkaufszettelElement einkaufszettelElement = (EinkaufszettelElement) getItem(i);

            if (einkaufszettelElement == focusElement) {
                view.setBackgroundColor(Color.YELLOW);
            }

            final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_einkaufsliste_element);
            final TextView elementTextView = (TextView) view.findViewById(R.id.element);
            final TextView anzahlUndEinheitTextView = (TextView) view.findViewById(R.id.anzahl_und_einheit);
            CheckBox einkaufCheckBox = (CheckBox) view.findViewById(R.id.einkauf_check_box);
            final LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.actually_got);

            ImageButton wenigerBekommenImageButton = (ImageButton) view.findViewById(R.id.weniger_bekommen);

            final String element = einkaufszettelElement.getFood().getEssensname();
            elementTextView.setText(element);
            final int anzahl = einkaufszettelElement.getAnzahlWunsch();
            final String einheit = UnitsAndCategories.getUnit(einkaufszettelElement.getFood().getEinheit_id());
            anzahlUndEinheitTextView.setText(anzahl + " " + einheit);

            //mark elements, that are already in the shopping venture
            if(einkaufszettelElement.getAnzahlBekommen() == einkaufszettelElement.getAnzahlWunsch()){
                einkaufCheckBox.setChecked(true);
                elementTextView.setPaintFlags(elementTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                anzahlUndEinheitTextView.setPaintFlags(anzahlUndEinheitTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }


        /*
            CheckBox, um schnell abzuhacken, dass man komplette Menge bekommen hat
         */
            final View finalView = view;
            einkaufCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        einkaufszettelElement.setAnzahlBekommen(einkaufszettelElement.getAnzahlWunsch());
                        elementTextView.setPaintFlags(elementTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        anzahlUndEinheitTextView.setPaintFlags(anzahlUndEinheitTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        ObjectCollections.einkaufszettelChanged = true;
                        ObjectCollections.addedEinkaufsElt.add(einkaufszettelElement.getId());
                    } else {
                        einkaufszettelElement.setAnzahlBekommen(0);
                        elementTextView.setPaintFlags(elementTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        anzahlUndEinheitTextView.setPaintFlags(anzahlUndEinheitTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        ObjectCollections.einkaufszettelChanged = true;
                        ObjectCollections.addedEinkaufsElt.add(einkaufszettelElement.getId());
                    }
                    finalView.setBackgroundColor(Color.WHITE);
                }
            });

        /*
            wenn man weniger bekommen hat, kann man über den ImageButton zu einer erweiterten Ansicht
            kommen, bei der man die bekommene Menge eintragen kann und dies speichern
            kann, so dass am Ende nur dies vom Einkaufszettel abgezogen wird und ein Restbestand bleibt,
            der zu kaufen ist
         */
            wenigerBekommenImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    //parentLayout.setVisibility(View.VISIBLE);
                    //LayoutInflater.from(view.getContext()).inflate(R.layout.actually_got, parentLayout);

                    //sicherstellen, dass nur eine Menge auf einmal veraendert wird (dazu nutzen des Hilfsattributs i)
                    finalView.setBackgroundColor(Color.WHITE);
                    if (j > 0) {
                        Toast toast = Toast.makeText(context, "Speicher zuerst letzt veränderte Menge", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                    j++;

                    final View view2 = LayoutInflater.from(view1.getContext()).inflate(R.layout.actually_got, parentLayout, false);
                    parentLayout.addView(view2);

                    final EditText gotAmount = (EditText) view2.findViewById(R.id.got_amount);
                    TextView einheitTextView = (TextView) view2.findViewById(R.id.einheitVariabel);
                    einheitTextView.setText(einheit);

                    gotAmount.setText(String.valueOf(einkaufszettelElement.getAnzahlBekommen()));

                    ImageButton saveButton = view2.findViewById(R.id.saveButton);

                    // beim Klicken des Save-Symbols werden die Daten gespeichert
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view3) {
                            String bekommeneMenge = gotAmount.getText().toString();

                            if (Integer.parseInt(bekommeneMenge) != 0) {
                                einkaufszettelElement.setAnzahlBekommen(Integer.parseInt(bekommeneMenge));
                                Toast toast = Toast.makeText(context, "Gespeichert!", Toast.LENGTH_SHORT);
                                toast.show();

                            }
                            parentLayout.removeView(view2);
                            j--;
                        }
                    });
                }
            });

            return view;
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.empty_view, viewGroup, false);
            return view;
        }


    }
}


