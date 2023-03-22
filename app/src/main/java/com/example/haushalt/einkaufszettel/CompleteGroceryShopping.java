package com.example.haushalt.einkaufszettel;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.haushalt.EinkaufszettelElement;
import com.example.haushalt.Fach;
import com.example.haushalt.Food;
import com.example.haushalt.Gefrierschrank;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.R;
import com.example.haushalt.UnitsAndCategories;
import com.example.haushalt.activity.EditOnFocusChangeListener;
import com.example.haushalt.activity.ObjectCollections;
import com.example.haushalt.data.HaushaltContract;
import com.example.haushalt.fragments_gefrierschrank.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

class CompleteGroceryShopping {

    static LinkedList<GefrierschrankElement> boughtItemsIntoHome;
    private static LinkedList<EinkaufszettelElement> boughtNotIntoHome;

    private static PopupWindow popupWindow;
    private static HashMap<Integer, Integer> idsEinkaufszettel = new HashMap<>();
    private static GefrierschrankElement elementOnFocus;
    private static EditText editTextOnFocus;

    private static ArrayAdapter<EinkaufszettelElement> einfuegeSpinnerAdapter;
    private static Gefrierschrank gefrierschrank;

    private static BaseAdapter einkaufBaseAdapter;

    public static void onClick(View view, Context context, BaseAdapter baseAdapter) {
        boughtItemsIntoHome = getBoughtItems();
        openPopUpWindow(view, context);
        einkaufBaseAdapter = baseAdapter;
    }

    //es werden die gekauften und schonmal im Gefrierschrank gelagerten Elemente in einer Menge zurueckgegeben
    static LinkedList<GefrierschrankElement> getBoughtItems() {
        LinkedList<GefrierschrankElement> boughtItems = new LinkedList<>();
        LinkedList<EinkaufszettelElement> einkaufszettelElements = ObjectCollections.getEinkaufszettelElements();
        LinkedList<Food> foodLinkedList = ObjectCollections.getEssensliste();
        boughtNotIntoHome = new LinkedList<>();
        boughtNotIntoHome.add(new EinkaufszettelElement(new Food(-1, "Auswaehlen des Elementes", -1,-1,null,0,-1,-1, -1), -1));

        for (EinkaufszettelElement einkaufszettelElement : einkaufszettelElements) {
            if (einkaufszettelElement.getAnzahlBekommen() > 0) {
                for (Food food : foodLinkedList) {
                    if (einkaufszettelElement.getFood() == food && food.getStoredInPast() == 1) {
                        GefrierschrankElement gefrierschrankElement1 = new GefrierschrankElement(einkaufszettelElement.getFood(), einkaufszettelElement.getAnzahlBekommen(), einkaufszettelElement.getFood().getLastFach());
                        boughtItems.add(gefrierschrankElement1);
                    } else if(einkaufszettelElement.getFood() == food && food.getStoredInPast() == 0){
                        boughtNotIntoHome.add(einkaufszettelElement);
                    }
                }
            }
        }
        return boughtItems;
    }

    static void openPopUpWindow(View view, final Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = (View) inflater.inflate(R.layout.einkauf_abschliessen, null);

        int width = (int) (view.getWidth() * 0.9);
        int height = (int) (view.getHeight() * 0.95);

        popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        final LinearLayout linearLayout = popupView.findViewById(R.id.list_einkauf_abschliessen);

        inflateLinearLayout(linearLayout, context);

        //in diesem Spinner werden alle Food-Elemente gesammelt, die nicht als Vorschlag genannt werden, einzuraeumen
        final Spinner einfuegeSpinner = popupView.findViewById(R.id.gefrierschrank_einfuege_spinner);
        einfuegeSpinnerAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, boughtNotIntoHome);
        einfuegeSpinner.setAdapter(einfuegeSpinnerAdapter);

        einfuegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0) {
                    EinkaufszettelElement einkaufszettelElement = (EinkaufszettelElement) adapterView.getSelectedItem();
                    GefrierschrankElement gefrierschrankElement = new GefrierschrankElement(einkaufszettelElement.getFood(), einkaufszettelElement.getAnzahlBekommen(),
                            ObjectCollections.getOptimalFach(einkaufszettelElement.getFood().getKategorie_id()) == null ? ObjectCollections.getFaecher().getFirst() : ObjectCollections.getOptimalFach(einkaufszettelElement.getFood().getKategorie_id()));
                    boughtItemsIntoHome.add(gefrierschrankElement);
                    inflateLinearLayout(gefrierschrankElement, linearLayout, context);
                    boughtNotIntoHome.remove(einkaufszettelElement);
                    einfuegeSpinnerAdapter.notifyDataSetChanged();
                    einfuegeSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // falls die Anzeige beendet werden soll ohne Auswirkungen auf den Einkaufszettel und den Gefrierschrank
        Button abbrechen = (Button) popupView.findViewById(R.id.abbrechen_button);
        abbrechen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boughtNotIntoHome.clear();
                popupWindow.dismiss();
            }
        });


        Button nichtsEinfuegen = (Button) popupView.findViewById(R.id.nichts_einfuegen_button);
        nichtsEinfuegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGroceryList(context);
                popupWindow.dismiss();
                einkaufBaseAdapter.notifyDataSetChanged();
            }
        });

        /*
            die angegebenen Sachen auf der Anzeige werden in den Gefrierschrank eingefuegt
            der Einkaufszettel wird aktualisiert gemaess dem Eingekauften (NICHT dem Eingeraeumten!!!)
         */
        final Button einrauemen = (Button) popupView.findViewById(R.id.einfuegen_button_einkauf_abschliessen);
        einrauemen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Insert/Update the database with the current list
                if(editTextOnFocus != null){
                    editTextOnFocus.clearFocus();
                }
                updateFreezer(context);
                updateGroceryList(context);
                popupWindow.dismiss();
                einkaufBaseAdapter.notifyDataSetChanged();
            }
        });
    }

    // in das PopUp werden hier die Daten eingespeist
    private static void inflateLinearLayout(final LinearLayout linearLayout, Context context) {
        Iterator<GefrierschrankElement> it = boughtItemsIntoHome.iterator();

        // einfuegen der Daten in die Text-/EditViews
        while (it.hasNext()) {
            final GefrierschrankElement gefrierschrankElement = it.next();

            inflateLinearLayout(gefrierschrankElement, linearLayout, context);
        }


    }

    private static void inflateLinearLayout(final GefrierschrankElement gefrierschrankElement, final LinearLayout linearLayout, final Context context){
        final View view1 = LayoutInflater.from(context).inflate(R.layout.einkauf_abschliessen_element, linearLayout, false);
        linearLayout.addView(view1);

        final TextView elementName = (TextView) view1.findViewById(R.id.element_name);
        elementName.setText(gefrierschrankElement.getFood().getEssensname());

        final EditText anzahl = (EditText) view1.findViewById(R.id.anzahl_edit_text);
        anzahl.setText(String.valueOf(gefrierschrankElement.getAnzahl()));

        //wenn die Anzahl geaendert wird, soll dies im Objekt gespeichert werden
        anzahl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    int i = Integer.parseInt(anzahl.getText().toString());
                    gefrierschrankElement.setAnzahl(i);
                } else {
                    elementOnFocus = gefrierschrankElement;
                    editTextOnFocus = anzahl;
                }
            }
        });

        // Einheitenspinner mHv der HashMap units
        final Spinner einheit = (Spinner) view1.findViewById(R.id.einheit_spinner_einkauf_abschliessen);
        ArrayAdapter<String> einheitAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, UnitsAndCategories.getNames(UnitsAndCategories.unit));
        einheit.setAdapter(einheitAdapter);

        // setzen der Einheit im Spinner
        int i = 0;
        for (String unit : UnitsAndCategories.getNames(UnitsAndCategories.unit)) {
            if (unit.equals(UnitsAndCategories.getUnit(gefrierschrankElement.getFood().getEinheit_id()))) {
                einheit.setSelection(i);
                break;
            }
            i++;
        }

        //bei aendern der Einheit wird Objekt geandert, vor allem muss das essen uU neu erstellt werden
        einheit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //bei initialisieren des Spinners ist die Einheit noch die Gleiche
            boolean check = false;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                if (check) {
                    gefrierschrankElement.setEinheitChanged(true);
                }
                //gibt es schon essensname und einheit kombination als food
                Food food = ObjectCollections.getFoodIDBy(elementName.getText().toString(), UnitsAndCategories.getID(UnitsAndCategories.unit, einheit.getSelectedItem().toString()));
                if (food == null) {
                    food = new Food(elementName.getText().toString(),
                            UnitsAndCategories.getID(UnitsAndCategories.unit,
                                    einheit.getSelectedItem().toString()),
                            gefrierschrankElement.getFood().getKategorie_id(),
                            0, 0, 0, 0);
                    ObjectCollections.insertFood(food, true);
                }
                gefrierschrankElement.setFood(food);
                check = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //FachArray fuer den Spinner
        LinkedList<Integer> fachList = new LinkedList<>();
        for (int i1 = 1; i1 <= gefrierschrankElement.getFach().getGefrierschrank().getAnzahlFaecher(); i1++) {
            fachList.add(i1);
        }

        // zum Aendern des zu einraeumenden Faches:
        final Spinner fach = (Spinner) view1.findViewById(R.id.fach_spinner);
        final ArrayAdapter<Integer> fachAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, fachList);
        fach.setAdapter(fachAdapter);
        fach.setSelection(gefrierschrankElement.getFach().getFachNummer() - 1);

        // gefrierschrank Spinner initialisieren
        final Spinner gefrierschraenke = (Spinner) view1.findViewById(R.id.gefrierschrankSpinner);
        LinkedList<Gefrierschrank> gefrierschranks = ObjectCollections.getGefrierschraenke();
        String[] gefrierschraenkeArray = new String[gefrierschranks.size()];
        int k = 0;
        for(Gefrierschrank gefrierschrank : gefrierschranks){
            gefrierschraenkeArray[k] = gefrierschrank.getLabel();
            k++;
        }
        ArrayAdapter<String> gefrierschrankAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, gefrierschraenkeArray);
        gefrierschraenke.setAdapter(gefrierschrankAdapter);
        String gefrierschrank_label = gefrierschrankElement.getFach().getGefrierschrank().getLabel();
        for(int l = 0; l < gefrierschraenkeArray.length; l++){
            if(gefrierschraenkeArray[l].equals(gefrierschrank_label)){
                gefrierschraenke.setSelection(l);
                break;
            }
        }



        fach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                int fachNummer = pos + 1;
                gefrierschrank = ObjectCollections.getGefrierschrankBy((String) gefrierschraenke.getSelectedItem());
                gefrierschrankElement.setFach(ObjectCollections.getFachBy(fachNummer, gefrierschrank));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        final LinearLayout durabilityLinLayout = view1.findViewById(R.id.durabilityLinLayout);
        CheckBox durabilityCheck = view1.findViewById(R.id.durabilityCheckBox);
        final EditText durabilityYears = view1.findViewById(R.id.durability_years_einkauf);
        final EditText durabilityMonths = view1.findViewById(R.id.durability_months_einkauf);
        final EditText durabilityDays = view1.findViewById(R.id.durability_days_einkauf);

        durabilityCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    durabilityYears.setVisibility(View.VISIBLE);
                    durabilityMonths.setVisibility(View.VISIBLE);
                    durabilityDays.setVisibility(View.VISIBLE);
                    ObjectCollections.addedFood.remove(gefrierschrankElement.getFood().getId());
                } else {
                    durabilityYears.setVisibility(View.GONE);
                    durabilityMonths.setVisibility(View.GONE);
                    durabilityDays.setVisibility(View.GONE);
                    switch(gefrierschrank.getTemperature()){
                        case HaushaltContract.temperatureFreezer:
                            gefrierschrankElement.getFood().setDurabilityFreezer(0);
                            break;
                        case HaushaltContract.temperatureFridge:
                            gefrierschrankElement.getFood().setDurabilityFridge(0);
                            break;
                        case HaushaltContract.temperatureRoom:
                            gefrierschrankElement.getFood().setDurabilityRoomTemperature(0);
                            break;
                    }
                    ObjectCollections.addedFood.add(gefrierschrankElement.getFood().getId());
                    ObjectCollections.essenslisteChanged = true;
                }
            }
        });


        EditOnFocusChangeListener focusChangeListener = new EditOnFocusChangeListener(
                durabilityYears,
                durabilityMonths,
                durabilityDays,
                gefrierschrankElement,
                gefrierschrankElement.getFach().getGefrierschrank().getTemperature(),
                1);
        durabilityYears.setOnFocusChangeListener(focusChangeListener);
        durabilityMonths.setOnFocusChangeListener(focusChangeListener);
        durabilityMonths.setOnFocusChangeListener(focusChangeListener);

        // falls ein anderes Element ausgewaehlt wird, soll auch der FaecherSpinner geandert werden
        gefrierschraenke.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gefrierschrank = ObjectCollections.getGefrierschrankBy((String) gefrierschraenke.getSelectedItem());

                LinkedList<Integer> fachList = new LinkedList<>();
                for (int i1 = 1; i1 <= gefrierschrank.getAnzahlFaecher(); i1++) {
                    fachList.add(i1);
                }

                fachAdapter.clear();
                fachAdapter.addAll(fachList);
                fachAdapter.notifyDataSetChanged();

                fach.setSelection(0);

                Fach currentFach = ObjectCollections.getFachBy(Integer.parseInt(fach.getSelectedItem().toString()), gefrierschrank);
                gefrierschrankElement.setFach(currentFach);

                // falls noch keine Haltbarkeit fuer den geanderten Geraetetypen festgelegt wurde, muss dies gemacht werden
                switch (gefrierschrank.getTemperature()){
                    case HaushaltContract.temperatureFreezer:
                        if(gefrierschrankElement.getFood().getDurabilityFreezer() == -1){
                            Toast.makeText(context, "Definiere dazu noch die Haltbarkeit im Gefrierschrank", Toast.LENGTH_SHORT).show();
                            durabilityLinLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                    case HaushaltContract.temperatureFridge:
                        if(gefrierschrankElement.getFood().getDurabilityFridge() == -1){
                            Toast.makeText(context, "Definiere dazu noch die Haltbarkeit im Kühlschrank", Toast.LENGTH_SHORT).show();
                            durabilityLinLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                    case HaushaltContract.temperatureRoom:
                        if(gefrierschrankElement.getFood().getDurabilityRoomTemperature() == -1){
                            Toast.makeText(context, "Definiere dazu noch die Haltbarkeit im Freien", Toast.LENGTH_SHORT).show();
                            durabilityLinLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //falls ein Element nicht in den Gefrierschrank oder aehnliches eingeraeumt werden soll
        ImageButton deleteButton = (ImageButton) view1.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boughtItemsIntoHome.remove(gefrierschrankElement);
                EinkaufszettelElement einkaufszettelElement = new EinkaufszettelElement(gefrierschrankElement.getFood(), gefrierschrankElement.getAnzahl(), gefrierschrankElement.getAnzahl());
                boughtNotIntoHome.add(einkaufszettelElement);
                einfuegeSpinnerAdapter.notifyDataSetChanged();
                linearLayout.removeView(view1);
            }
        });

    }


    private static void updateFreezer(Context context) {
        for (GefrierschrankElement gefrierschrankElement : boughtItemsIntoHome) {
            Date currentDate = new Date();
            gefrierschrankElement.setDate(currentDate);
            gefrierschrankElement.getFood().setStoredInPast(1);
            ObjectCollections.addToFreezer(gefrierschrankElement, true);

            // setting an alarm for the push notifications on the durability of the element
            long timeOfAlarm = 0;
            switch (gefrierschrankElement.getFach().getGefrierschrank().getTemperature()){
                case HaushaltContract.temperatureFreezer:
                    timeOfAlarm = gefrierschrankElement.getDate().getTime() + (long) gefrierschrankElement.getFood().getDurabilityFreezer() * 1000 * 24 * 60 * 60;
                    break;
                case HaushaltContract.temperatureFridge:
                    timeOfAlarm = gefrierschrankElement.getDate().getTime() + (long) gefrierschrankElement.getFood().getDurabilityFridge() * 1000 * 24 * 60 * 60;
                    break;
                case HaushaltContract.temperatureRoom:
                    timeOfAlarm = gefrierschrankElement.getDate().getTime() + (long) gefrierschrankElement.getFood().getDurabilityRoomTemperature() * 1000 * 24 * 60 * 60;
                    break;
                default:
                    break;
            }
            if(timeOfAlarm > 0){
                Utils.setAlarm(context, timeOfAlarm, gefrierschrankElement.getId(), gefrierschrankElement.getFood().getEssensname(), gefrierschrankElement.getFach().getGefrierschrank().getLabel(), gefrierschrankElement.getFach().getFachNummer());
            }
        }
    }

    private static void updateGroceryList(Context context) {
        LinkedList<EinkaufszettelElement> einkaufszettelElements = ObjectCollections.getEinkaufszettelElements();

        for(Iterator<EinkaufszettelElement> it = einkaufszettelElements.iterator(); it.hasNext();) {
            EinkaufszettelElement einkaufszettelElement = it.next();
            if (einkaufszettelElement.getAnzahlBekommen() > 0) {
                einkaufszettelElement.setAnzahlWunsch(einkaufszettelElement.getAnzahlWunsch() - einkaufszettelElement.getAnzahlBekommen());
                if (einkaufszettelElement.getAnzahlWunsch() == 0) {
                    it.remove();
                    ObjectCollections.einkaufszettelChanged = true;
                    ObjectCollections.removedEinkaufsElt.add(einkaufszettelElement.getId());
                    if(ObjectCollections.addedEinkaufsElt.contains(einkaufszettelElement.getId())){
                        ObjectCollections.addedEinkaufsElt.remove(einkaufszettelElement.getId());
                    }
                }
            }
        }
        einkaufBaseAdapter.notifyDataSetChanged();

    }

    public static void setEditTextOnFocus(EditText editText){
        editTextOnFocus = editText;
    }

    /*private static class EditOnFocusChangeListener implements View.OnFocusChangeListener {

        EditText yearsEditText;
        EditText monthsEditText;
        EditText daysEditText;
        GefrierschrankElement gefrierschrankElement;

        public EditOnFocusChangeListener(EditText years, EditText months, EditText days, GefrierschrankElement gefrierschrankElement){
            this.yearsEditText = years;
            this.monthsEditText = months;
            this.daysEditText = days;
            this.gefrierschrankElement = gefrierschrankElement;
        }

        @Override
        public void onFocusChange(View view, boolean focus) {
            if(!focus){
                int years = yearsEditText.getText().toString().equals("") ? 0 : Integer.valueOf(yearsEditText.getText().toString());
                int months = monthsEditText.getText().toString().equals("") ? 0 : Integer.valueOf(monthsEditText.getText().toString());
                int days = daysEditText.getText().toString().equals("") ? 0 : Integer.valueOf(daysEditText.getText().toString());
                int durability = years * 360 + months * 30 + days;
                switch (gefrierschrank.getTemperature()){
                    case HaushaltContract.temperatureFreezer:
                        gefrierschrankElement.getFood().setDurabilityFreezer(durability);
                        break;
                    case HaushaltContract.temperatureFridge:
                        gefrierschrankElement.getFood().setDurabilityFridge(durability);
                        break;
                    case HaushaltContract.temperatureRoom:
                        gefrierschrankElement.getFood().setDurabilityRoomTemperature(durability);
                        break;
                }
                ObjectCollections.addedFood.add(gefrierschrankElement.getFood().getId());
                ObjectCollections.essenslisteChanged = true;
            } else {
                editTextOnFocus = (EditText) view;
            }
        }
    } */
}
