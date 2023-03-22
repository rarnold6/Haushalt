package com.example.haushalt;

import androidx.annotation.NonNull;

import com.example.haushalt.activity.ObjectCollections;
import com.example.haushalt.einkaufszettel.EinkaufszettelFragment;

import java.util.LinkedList;

public class EinkaufszettelElement implements Comparable<EinkaufszettelElement>{
    private long id;
    private Food food;
    private int anzahlWunsch;
    private int einkaufsladen;
    private int anzahlBekommen;

    public EinkaufszettelElement(long id, Food food, int anzahlWunsch, int einkaufsladen, int anzahlBekommen) {
        this.id = id;
        this.food = food;
        this.anzahlWunsch = anzahlWunsch;
        this.einkaufsladen = einkaufsladen;
        this.anzahlBekommen = anzahlBekommen;
    }

    public EinkaufszettelElement(){
        this.id = -1;
        this.food = null;
    }

    public EinkaufszettelElement(Food food, int anzahlWunsch){
        findID();
        this.food = food;
        this.anzahlWunsch = anzahlWunsch;
        this.einkaufsladen = 0;
        this.anzahlBekommen = 0;
    }

    public EinkaufszettelElement(Food food, int anzahlWunsch, int anzahlBekommen){
        findID();
        this.food = food;
        this.anzahlWunsch = anzahlWunsch;
        this.einkaufsladen = 0;
        this.anzahlBekommen = anzahlBekommen;
    }

    private void findID(){
        LinkedList<EinkaufszettelElement> einkaufszettelElements = ObjectCollections.getEinkaufszettelElements();
        A: for(long i = 1; i <= einkaufszettelElements.size(); i++) {
            for (EinkaufszettelElement einkaufszettelElement : einkaufszettelElements) {
                if (einkaufszettelElement.getId() == i) {
                    continue A;
                }
                if(einkaufszettelElements.getLast() == einkaufszettelElement){
                    this.id = i;
                    break A;
                }
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public int getAnzahlWunsch() {
        return anzahlWunsch;
    }

    public void setAnzahlWunsch(int anzahlWunsch) {
        this.anzahlWunsch = anzahlWunsch;
    }

    public int getEinkaufsladen() {
        return einkaufsladen;
    }

    public void setEinkaufsladen(int einkaufsladen) {
        this.einkaufsladen = einkaufsladen;
    }

    public int getAnzahlBekommen() {
        return anzahlBekommen;
    }

    public void setAnzahlBekommen(int anzahlBekommen) {
        this.anzahlBekommen = anzahlBekommen;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getFood().toString();
    }

    @Override
    public int compareTo(EinkaufszettelElement einkaufszettelElement) {
        if(this.getFood() == null && einkaufszettelElement.getFood() == null){
            return 0;
        } else if(this.getFood() == null){
            return 1;
        } else if(einkaufszettelElement.getFood() == null){
            return -1;
        }
        return (int) (this.getFood().getKategorie_id() - einkaufszettelElement.getFood().getKategorie_id());
    }
}
