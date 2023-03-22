package com.example.haushalt.fragments_gefrierschrank;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.haushalt.Fach;
import com.example.haushalt.FragmentChangeListener;
import com.example.haushalt.Gefrierschrank;
import com.example.haushalt.UnitsAndCategories;
import com.example.haushalt.activity.GefrierschrankBaseAdapter;
import com.example.haushalt.R;
import com.example.haushalt.activity.ObjectCollections;
import com.example.haushalt.initializing.AddFreezerFragment;
import com.example.haushalt.suche.SuchFragment;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

public class GefrierschrankFragment extends Fragment {

    private static final int GEFRIERSCHRANK_LOADER = 0;
    private static final String GEFRIERSCHRANK_ID = "GEFRIERSCHRANK_ID";

    private Button addButton;
    private Button addAnotherFreezer;
    private ListView faecherListView;

    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    private static int anzahlFaecher;
    BaseAdapter adapter;
    private FragmentChangeListener fragmentChangeListener;
    private Gefrierschrank gefrierschrank;

    // bei true wird Gefrierschrank-Uebersicht angezeigt, sonst Sortieruebersicht
    public static boolean gefrierschrankOverview = true;

    private GefrierschrankFragment(boolean gefrierschrankOverview){
        this.gefrierschrankOverview = gefrierschrankOverview;
    }

    // erzeugt neue Instanz und gibt Fragment zurück
    public static GefrierschrankFragment newInstance(boolean gefrierschrankOverview, Gefrierschrank gefrierschrank) {
        GefrierschrankFragment fragment = new GefrierschrankFragment(gefrierschrankOverview);
        Bundle args = new Bundle();
        args.putLong(GEFRIERSCHRANK_ID, gefrierschrank != null ? gefrierschrank.getId() : -1);
        fragment.setArguments(args);
        return fragment;
    }


