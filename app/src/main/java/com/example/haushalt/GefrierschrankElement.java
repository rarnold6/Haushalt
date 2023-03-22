package com.example.haushalt;

import com.example.haushalt.activity.ObjectCollections;

import java.util.Date;
import java.util.LinkedList;

public class GefrierschrankElement {

    private long id;
    private Food food;
    private int anzahl;
    private Fach fach;
    private Date date;

    private boolean einheitChanged = false;

    public GefrierschrankElement(long id, Food food, int anzahl, Fach fach, Date date){
        this.id = id;
        this.food = food;
        this.anzahl = anzahl;
        this.fach = fach;
        this.date = date;

    }

    public GefrierschrankElement(Food food, int anzahl, Fach fach){
        findID();
        this.food = food;
        this.anzahl = anzahl;
        this.fach = fach;
        this.date = new Date();
    }

    private void findID(){
        LinkedList<GefrierschrankElement> gefrierschrankElements = ObjectCollections.getGefrierschrankElements();
        this.id = gefrierschrankElements.size()+1;
        A: for(int i = 1; i <= gefrierschrankElements.size(); i++){
            for(GefrierschrankElement gefrierschrankElement : gefrierschrankElements){
                if(gefrierschrankElement.getId() == i){
                    continue A;
                }
                if(gefrierschrankElements.getLast() == gefrierschrankElement){
                    this.id = i;
                    break A;
                }
            }
        }
    }

    public int getAnzahl() {
        return anzahl;
    }

    public void setAnzahl(int anzahl) {
        this.anzahl = anzahl;
    }

    public Fach getFach() {
        return fach;
    }

    public void setFach(Fach fach) {
        this.fach = fach;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public Food getFood() {
        return this.food;
    }

    public void setEinheitChanged(boolean einheitChanged) {
        this.einheitChanged = einheitChanged;
    }

    public boolean isEinheitChanged() {
        return einheitChanged;
    }

    @Override
    public String toString(){
        return food.getEssensname() + " " + anzahl + " " + UnitsAndCategories.getUnit(food.getEinheit_id());
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date){
        this.date = date;
    }


}
