package com.example.haushalt.initializing;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.haushalt.Fach;
import com.example.haushalt.FragmentChangeListener;
import com.example.haushalt.Gefrierschrank;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.R;
import com.example.haushalt.activity.EditOnFocusChangeListener;
import com.example.haushalt.activity.ObjectCollections;
import com.example.haushalt.data.HaushaltContract;
import com.example.haushalt.fragments_gefrierschrank.EditFragment;
import com.example.haushalt.fragments_gefrierschrank.FachFragment;
import com.example.haushalt.fragments_gefrierschrank.GefrierschrankFragment;

import org.w3c.dom.Text;

import java.util.LinkedList;

public class AddFreezerFragment extends Fragment {

    private static final String OBJECT_ID = "object_id";
    private FragmentChangeListener fragmentChangeListener;
    private String[] labels;
    private EditText editTextOnFocus;
    private static EditText editDurabilityOnFocus;

    private long objectID;
    private Gefrierschrank object;
    private Fach[] faecher;

    private AddFreezerFragment(){}

    public static AddFreezerFragment newInstance(Gefrierschrank object){
        Bundle args = new Bundle();
        args.putLong(OBJECT_ID, object != null ? object.getId() : -1);
        AddFreezerFragment addFreezerFragment = new AddFreezerFragment();
        addFreezerFragment.setArguments(args);
        return addFreezerFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.objectID = getArguments().getLong(OBJECT_ID);
        if(context instanceof FragmentChangeListener) {
            fragmentChangeListener = (FragmentChangeListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement FragmentChangeListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.new_freezer_definition_fragment,container, false);

        TextView headline = (TextView) rootView.findViewById(R.id.deinNeuerGefrierschrankTextView);

        final EditText freezerName = (EditText) rootView.findViewById(R.id.freezerNameEdit);

        final EditText freezerNumberCases = (EditText) rootView.findViewById(R.id.freezerNumberCases);

        final CheckBox defineLabels = (CheckBox) rootView.findViewById(R.id.checkbox_labels);

        Button addFreezer = (Button) rootView.findViewById(R.id.addFreezerButton);

        final Spinner deviceType = rootView.findViewById(R.id.device_type);
        deviceType.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Gefrierschrank", "Kühlschrank", "Raumtemperatur"}));


        //set the data of the object that the user wants to edit into the text- and editViews
        if(objectID != -1){
            this.object = ObjectCollections.getGefrierschrankBy(objectID);
            headline.setText("Ändern von " + this.object.getLabel());
            freezerName.setText(this.object.getLabel());
            freezerNumberCases.setText(String.valueOf(this.object.getAnzahlFaecher()));
            this.faecher = ObjectCollections.getFaecherOf(this.object);

            deviceType.setSelection(this.object.getTemperature());
            addFreezer.setText("Ändern");
        }

        //if the user wants to define labels for the object, the linear layout gets inflated
        final LinearLayout linearLayoutLabels = (LinearLayout) rootView.findViewById(R.id.list_labels_cases);

        defineLabels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    int numberOfCases = Integer.parseInt(freezerNumberCases.getText().toString());
                    labels = new String[numberOfCases];
                    for(int i = 0; i < labels.length; i++){
                        labels[i] = faecher[i].getBeschriftung();
                    }
                    inflateLinearLayoutLabels(linearLayoutLabels, numberOfCases);
                } else {
                    linearLayoutLabels.removeAllViews();
                }
            }
        });

        if(objectID != -1){
            for(Fach fach : this.faecher){
                if(!fach.getBeschriftung().equals("")){
                    defineLabels.setChecked(true);
                    break;
                }
            }
        }

        //if the user changes the number of cases while the checkBox isChecked
        freezerNumberCases.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int currentLength) {
                if(currentLength > 0) {
                    int numberOfCases = Integer.parseInt(charSequence.toString());
                    String[] newLabels = new String[numberOfCases];
                    for(int j = 0; j < newLabels.length; j++){
                        try{
                            newLabels[j] = labels[j];
                        } catch(IndexOutOfBoundsException e){
                            break;
                        }
                    }
                    labels = newLabels;
                    if (defineLabels.isChecked()) {
                        linearLayoutLabels.removeAllViews();
                        inflateLinearLayoutLabels(linearLayoutLabels, numberOfCases);
                    }

                    //if there is food in cases
                    if (objectID != -1 && numberOfCases < faecher.length) {
                        try {
                            checkFoodInCases(numberOfCases);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            private void checkFoodInCases(int numberOfCases) throws InterruptedException {
                while(numberOfCases < faecher.length){
                    LinkedList<GefrierschrankElement> gefrierschrankElements = ObjectCollections.getGefrierschrankElementsIn(faecher[numberOfCases]);
                    if(gefrierschrankElements.size() != 0){
                        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                        final View popupView = (View) inflater.inflate(R.layout.popup_elements_in_case, null);

                        // festlegen der Groesse des PopUps
                        int width = (int) (rootView.getWidth() * 0.95);
                        int height = (int) (rootView.getHeight() * 0.4);

                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0 ,0);

                        TextView textView = popupView.findViewById(R.id.text_view_warning_of_deleting);
                        textView.setText("Achtung!\nIn Fach " + (numberOfCases + 1) + " sind noch Elemente drin." +
                                "\nWenn Sie den Button 'Ändern' später drücken, werden diese Elemente gelöscht!");

                        Button goToCaseButton = popupView.findViewById(R.id.button_to_case);
                        final int finalNumberOfCases = numberOfCases + 1;
                        goToCaseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Fach fach = ObjectCollections.getFachBy(finalNumberOfCases, object);
                                FachFragment fachFragment = FachFragment.newInstance(fach, null);
                                fragmentChangeListener.replaceFragment(fachFragment);
                                popupWindow.dismiss();
                            }
                        });

                        Button acceptDeletion = popupView.findViewById(R.id.delete_accept);
                        acceptDeletion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupWindow.dismiss();
                            }
                        });
                        break;
                    }
                    numberOfCases++;
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        deviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean secondSelection = false;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int position, long l) {
                if(objectID != -1 && secondSelection){
                    final LinkedList<GefrierschrankElement> gefrierschrankElements = ObjectCollections.getGefrierschrankElementsIn(object, position);
                    if(gefrierschrankElements != null && gefrierschrankElements.size() != 0){
                        getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                        final View popupView = (View) inflater.inflate(R.layout.popup_change_temperature, null);

                        // festlegen der Groesse des PopUps
                        int width = (int) (rootView.getWidth() );
                        int height = (int) (rootView.getHeight() );

                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0 ,0);

                        TextView textView = popupView.findViewById(R.id.text_view_headline_change_temperature);
                        textView.setText("Folgende Elemente haben kein Haltbarkeitsdatum");

                        LinearLayout linearLayout = popupView.findViewById(R.id.linear_layout_change_temperature);

                        for(GefrierschrankElement gefrierschrankElement : gefrierschrankElements){
                            final View view1 = LayoutInflater.from(getContext()).inflate(R.layout.popup_change_temp_element, linearLayout, false);
                            linearLayout.addView(view1);

                            TextView foodName = view1.findViewById(R.id.text_view_food_name);
                            foodName.setText(gefrierschrankElement.getFood().getEssensname());
                            EditText editYear = view1.findViewById(R.id.durability_years_change_temp);
                            EditText editMonths = view1.findViewById(R.id.durability_months_change_temp);
                            EditText editDays = view1.findViewById(R.id.durability_days_change_temp);

                            EditOnFocusChangeListener focusChangeListener = new EditOnFocusChangeListener(
                                    editYear,
                                    editMonths,
                                    editDays,
                                    gefrierschrankElement,
                                    position,
                                    0);

                            editYear.setOnFocusChangeListener(focusChangeListener);
                            editMonths.setOnFocusChangeListener(focusChangeListener);
                            editDays.setOnFocusChangeListener(focusChangeListener);
                        }

                        Button exitButton = popupView.findViewById(R.id.button_exit_change_durability);
                        exitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                secondSelection = false;
                                deviceType.setSelection(object.getTemperature());
                                popupWindow.dismiss();
                            }
                        });

                        Button confirmButton = popupView.findViewById(R.id.button_change_durability);
                        confirmButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editDurabilityOnFocus.clearFocus();

                                for(GefrierschrankElement gefrierschrankElement : gefrierschrankElements){
                                    switch (position){
                                        case HaushaltContract.temperatureFreezer:
                                            if(gefrierschrankElement.getFood().getDurabilityFreezer() == -1){
                                                gefrierschrankElement.getFood().setDurabilityFreezer(0);
                                            }
                                            break;
                                        case HaushaltContract.temperatureFridge:
                                            if(gefrierschrankElement.getFood().getDurabilityFridge() == -1){
                                                gefrierschrankElement.getFood().setDurabilityFridge(0);
                                            }
                                            break;
                                        case HaushaltContract.temperatureRoom:
                                            if(gefrierschrankElement.getFood().getDurabilityRoomTemperature() == -1){
                                                gefrierschrankElement.getFood().setDurabilityRoomTemperature(0);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    ObjectCollections.addedFood.add(gefrierschrankElement.getFood().getId());
                                    ObjectCollections.essenslisteChanged = true;
                                }
                                popupWindow.dismiss();
                            }
                        });
                    }

                } else if(objectID != -1) {
                    secondSelection = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addFreezer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextOnFocus != null) {
                    editTextOnFocus.clearFocus();
                }

                if(objectID == -1) {
                    Gefrierschrank gefrierschrank = new Gefrierschrank(freezerName.getText().toString(), Integer.parseInt(freezerNumberCases.getText().toString()), (int) deviceType.getSelectedItemId());
                    Fach[] neueFaecher = new Fach[gefrierschrank.getAnzahlFaecher()];
                    for (int i = 1; i <= gefrierschrank.getAnzahlFaecher(); i++) {
                        Fach fach;
                        if (labels[i - 1] == null) {
                            fach = new Fach(i, gefrierschrank);
                        } else {
                            fach = new Fach(i, labels[i - 1], gefrierschrank);
                        }
                        ObjectCollections.addFach(fach, true);
                        neueFaecher[i - 1] = fach;
                    }
                    ObjectCollections.addGefrierschrank(gefrierschrank, true);
                    EditFragment editFragment = new EditFragment(neueFaecher, 0, gefrierschrank);
                    fragmentChangeListener.replaceFragment(editFragment);
                } else {
                    //edit the given object
                    object.setLabel(freezerName.getText().toString());

                    if(object.getAnzahlFaecher() > faecher.length){
                        for(int i = object.getAnzahlFaecher(); i > faecher.length; i--){
                            Fach fach = ObjectCollections.getFachBy(i, object);
                            LinkedList<GefrierschrankElement> gefrierschrankElements = ObjectCollections.getGefrierschrankElementsIn(fach);
                            for(GefrierschrankElement element : gefrierschrankElements){
                                ObjectCollections.removeFromFreezer(element);
                            }
                            ObjectCollections.removeFach(fach);
                        }
                    } else if(object.getAnzahlFaecher() < faecher.length) {
                        for(int i = object.getAnzahlFaecher(); i <= faecher.length; i++){
                            Fach fach = new Fach(i, "", object);
                            ObjectCollections.addFach(fach, true);
                        }
                    }
                    object.setAnzahlFaecher(Integer.parseInt(freezerNumberCases.getText().toString()));

                    if(!defineLabels.isChecked()){
                        for(Fach fach : faecher){
                            fach.setBeschriftung("");
                        }
                    } else {
                        for(int i = 0; i < faecher.length; i++){
                            if(labels[i] != null){
                                faecher[i].setBeschriftung(labels[i]);
                            } else {
                                faecher[i].setBeschriftung("");
                            }
                        }
                    }
                    object.setTemperature((int)deviceType.getSelectedItemId());

                    //to save the changed data
                    ObjectCollections.addGefrierschrank(object, true);

                    GefrierschrankFragment gefrierschrankFragment = GefrierschrankFragment.newInstance(true, object);
                    fragmentChangeListener.replaceFragment(gefrierschrankFragment);
                }
            }
        });
        return rootView;
    }


    private void inflateLinearLayoutLabels(LinearLayout linearLayoutLabels, int numberOfCases) {
        for(int i = 1; i <= numberOfCases; i++){
            inflateLinearLayoutLabel(linearLayoutLabels, i);
        }
    }

    private void inflateLinearLayoutLabel(LinearLayout linearLayoutLabels, final int currentCaseNumber){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.object_label, linearLayoutLabels, false);
        linearLayoutLabels.addView(view);

        TextView textViewLabel = (TextView) view.findViewById(R.id.text_view_label);
        textViewLabel.setText("Beschriftung Fach " + currentCaseNumber + ":");

        final EditText editTextLabel = (EditText) view.findViewById(R.id.edit_text_label);
        editTextLabel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus){
                    labels[currentCaseNumber-1] = editTextLabel.getText().toString();
                } else {
                    editTextOnFocus = editTextLabel;
                }
            }
        });
        if(objectID != -1 && faecher.length >= currentCaseNumber && !faecher[currentCaseNumber-1].getBeschriftung().equals("")){
            editTextLabel.setText(faecher[currentCaseNumber-1].getBeschriftung());
        }

    }


    public static void setEditDurabilityOnFocus(EditText editDurability) {
        editDurabilityOnFocus = editDurability;
    }
}
