package com.example.haushalt.fragments_gefrierschrank;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.haushalt.FragmentChangeListener;
import com.example.haushalt.Gefrierschrank;
import com.example.haushalt.R;
import com.example.haushalt.UnitsAndCategories;
import com.example.haushalt.activity.ObjectCollections;

public class CategoryFragment extends Fragment {

    private static final String CATEGORY_ID ="CategoryID";
    private static final String GEFRIERSCHRANK_ID = "GefrierschrankID";

    private FragmentChangeListener fragmentChangeListener;
    private long categoryID;
    private Gefrierschrank gefrierschrank;
    private TextView header;

    private BaseAdapter baseAdapter;

    public static CategoryFragment newInstance(long categoryID, Gefrierschrank gefrierschrank) {
        Bundle args = new Bundle();
        args.putLong(CATEGORY_ID, categoryID);
        if(gefrierschrank != null){
            args.putLong(GEFRIERSCHRANK_ID, gefrierschrank.getId());
        } else {
            args.putLong(GEFRIERSCHRANK_ID, -1);
        }
        CategoryFragment fragment = new CategoryFragment();
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
        if(getArguments() != null && getArguments().getLong(GEFRIERSCHRANK_ID) != -1) {
            this.categoryID = getArguments().getLong(CATEGORY_ID);
            this.gefrierschrank = ObjectCollections.getGefrierschrankBy(getArguments().getLong(GEFRIERSCHRANK_ID));
        } else {
            this.categoryID = getArguments().getLong(CATEGORY_ID);
            this.gefrierschrank = null;
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fach_fragment, viewGroup, false);

        //Headline in the toolbar and BackButton
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Kategorie: " + UnitsAndCategories.getCategory(this.categoryID));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ListView objectList = (ListView) rootView.findViewById(R.id.objectList);
        this.baseAdapter = new CategoryBaseAdapter(getContext(), categoryID, ObjectCollections.getGefrierschrankElements(), gefrierschrank);
        objectList.setAdapter(this.baseAdapter);
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.menu_object_settings).setVisible(false);
        menu.findItem(R.id.menu_add_case).setVisible(false);
        menu.findItem(R.id.menu_new_object).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                GefrierschrankFragment gefrierschrankFragment = GefrierschrankFragment.newInstance(false, gefrierschrank);
                fragmentChangeListener.replaceFragment(gefrierschrankFragment);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
