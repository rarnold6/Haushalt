package com.example.haushalt.suche;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.haushalt.EinkaufszettelElement;
import com.example.haushalt.FragmentChangeListener;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.R;
import com.example.haushalt.activity.MainActivity;
import com.example.haushalt.activity.ObjectCollections;
import com.example.haushalt.data.HaushaltContract;
import com.example.haushalt.einkaufszettel.EinkaufszettelFragment;
import com.example.haushalt.fragments_gefrierschrank.FachFragment;
import com.example.haushalt.fragments_gefrierschrank.GefrierschrankFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SuchFragment extends Fragment {

    private FragmentChangeListener fragmentChangeListener;
    private List<String> kategorien = new ArrayList<>();

    private boolean gefrierschrankSuche = true;

    public static SuchFragment newInstance() {
        
        Bundle args = new Bundle();
        
        SuchFragment fragment = new SuchFragment();
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
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.such_fragment, container, false);
        final AutoCompleteTextView sucheingabe = rootView.findViewById(R.id.suche_auto_complete);
        sucheingabe.requestFocus();

        Button kategorien = rootView.findViewById(R.id.category_all);
        kategorien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GefrierschrankFragment gefrierschrankFragment = GefrierschrankFragment.newInstance(false, null);
                fragmentChangeListener.addFragment(gefrierschrankFragment);
            }
        });

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Suche");

        //Automatisches Oeffnen der Tastatur
        final InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        addAutoCompleteAdapter(sucheingabe);

        final Switch switchSuche = (Switch) rootView.findViewById(R.id.switch_suche);
        switchSuche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //falls switchButton umgelegt wird, soll der Text und das Attribut geaendert werden
                if(isChecked){
                    switchSuche.setText("Einkaufszettel");
                    gefrierschrankSuche = false;
                } else {
                    switchSuche.setText("Daheim");
                    gefrierschrankSuche = true;
                }
                addAutoCompleteAdapter(sucheingabe);
            }
        });


        sucheingabe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(gefrierschrankSuche){
                    GefrierschrankElement gefrierschrankElement = (GefrierschrankElement) adapterView.getAdapter().getItem(i);
                    int fachnummer = gefrierschrankElement.getFach().getFachNummer();

                    sucheingabe.clearFocus();
                    imgr.hideSoftInputFromWindow(sucheingabe.getWindowToken(), 0);

                    FachFragment fachFragment = FachFragment.newInstance(gefrierschrankElement.getFach(), gefrierschrankElement);
                    fragmentChangeListener.replaceFragment(fachFragment);
                } else {
                    EinkaufszettelElement einkaufszettelElement = (EinkaufszettelElement) adapterView.getAdapter().getItem(i);

                    sucheingabe.clearFocus();
                    imgr.hideSoftInputFromWindow(sucheingabe.getWindowToken(), 0);

                    EinkaufszettelFragment einkaufszettelFragment = EinkaufszettelFragment.newInstance(einkaufszettelElement);
                    fragmentChangeListener.replaceFragment(einkaufszettelFragment);
                }
            }
        });



        return rootView;
    }

    private void addAutoCompleteAdapter(AutoCompleteTextView sucheingabe){
        if (gefrierschrankSuche) {
            AutoFillBaseAdapter autoFillBaseAdapter = new AutoFillBaseAdapter(getContext(), ObjectCollections.getGefrierschrankElements());
            sucheingabe.setAdapter(autoFillBaseAdapter);
        } else {
            AutoFillBaseAdapter autoFillBaseAdapter = new AutoFillBaseAdapter(getContext(), ObjectCollections.getEinkaufszettelElements());
            sucheingabe.setAdapter(autoFillBaseAdapter);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.setGroupVisible(R.id.menu_group, false);
        super.onPrepareOptionsMenu(menu);
    }
}