    // die MainActivity wird hier der Instanz fragmentChangeListener hinzugefuegt und sichergestellt,
    // dass diese das Interface implementiert
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentChangeListener) {
            fragmentChangeListener = (FragmentChangeListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement FragmentChangeListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().getLong(GEFRIERSCHRANK_ID) != -1){
            this.gefrierschrank = ObjectCollections.getGefrierschrankBy(getArguments().getLong(GEFRIERSCHRANK_ID));
        } else {
            this.gefrierschrank = null;
        }
        setHasOptionsMenu(true);
    }

    /*
        onCreateView werden die Elemente des Fragments/xml initialisiert
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gefrierschrank_fragment, container, false);

        faecherListView = (ListView) rootView.findViewById(R.id.list_view_gefrierschrank);
        final Button sortButton = (Button) rootView.findViewById(R.id.sortieren_button);

        if(gefrierschrank == null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(gefrierschrankOverview) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x1 = motionEvent.getX();
                        case MotionEvent.ACTION_UP:
                            x2 = motionEvent.getX();
                            float delta = x2 - x1;
                            Gefrierschrank gefrierschrank1 = null;
                            if (delta > MIN_DISTANCE) {
                                gefrierschrank1 = findGefrierschrank(false);
                            } else if (-1 * delta > MIN_DISTANCE) {
                                gefrierschrank1 = findGefrierschrank(true);
                            }
                            if (gefrierschrank1 != null) {
                                GefrierschrankFragment gefrierschrankFragment = GefrierschrankFragment.newInstance(true, gefrierschrank1);
                                fragmentChangeListener.replaceFragment(gefrierschrankFragment);
                            }

                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                    }
                    return true;
                }
                return false;
            }

            private Gefrierschrank findGefrierschrank(boolean naechsteElement){
                LinkedList<Gefrierschrank> gefrierschranks = ObjectCollections.getGefrierschraenke();
                Gefrierschrank previous = null;
                Iterator<Gefrierschrank> iterator = gefrierschranks.iterator();
                while(iterator.hasNext()){
                    Gefrierschrank gefrierschrank1 = iterator.next();
                    if(gefrierschrank1 == gefrierschrank){
                        if(naechsteElement && iterator.hasNext()){
                            return iterator.next();
                        } else if(!naechsteElement && previous != null){
                            return previous;
                        }
                    }
                    previous = gefrierschrank1;
                }
                return null;
            }
        });

        if(gefrierschrankOverview){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(gefrierschrank.getLabel());
            sortButton.setText("Kategorien");


        } else if(gefrierschrank != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Kategorien im " + gefrierschrank.getLabel());
            sortButton.setText("Gefriere");
        } else {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Kategorien");
            sortButton.setVisibility(View.GONE);
        }
        insertDataInListView();
        
        // wenn ein Fach angeklickt wird, soll dieses geoeffnet werden
        faecherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(gefrierschrankOverview) {
                    int fachNummer = position + 1;
                    Fach fach = ObjectCollections.getFachBy(fachNummer, gefrierschrank);
                    FachFragment fachFragment = FachFragment.newInstance(fach, null);
                    fragmentChangeListener.addFragment(fachFragment);

                } else {
                    long categoryID = position + 1;
                    CategoryFragment categoryFragment = CategoryFragment.newInstance(categoryID, gefrierschrank);
                    fragmentChangeListener.addFragment(categoryFragment);
                }
            }
        });

        //bei Druecken des Buttons, soll die Liste geandert werden, sowie die Beschriftungen
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gefrierschrankOverview) {
                    gefrierschrankOverview = false;
                    getActivity().invalidateOptionsMenu();
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Kategorien im " + gefrierschrank.getLabel());
                    sortButton.setText("Gefriere");

                } else {
                    gefrierschrankOverview = true;
                    getActivity().invalidateOptionsMenu();
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(gefrierschrank.getLabel());
                    sortButton.setText("Kategorien");

                }
                insertDataInListView();
            }
        });
        

        return rootView;
    }

    private void insertDataInListView(){
        if(gefrierschrankOverview){
            LinkedList<Fach> gefrierschrankFaecher = new LinkedList<>();
            for(Fach fach : ObjectCollections.getFaecherOf(gefrierschrank)){
                gefrierschrankFaecher.add(fach);
            }
            anzahlFaecher = gefrierschrankFaecher.size();
            adapter = new GefrierschrankBaseAdapter(getContext(), gefrierschrankFaecher);
        } else {
            //Loswerden von "Kategorie hinzufuegen" und "Bitte auswaehlen"
            String[] categories = UnitsAndCategories.getNames(UnitsAndCategories.category);
            String[] categoryArray = new String[categories.length-2];
            for(int i = 1; i < categories.length-1; i++){
                categoryArray[i-1] = categories[i];
            }
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, categoryArray);
        }
        faecherListView.setAdapter((ListAdapter) adapter);

    }



    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if(!gefrierschrankOverview){
            menu.findItem(R.id.menu_add_element).setVisible(false);
            menu.setGroupVisible(R.id.menu_group, false);
        } else {
            menu.findItem(R.id.menu_add_element).setVisible(false);
            menu.setGroupVisible(R.id.menu_group, true);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                SuchFragment suchFragment = SuchFragment.newInstance();
                fragmentChangeListener.replaceFragment(suchFragment);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
            case R.id.menu_new_object:
                fragmentChangeListener.replaceFragment(AddFreezerFragment.newInstance(null));
                return true;
            case R.id.menu_add_case:
                Fach fach = new Fach(++anzahlFaecher, gefrierschrank);
                ObjectCollections.addFach(fach, true);
                insertDataInListView();
                return true;
            case R.id.menu_object_settings:
                fragmentChangeListener.addFragment(AddFreezerFragment.newInstance(gefrierschrank));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        diese Methoden sind dazu da, dass automatisch die Daten aus der Datenbank aktualisiert
        angezeigt werden

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                HaushaltContract.FaecherEntry.FAECHER_ID,
                HaushaltContract.FaecherEntry.FAECHER_FACH,
                HaushaltContract.FaecherEntry.FAECHER_BESCHRIFTUNG
        };

        CursorLoader cl = new CursorLoader(getActivity(),
                HaushaltContract.FaecherEntry.CONTENT_URI_FAECHER,
                projection,
                null,
                null,
                null);
        Cursor c = cl.loadInBackground();
        this.anzahlFaecher = c.getCount();
        return cl;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ((GefrierschrankBaseAdapter) this.adapter).swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ((GefrierschrankBaseAdapter) this.adapter).swapCursor(null);
    }
    */


    public static int getAnzahlFaecher() {
        return anzahlFaecher;
    }


}
