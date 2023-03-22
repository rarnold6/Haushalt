package com.example.haushalt;

import com.example.haushalt.activity.ObjectCollections;

import java.util.LinkedList;

public class Gefrierschrank {
    private long _id;
    private String label;
    private int anzahlFaecher;
    // row temperature: 0 means freezer, 1 means fridge and 2 means room temperature
    private int temperature;

    public Gefrierschrank(long _id, String label, int anzahlFaecher, int temperature){
        this._id = _id;
        this.label = label;
        this.anzahlFaecher = anzahlFaecher;
        this.temperature = temperature;
    }

    public Gefrierschrank(String label, int anzahlFaecher, int temperature){
        findID();
        this.label = label;
        this.anzahlFaecher = anzahlFaecher;
        this.temperature = temperature;
    }

    private void findID(){
        LinkedList<Gefrierschrank> gefrierschraenke = ObjectCollections.getGefrierschraenke();
        this._id = gefrierschraenke.size()+1;
        A: for(int i = 1; i <= gefrierschraenke.size(); i++){
            for(Gefrierschrank gefrierschrank : gefrierschraenke){
                if(gefrierschrank.getId() == i){
                    continue A;
                }
                if(gefrierschraenke.getLast() == gefrierschrank){
                    this._id = i;
                    break A;
                }
            }
        }
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getAnzahlFaecher() {
        return anzahlFaecher;
    }

    public void setAnzahlFaecher(int anzahlFaecher) {
        this.anzahlFaecher = anzahlFaecher;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
