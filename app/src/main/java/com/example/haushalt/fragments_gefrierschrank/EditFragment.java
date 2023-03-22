package com.example.haushalt.fragments_gefrierschrank;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.haushalt.EinkaufszettelElement;
import com.example.haushalt.Fach;
import com.example.haushalt.Food;
import com.example.haushalt.FragmentChangeListener;
import com.example.haushalt.Gefrierschrank;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.GeneralMethods;
import com.example.haushalt.R;
import com.example.haushalt.UnitsAndCategories;
import com.example.haushalt.activity.MainActivity;
import com.example.haushalt.activity.ObjectCollections;
import com.example.haushalt.data.DBHelper;
import com.example.haushalt.data.HaushaltContract;
import com.example.haushalt.einkaufszettel.EinkaufszettelFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditFragment extends Fragment {

    private static final String FACHID = "FACHID";
    private static String EINKAUFSZETTELEDIT = "EINKAUFSZETTELEDIT";
    private static final String GEFRIERSCHRANKID = "GEFRIERSCHRANKID";
    private boolean einkaufszettelEdit;


    private static final String FACHNUMMER = "Fachnummer";

    private Button einfuegenButton;
    private Button einfuegenUndAbschliessen;
    private Button zumNaechstenFach;
    private AutoCompleteTextView essensnameEditText;
    private EditText anzahlEditText;
    private AutoCompleteEditBaseAdapter autoCompleteEditBaseAdapter;

    private Spinner einheitSpinner;
    private EditText einheitHinzufuegen;
    private Spinner kategorieSpinner;
    private EditText kategorieHinzufuegen;
    private EditText durabilityYears;
    private EditText durabilityMonths;
    private EditText durabilityDays;
    private CheckBox durabilityCheck;

    private long fach_id;
    private int fachNummer;
    private long gefrierschrank_id;
    private Gefrierschrank gefrierschrank;
    private Fach fach;
    private FragmentChangeListener fragmentChangeListener;
    private Fach[] faecher;
    private int position;
    private boolean freezerInitialize;


    private EditFragment(){
    }

    public EditFragment(Fach[] faecher, int i, Gefrierschrank gefrierschrank){
        this.fachNummer = faecher[i].getFachNummer();
        this.fach = faecher[i];
        this.gefrierschrank_id = gefrierschrank.getId();
        this.gefrierschrank = gefrierschrank;
        this.freezerInitialize = true;
        this.position = i;
        this.faecher = faecher;
        this.einkaufszettelEdit = false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentChangeListener) {
            fragmentChangeListener = (FragmentChangeListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement FragmentChangeListener");
        }
    }

    public static EditFragment newInstance(Fach fach, boolean einkaufszettelEdit) {
        Bundle args = new Bundle();

        args.putBoolean(EINKAUFSZETTELEDIT, einkaufszettelEdit);

        args.putInt(FACHNUMMER, fach == null ? 0 : fach.getFachNummer());

        args.putLong(FACHID, fach == null ? 0 : fach.getId());

        EditFragment fragment = new EditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            if(getArguments().getLong(FACHID) > 0){
                this.fach = ObjectCollections.getFachBy(getArguments().getLong(FACHID));
                this.fachNummer = this.fach.getFachNummer();
                this.gefrierschrank = this.fach.getGefrierschrank();
                this.gefrierschrank_id = this.gefrierschrank.getId();
            }

            if(fachNummer != 0 && gefrierschrank_id != 0) {
                this.fach = ObjectCollections.getFachBy(this.fachNummer, gefrierschrank);
            }
            this.einkaufszettelEdit = getArguments().getBoolean(EINKAUFSZETTELEDIT);
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.editor_fragment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.durabilityCheck = rootView.findViewById(R.id.durabilityCheckEdit);


        final GridLayout durabilityGrid = rootView.findViewById(R.id.grid_durability);
        this.durabilityCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    durabilityGrid.setVisibility(View.VISIBLE);
                } else {
                    durabilityGrid.setVisibility(View.GONE);
                }
            }
        });
        this.durabilityYears = rootView.findViewById(R.id.durability_years);
        this.durabilityMonths = rootView.findViewById(R.id.durability_months);
        this.durabilityDays = rootView.findViewById(R.id.durability_days);

        this.durabilityYears.setOnFocusChangeListener(new DurabilityFocusChangeListener());
        this.durabilityMonths.setOnFocusChangeListener(new DurabilityFocusChangeListener());
        this.durabilityDays.setOnFocusChangeListener(new DurabilityFocusChangeListener());

        // if durability field has the focus, the default value will be deleted, so that the user can type in directly
        this.durabilityYears.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean inFocus) {
                if(inFocus){
                    durabilityYears.setText("");
                }
            }
        });


        if(einkaufszettelEdit){

            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Hinzufügen zum Einkaufszettel");
            durabilityCheck.setVisibility(View.GONE);
            durabilityGrid.setVisibility(View.GONE);
        } else {

            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Hinzufügen zu Fach " + this.fachNummer);
        }

        this.einfuegenButton = rootView.findViewById(R.id.einfuegenButton);
        this.essensnameEditText = rootView.findViewById(R.id.essensnameEdit);
        this.anzahlEditText = rootView.findViewById(R.id.anzahlEdit);

        this.einheitSpinner = rootView.findViewById(R.id.einheit_spinner_gefrierschrank);
        this.einheitHinzufuegen = rootView.findViewById(R.id.einheit_hinzufuegen);

        this.kategorieSpinner = rootView.findViewById(R.id.spinner_kategorien);
        this.kategorieHinzufuegen = rootView.findViewById(R.id.kategorie_hinzufuegen);

        autoCompleteEditBaseAdapter = new AutoCompleteEditBaseAdapter(getContext(), ObjectCollections.getEssensliste(), this.fachNummer != 0);
        this.essensnameEditText.setAdapter(autoCompleteEditBaseAdapter);

        this.einfuegenUndAbschliessen = rootView.findViewById(R.id.einfuegenUndAbschliessen);

        this.einfuegenUndAbschliessen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String essensname = addItem();

                if(essensname == null || !essensname.equals("")) {
                    //Rueckkehr auf die Seite mit der Fachuebersicht
                    if (einkaufszettelEdit) {
                        EinkaufszettelFragment einkaufszettelFragment = EinkaufszettelFragment.newInstance(null);
                        fragmentChangeListener.replaceFragment(einkaufszettelFragment);
                    } else if (freezerInitialize) {
                        GefrierschrankFragment gefrierschrankFragment = GefrierschrankFragment.newInstance(true, gefrierschrank);
                        fragmentChangeListener.replaceFragment(gefrierschrankFragment);
                    } else {
                        FachFragment fachFragment = FachFragment.newInstance(fach, null);
                        fragmentChangeListener.replaceFragment(fachFragment);
                    }
                }

            }
        });

        this.zumNaechstenFach = rootView.findViewById(R.id.zumNaechstenFacheinfuegen);
        if(this.freezerInitialize){
            this.zumNaechstenFach.setVisibility(View.VISIBLE);
        }

        this.zumNaechstenFach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String essensname = addItem();

                if(essensname == null || !essensname.equals("")) {
                    if (position < faecher.length - 1) {
                        //zum naechsten Fach gehen
                        ++position;
                        fach = faecher[position];
                        fachNummer = fach.getFachNummer();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Hinzufügen zu Fach " + fachNummer);

                        //Leeren der Eingaben
                        essensnameEditText.setText("");
                        einheitSpinner.setSelection(0);
                        kategorieSpinner.setSelection(0);
                        anzahlEditText.setText("");
                        durabilityYears.setText("00");
                        durabilityMonths.setText("00");
                        durabilityDays.setText("00");

                    } else {
                        //falls alle Faecher durchlaufen naechstes Fach
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), essensname + "Gefrierschrank fertig eingeräumt!", Toast.LENGTH_SHORT);
                        toast.show();

                        GefrierschrankFragment gefrierschrankFragment = GefrierschrankFragment.newInstance(true, gefrierschrank);
                        fragmentChangeListener.replaceFragment(gefrierschrankFragment);
                    }
                }

            }
        });



        // wenn auf Autocomplete-Vorschlag getippt wird, werden Felder automatisch eingetragen
        this.essensnameEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // geklicktes Essen ermitteln
                Food food = (Food) adapterView.getAdapter().getItem(i);

                // Spinnerauswahl automatisch treffen
                einheitSpinner.setSelection((int) food.getEinheit_id() - 1);
                kategorieSpinner.setSelection((int) food.getKategorie_id());
                //direktes Oeffnen der Tastatur durch Fokus auf EditText
                anzahlEditText.requestFocus();


                int durability = 0;
                //falls EditFragment von Daheim aus aufgerufen wurde, muss auch Haltbarkeit eingetragen werden
                if(fachNummer != 0){
                    switch (gefrierschrank.getTemperature()){
                        case HaushaltContract.temperatureFreezer:
                            durability = food.getDurabilityFreezer();
                            break;
                        case HaushaltContract.temperatureFridge:
                            durability = food.getDurabilityFridge();
                            break;
                        case HaushaltContract.temperatureRoom:
                            durability = food.getDurabilityRoomTemperature();
                            break;
                        default: break;
                    }

                    // falls absichtlich schonmal keine Haltbarkeit definiert wurde
                    if(durability == 0){
                        durabilityCheck.setChecked(false);
                    } else if(durability == -1) { // falls in dem Geraet noch keine Haltbarkeit fuer das Lebensmittel vorhanden ist
                        Toast.makeText(getActivity().getApplicationContext(), "In diesem Geraetetyp ist noch keine Haltbarkeit definiert", Toast.LENGTH_SHORT).show();
                        durabilityCheck.setChecked(true);
                    } else { // sonst wird die frueher definierte Haltbarkeit eingetragen
                        durabilityCheck.setChecked(true);
                        int durabilityYear = (durability / 360);
                        durability = durability % 360;
                        int durabilityMonth = (durability / 30);
                        durability = durability % 30;
                        durabilityYears.setText(String.valueOf(durabilityYear));
                        durabilityMonths.setText(String.valueOf(durabilityMonth));
                        durabilityDays.setText(String.valueOf(durability));
                    }
                }


            }
        });

        initUnitSpinner();


        this.einheitSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(einheitSpinner.getSelectedItem().equals(HaushaltContract.Einheit_Hinzufuegen)){
                    einheitHinzufuegen.setVisibility(View.VISIBLE);
                } else {
                    einheitHinzufuegen.setVisibility(View.GONE);
                }
                //einheitSpinner.getSelectedItem().equals(items)
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initCategorySpinner();


        this.kategorieSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(kategorieSpinner.getSelectedItem().equals(HaushaltContract.Kategorie_Hinzufuegen)){
                    kategorieHinzufuegen.setVisibility(View.VISIBLE);
                } else {
                    kategorieHinzufuegen.setVisibility(View.GONE);
                }
                //einheitSpinner.getSelectedItem().equals(items)
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        /*
            Dateneintraege werden durch Hinzufuegen erweitert oder neue angelegt
         */
        this.einfuegenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String essensname = addItem();

                    if(essensname != null && !essensname.equals("")) {
                        //Leeren der Eingaben
                        essensnameEditText.setText("");
                        einheitSpinner.setSelection(0);
                        kategorieSpinner.setSelection(0);
                        anzahlEditText.setText("");
                        if(!einkaufszettelEdit) {
                            durabilityYears.setText("00");
                            durabilityMonths.setText("00");
                            durabilityDays.setText("00");
                        }
                    }

                }

        });

        return rootView;
    }

    private String addItem() {
        // auslesen der eingegebenen Essensdaten und speichern als Variable

        String essensname = essensnameEditText.getText().toString();
        int anzahl = anzahlEditText.getText().toString().equals("") ? 0 : Integer.parseInt(anzahlEditText.getText().toString());
        String einheit;
        String kategorie;



        //falls eine neue Einheit eingespeichert wird, wird diese der HashMap units hinzugefuegt
        if (einheitHinzufuegen.getVisibility() == View.VISIBLE) {
            einheit = einheitHinzufuegen.getText().toString();
            UnitsAndCategories.addUnitCategory(UnitsAndCategories.unit, einheit, true);
        } else {
            einheit = String.valueOf(einheitSpinner.getSelectedItem());
        }

        //falls eine neue Kategorie eingespeichert wird, wird diese der HashMap categories hinzugefuegt
        if (kategorieHinzufuegen.getVisibility() == View.VISIBLE) {
            kategorie = kategorieHinzufuegen.getText().toString();
            UnitsAndCategories.addUnitCategory(UnitsAndCategories.category, kategorie, true);
        } else {
            kategorie = String.valueOf(kategorieSpinner.getSelectedItem());
        }

        // es wird zuerst geprueft, ob in jedes Feld etwas eingetragen wird
        if (!essensname.equals("") && anzahl != 0 && !einheit.equals("")) {
                    /*
                        ueberpruefen, ob die Einheit bzw. das Essen schon in dem
                        Table "einheiten" bzw. "essen" vorhanden ist,
                        speichern von dessen ID
                     */
            long einheitID = UnitsAndCategories.getID(UnitsAndCategories.unit, einheit);

            if (einheitID == -1) {
                einheitID = UnitsAndCategories.addUnitCategory(UnitsAndCategories.unit, einheit, true);
            }

            long kategorieID = UnitsAndCategories.getID(UnitsAndCategories.category, kategorie);

            if (kategorieID == -1) {
                kategorieID = UnitsAndCategories.getID(UnitsAndCategories.category, "Sonstiges");
            }
            int durabilityCategory = -1;
            if(!einkaufszettelEdit) {

                if (this.durabilityCheck.isChecked()) {

                    //speichern der Haltbarkeit in Tagen
                    String durabilityYears = this.durabilityYears.getText().toString();
                    String durabilityMonths = this.durabilityMonths.getText().toString();
                    String durabilityDays = this.durabilityDays.getText().toString();

                    String matchString = "0.";
                    if (durabilityYears.matches(matchString)) {
                            durabilityYears = String.valueOf(durabilityYears.charAt(1));
                    }
                    if (durabilityMonths.matches(matchString)) {
                        durabilityMonths = String.valueOf(durabilityMonths.charAt(1));
                    }
                    if (durabilityDays.matches(matchString)) {
                        durabilityDays = String.valueOf(durabilityDays.charAt(1));
                    }

                    // durability EditTexts can be "", therefore single try/catch solutions
                    try {
                        durabilityCategory = 360 * Integer.parseInt(durabilityYears);
                    } catch (NumberFormatException nfe) {
                        durabilityCategory = 0;
                        Toast.makeText(getActivity().getApplicationContext(), "Keine valide Angabe für die Haltbarkeit", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        durabilityCategory += 30 * Integer.parseInt(durabilityMonths);
                    } catch (NumberFormatException nfe){
                        Toast.makeText(getActivity().getApplicationContext(), "Keine valide Angabe für die Haltbarkeit", Toast.LENGTH_SHORT).show();
                    }
                    try{
                        durabilityCategory += Integer.parseInt(durabilityDays);
                    } catch(NumberFormatException nfe){
                        Toast.makeText(getActivity().getApplicationContext(), "Keine valide Angabe für die Haltbarkeit", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    durabilityCategory = 0;
                }
            }

            Food food = ObjectCollections.getFoodIDBy(essensname, einheitID);

            // falls es noch kein Essen mit dem Essennsnamen und der Einheit gibt, wird neues angelegt
            if (food == null) {
                int storedInPast = einkaufszettelEdit ? 0 : 1;
                if(einkaufszettelEdit){
                    food = new Food(essensname, einheitID, kategorieID, storedInPast, -1, -1, -1);
                } else {
                    switch (this.gefrierschrank.getTemperature()) {
                        case HaushaltContract.temperatureFreezer:
                            food = new Food(essensname, einheitID, kategorieID, fach, storedInPast, durabilityCategory, -1, -1);
                            break;
                        case HaushaltContract.temperatureFridge:
                            food = new Food(essensname, einheitID, kategorieID, fach, storedInPast, -1, durabilityCategory, -1);
                            break;
                        case HaushaltContract.temperatureRoom:
                            food = new Food(essensname, einheitID, kategorieID, fach, storedInPast, -1, -1, durabilityCategory);
                            break;
                        default:
                            break;
                    }
                }

                ObjectCollections.insertFood(food, true);
                autoCompleteEditBaseAdapter.addElement(food);

            } else if(!einkaufszettelEdit){
                /*
                    ansonsten wird, falls die Haltbarkeit geandert wurde, dies entsprechend im Essen gespeichert (erstmals fuer alle Elemente, die unter dem Essen im Schrank sind)
                 */



                // ermitteln der Haltbarkeit vor moeglicher Aenderung
                int durabilityBefore = 0;
                switch(gefrierschrank.getTemperature()){
                    case HaushaltContract.temperatureFreezer:
                        durabilityBefore = food.getDurabilityFreezer();
                        break;
                        case HaushaltContract.temperatureFridge:
                            durabilityBefore = food.getDurabilityFridge();
                            break;
                    case HaushaltContract.temperatureRoom:
                        durabilityBefore = food.getDurabilityRoomTemperature();
                        break;
                    default: break;
                }
                //falls Haltbarkeit geandert wurde, soll dies angepasst werden
                if(durabilityCategory != durabilityBefore){
                    switch(gefrierschrank.getTemperature()){
                        case HaushaltContract.temperatureFreezer:
                            food.setDurabilityFreezer(durabilityCategory);
                            break;
                        case HaushaltContract.temperatureFridge:
                            food.setDurabilityFridge(durabilityCategory);
                            break;
                        case HaushaltContract.temperatureRoom:
                            food.setDurabilityRoomTemperature(durabilityCategory);
                            break;
                        default: break;
                    }
                }
            }

            //einfuegen des Essens in den "gefrierschrank" oder den "einkaufszettel"
            if (einkaufszettelEdit) {
                EinkaufszettelElement einkaufszettelElement = new EinkaufszettelElement(food, anzahl);
                ObjectCollections.addToShoppingList(einkaufszettelElement, true);

                // ToastMessage fuer das erfolgreiche Einfuegen
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), essensname + " erfolgreich eingefügt", Toast.LENGTH_SHORT);
                toast.show();

                return einkaufszettelElement.getFood().getEssensname();
            } else {
                GefrierschrankElement gefrierschrankElement = new GefrierschrankElement(food, anzahl, fach);
                ObjectCollections.addToFreezer(gefrierschrankElement, true);

                // ToastMessage fuer das erfolgreiche Einfuegen
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), essensname + " erfolgreich eingefügt", Toast.LENGTH_SHORT);
                toast.show();

                long datecurrent = gefrierschrankElement.getDate().getTime();
                if(durabilityCategory > 0){
                    long timeOfAlarm = gefrierschrankElement.getDate().getTime() + (long) durabilityCategory * 1000 * 60 * 60 * 24;
                    // zum Testen:
                    //long timeOfAlarm = gefrierschrankElement.getDate().getTime() + durabilityCategory * 1000 * 10;
                    Utils.setAlarm(getContext(), timeOfAlarm, gefrierschrankElement.getId(), gefrierschrankElement.getFood().getEssensname(), gefrierschrankElement.getFach().getGefrierschrank().getLabel(), gefrierschrankElement.getFach().getFachNummer());
                    // setAlarm(gefrierschrankElement, durabilityCategory);

                }
                return gefrierschrankElement.getFood().getEssensname();
            }
        }// sobald eine Angabe fehlt, wird eine ToastMessage angezeigt
            else if(essensname.equals("") && anzahl == 0 && einheit.equals("g")) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Es wurde nichts eingefügt", Toast.LENGTH_LONG);
            toast.show();
            return null;
        } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Angabe nicht vollständig", Toast.LENGTH_LONG);
                toast.show();
                return "";
            }
        }

    // fuellen des Spinners mit Einheiten
    private void initUnitSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, UnitsAndCategories.getNames(UnitsAndCategories.unit));
        einheitSpinner.setAdapter(adapter);
    }

    private void initCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, UnitsAndCategories.getNames(UnitsAndCategories.category));
        kategorieSpinner.setAdapter(adapter);
    }

    private void addAutoCompleteAdapter(){
        AutoCompleteEditBaseAdapter autoCompleteEditBaseAdapter = new AutoCompleteEditBaseAdapter(getContext(), ObjectCollections.getEssensliste(), this.fachNummer != 0);
        this.essensnameEditText.setAdapter(autoCompleteEditBaseAdapter);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.setGroupVisible(R.id.menu_group, false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (einkaufszettelEdit) {
                    EinkaufszettelFragment einkaufszettelFragment = EinkaufszettelFragment.newInstance(null);
                    fragmentChangeListener.replaceFragment(einkaufszettelFragment);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } else if (freezerInitialize) {
                    GefrierschrankFragment gefrierschrankFragment = GefrierschrankFragment.newInstance(true, gefrierschrank);
                    fragmentChangeListener.replaceFragment(gefrierschrankFragment);
                } else {
                    FachFragment fachFragment = FachFragment.newInstance(fach, null);
                    fragmentChangeListener.replaceFragment(fachFragment);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DurabilityFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean onFocus) {
            if(onFocus){
                ((EditText) view).setText("");
            }
        }
    }
}
