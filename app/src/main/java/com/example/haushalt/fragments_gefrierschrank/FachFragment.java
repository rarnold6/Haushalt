package com.example.haushalt.fragments_gefrierschrank;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;

import com.example.haushalt.Fach;
import com.example.haushalt.FragmentChangeListener;
import com.example.haushalt.Gefrierschrank;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.R;
import com.example.haushalt.activity.ObjectCollections;

public class FachFragment extends Fragment {

    private static final String FACHID = "FACHID";
    private static final String FACHNUMMER = "Fachnummer";
    private static final String FOCUSELEMENTID = "FOCUSELEMENTID";
    private static final String GEFRIERSCHRANKID = "GEFRIERSCHRANKID";


    private static final int FACH_LOADER = 1;
    private Fach fach;
    private int fachNummer;
    private Gefrierschrank gefrierschrank;
    private TextView header;
    private Button einfuegen;
    private ImageButton backButton;
    FachBaseAdapter baseAdapter;
    private long focusElementID;
    private FragmentChangeListener fragmentChangeListener;

    public FachFragment(){
    }

    // generiert das auf der Gefrierschrank-Page angeklickte Fach
    public static FachFragment newInstance(Fach fach, GefrierschrankElement focusElement) {
        Bundle args = new Bundle();
        args.putLong(FACHID, fach.getId());
        //args.putInt(FACHNUMMER, fach.getFachNummer());
        //args.putLong(GEFRIERSCHRANKID, fach.getGefrierschrank_id());
        if(focusElement != null) {
            args.putLong(FOCUSELEMENTID, focusElement.getId());
        } else {
            args.putLong(FOCUSELEMENTID, 0);
        }
        FachFragment fragment = new FachFragment();
        fragment.setArguments(args);
        return fragment;
    }

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
        // weist dem Attribut die aktuelle Fachnummer zu
        if(getArguments() != null) {
            this.fach = ObjectCollections.getFachBy(getArguments().getLong(FACHID));
            this.fachNummer = fach.getFachNummer();
            this.gefrierschrank = fach.getGefrierschrank();
            this.focusElementID = getArguments().getLong(FOCUSELEMENTID);
        }
        setHasOptionsMenu(true);
    }

    // hier wird die Page wirklich erstellt!
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fach_fragment, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(this.fachNummer + ".Fach");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GefrierschrankElement focusElement = ObjectCollections.getGefrierschrankEltByID(this.focusElementID);

        // die Liste mHv dem LoaderManager mit Daten aus der Datenbank fuettern
        ListView objectList = (ListView) rootView.findViewById(R.id.objectList);
        this.baseAdapter = new FachBaseAdapter(getActivity(), fach, ObjectCollections.getGefrierschrankElements(), focusElement);
        objectList.setAdapter(this.baseAdapter);

        if(this.focusElementID != 0){
            for(int position = 0; position < objectList.getAdapter().getCount(); position++){
                if(focusElement == objectList.getAdapter().getItem(position)){
                    objectList.setSelection(position);
                }
            }
        }

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.setGroupVisible(R.id.menu_group, false);
        menu.findItem(R.id.menu_add_element).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_element:
                EditFragment editFragment = EditFragment.newInstance(fach, false);
                fragmentChangeListener.addFragment(editFragment);
                return true;
            case android.R.id.home:
                GefrierschrankFragment gefrierschrankFragment = GefrierschrankFragment.newInstance(true, gefrierschrank);
                fragmentChangeListener.replaceFragment(gefrierschrankFragment);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
