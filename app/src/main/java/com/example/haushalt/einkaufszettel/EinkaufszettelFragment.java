package com.example.haushalt.einkaufszettel;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.haushalt.EinkaufszettelElement;
import com.example.haushalt.FragmentChangeListener;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.R;
import com.example.haushalt.activity.ObjectCollections;
import com.example.haushalt.fragments_gefrierschrank.EditFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;

public class EinkaufszettelFragment extends Fragment {

    private static final String FOCUSELEMENTID = "FOCUSELEMENTID";
    private EinkaufszettelBaseAdapter adapter;
    private FragmentChangeListener fragmentChangeListener;

    private Button addStuff;
    private FloatingActionButton addGroceryElement;
    private ViewGroup container;

    private long focusElementID;

    private EinkaufszettelFragment() {
    }

    public static EinkaufszettelFragment newInstance(EinkaufszettelElement einkaufszettelElement) {
        Bundle args = new Bundle();
        EinkaufszettelFragment fragment = new EinkaufszettelFragment();
        if(einkaufszettelElement != null) {
            args.putLong(FOCUSELEMENTID, einkaufszettelElement.getId());
        } else {
            args.putLong(FOCUSELEMENTID, -1);
        }
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
        this.focusElementID = getArguments().getLong(FOCUSELEMENTID);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.einkaufszettel_fragment, container, false);
        this.container = container;

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Einkaufszettel");


        EinkaufszettelElement focusElement = ObjectCollections.getEinkaufszettelEltBy(this.focusElementID);

        ListView einkaufsliste = (ListView) rootView.findViewById(R.id.einkaufsliste);

        adapter = new EinkaufszettelBaseAdapter(getContext(), ObjectCollections.getEinkaufszettelElements(), focusElement);
        einkaufsliste.setAdapter(adapter);

        this.addGroceryElement = (FloatingActionButton) rootView.findViewById(R.id.einkauf_abschließen);
        this.addGroceryElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditFragment editFragment = EditFragment.newInstance(null, true);
                fragmentChangeListener.addFragment(editFragment);
            }
        });


        if(this.focusElementID != -1){
            for(int position = 0; position < einkaufsliste.getAdapter().getCount(); position++){
                if(focusElement == einkaufsliste.getAdapter().getItem(position)){
                    einkaufsliste.setSelection(position);
                }
            }
        }

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.setGroupVisible(R.id.menu_group, false);
        menu.findItem(R.id.menu_complete_grocery).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_complete_grocery:
                CompleteGroceryShopping.onClick(container, getContext(), adapter);


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
