package com.example.haushalt;

import androidx.annotation.NonNull;

import com.example.haushalt.activity.ObjectCollections;

import java.util.LinkedList;

public class Food {
    private long id;
    private String essensname;
    private long einheit_id;
    private long kategorie_id;
    private Fach lastFach;
    private int storedInPast;
    private int durabilityFreezer;
    private int durabilityFridge;
    private int durabilityRoomTemperature;

    public Food(long id, String essensname, long einheit_id, long kategorie_id, Fach lastFach, int storedInPast, int durabilityFreezer, int durabilityFridge, int durabilityRoomTemperature){
        this.id = id;
        this.essensname = essensname;
        this.einheit_id = einheit_id;
        this.kategorie_id = kategorie_id;
        this.lastFach = lastFach;
        this.storedInPast = storedInPast;
        this.durabilityFreezer = durabilityFreezer;
        this.durabilityFridge = durabilityFridge;
        this.durabilityRoomTemperature = durabilityRoomTemperature;
    }

    public Food(String essensname, long einheit_id, long kategorie_id, int storedInPast, int durabilityFreezer, int durabilityFridge, int durabilityRoomTemperature){
        findID();
        this.essensname = essensname;
        this.einheit_id = einheit_id;
        this.kategorie_id = kategorie_id;
        this.lastFach = null;
        this.storedInPast = storedInPast;
        this.durabilityFreezer = durabilityFreezer;
        this.durabilityFridge = durabilityFridge;
        this.durabilityRoomTemperature = durabilityRoomTemperature;
    }

    public Food(String essensname, long einheit_id, long kategorie_id, Fach fach, int storedInPast, int durabilityFreezer, int durabilityFridge, int durabilityRoomTemperature){
        findID();
        this.essensname = essensname;
        this.einheit_id = einheit_id;
        this.kategorie_id = kategorie_id;
        this.lastFach = fach;
        this.storedInPast = storedInPast;
        this.durabilityFreezer = durabilityFreezer;
        this.durabilityFridge = durabilityFridge;
        this.durabilityRoomTemperature = durabilityRoomTemperature;
    }

    private void findID(){
        LinkedList<Food> essensliste = ObjectCollections.getEssensliste();
        this.id = essensliste.size()+1;
        A: for(int i = 1; i <= essensliste.size(); i++){
            for(Food food : essensliste){
                if(food.getId() == i){
                    continue A;
                }
                if(essensliste.getLast() == food){
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

    public String getEssensname() {
        return essensname;
    }

    public void setEssensname(String essensname) {
        this.essensname = essensname;
    }

    public long getEinheit_id() {
        return einheit_id;
    }

    public void setEinheit_id(long einheit_id) {
        this.einheit_id = einheit_id;
    }

    public long getKategorie_id() {
        return kategorie_id;
    }

    public void setKategorie_id(long kategorie_id) {
        this.kategorie_id = kategorie_id;
    }

    public Fach getLastFach() {
        return lastFach;
    }

    public void setLastFach(Fach lastFach) {
        this.lastFach = lastFach;
        ObjectCollections.essenslisteChanged = true;
        ObjectCollections.addedFood.add(this.id);
    }

    public int getStoredInPast() {
        return storedInPast;
    }

    public void setStoredInPast(int storedInPast) {
        this.storedInPast = storedInPast;
    }

    public int getDurabilityFreezer() {
        return durabilityFreezer;
    }

    public void setDurabilityFreezer(int durabilityFreezer) {
        this.durabilityFreezer = durabilityFreezer;
        ObjectCollections.essenslisteChanged = true;
        ObjectCollections.addedFood.add(this.id);
    }

    public int getDurabilityFridge() {
        return durabilityFridge;
    }

    public void setDurabilityFridge(int durabilityFridge) {
        this.durabilityFridge = durabilityFridge;
        ObjectCollections.essenslisteChanged = true;
        ObjectCollections.addedFood.add(this.id);
    }

    public int getDurabilityRoomTemperature() {
        return durabilityRoomTemperature;
    }

    public void setDurabilityRoomTemperature(int durabilityRoomTemperature) {
        this.durabilityRoomTemperature = durabilityRoomTemperature;
        ObjectCollections.essenslisteChanged = true;
        ObjectCollections.addedFood.add(this.id);
    }

    @NonNull
    @Override
    public String toString() {
        return this.essensname;
    }
}
