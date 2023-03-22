package com.example.haushalt.initializing;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.haushalt.FragmentChangeListener;
import com.example.haushalt.R;

public class InitialFragment extends Fragment {

    private FragmentChangeListener fragmentChangeListener;

    private InitialFragment(){}

    public static InitialFragment newInstance(){
        Bundle args = new Bundle();
        InitialFragment initialFragment = new InitialFragment();
        initialFragment.setArguments(args);
        return initialFragment;
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
        View rootView = inflater.inflate(R.layout.no_freezer_fragment,container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");

        ImageButton addFreezer = (ImageButton) rootView.findViewById(R.id.addFreezerToApp);

        addFreezer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddFreezerFragment addFreezerFragment = AddFreezerFragment.newInstance(null);
                fragmentChangeListener.addFragment(addFreezerFragment);
            }
        });

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.menu_add_case).setVisible(false);
        menu.findItem(R.id.menu_object_settings).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_new_object:
                fragmentChangeListener.addFragment(AddFreezerFragment.newInstance(null));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
