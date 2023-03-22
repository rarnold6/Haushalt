package com.example.haushalt.activity;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.data.HaushaltContract;
import com.example.haushalt.initializing.AddFreezerFragment;

public class EditOnFocusChangeListener implements View.OnFocusChangeListener {

    EditText yearsEditText;
    EditText monthsEditText;
    EditText daysEditText;
    GefrierschrankElement gefrierschrankElement;
    int temperature;
    int classAddOrEdit;

    public EditOnFocusChangeListener(EditText years, EditText months, EditText days, GefrierschrankElement gefrierschrankElement, int temperature, int classAddOrEdit){
        this.yearsEditText = years;
        this.monthsEditText = months;
        this.daysEditText = days;
        this.gefrierschrankElement = gefrierschrankElement;
        this.temperature = temperature;
        this.classAddOrEdit = classAddOrEdit;
    }

    @Override
    public void onFocusChange(View view, boolean focus) {
        if(!focus){
            int years = yearsEditText.getText().toString().equals("") ? 0 : Integer.parseInt(yearsEditText.getText().toString());
            int months = monthsEditText.getText().toString().equals("") ? 0 : Integer.parseInt(monthsEditText.getText().toString());
            int days = daysEditText.getText().toString().equals("") ? 0 : Integer.parseInt(daysEditText.getText().toString());
            int durability = years * 360 + months * 30 + days;
            switch (temperature){
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
            if(this.classAddOrEdit == 0){
                AddFreezerFragment.setEditDurabilityOnFocus((EditText)view);
            } else {

            }

        }
    }
}