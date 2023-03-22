package com.example.haushalt.activity;

import com.example.haushalt.EinkaufszettelElement;
import com.example.haushalt.Fach;
import com.example.haushalt.Food;
import com.example.haushalt.Gefrierschrank;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.UnitsAndCategories;
import com.example.haushalt.data.HaushaltContract;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;


// Klasse, um die Elemente einer Tabelle in einer Liste zu speichern
public class ObjectCollections {
    private ObjectCollections(){}

    public static boolean essenslisteChanged = false;
    public static TreeSet<Long> addedFood = new TreeSet<Long>();
    public static TreeSet<Long> removedFood = new TreeSet<Long>();
    private static LinkedList<Food> essensliste = new LinkedList<Food>();

    public static boolean gefrierschrankContentChanged = false;
    public static TreeSet<Long> addedGefrierschrankElts = new TreeSet<Long>();
    public static TreeSet<Long> removedGefrierschrankElts = new TreeSet<Long>();
    private static LinkedList<GefrierschrankElement> gefrierschrankElements = new LinkedList<GefrierschrankElement>();

    public static boolean einkaufszettelChanged = false;
    private static LinkedList<EinkaufszettelElement> einkaufszettelElements = new LinkedList<EinkaufszettelElement>();
    public static TreeSet<Long> addedEinkaufsElt = new TreeSet<Long>();
    public static TreeSet<Long> removedEinkaufsElt = new TreeSet<Long>();

    public static boolean faecherChanged = false;
    public static TreeSet<Long> addedFach = new TreeSet<Long>();
    public static TreeSet<Long> removedFaecher = new TreeSet<Long>();
    private static LinkedList<Fach> faecher = new LinkedList<Fach>();

    public static boolean gefrierschrankChanged = false;
    public static TreeSet<Long> addedGefrierschrank = new TreeSet<>();
    public static TreeSet<Long> removedGefrierschrank = new TreeSet<>();
    private static LinkedList<Gefrierschrank> gefrierschraenke = new LinkedList<Gefrierschrank>();

    // alle Methoden fuer den Table food
    public static boolean insertFood(Food food, boolean foodChange){
        if(foodChange){
            essenslisteChanged = true;
            addedFood.add(food.getId());
            if(removedFood.contains(food.getId())){
                removedFood.remove(food.getId());
            }
        }
        boolean add = essensliste.add(food);
        return add;
    }

    public static boolean removeFood(Food food){
        essenslisteChanged = true;
        removedFood.add(food.getId());
        if(addedFood.contains(food.getId())){
            addedFood.remove(food.getId());
        }
        return essensliste.remove(food);
    }

    public static LinkedList<Food> getEssensliste(){
        return (LinkedList<Food>) essensliste;

    }

    public static Food getFoodByID(long id){
        for(Food food : essensliste){
            if(food.getId() == id){
                return food;
            }
        }
        return null;
    }

    public static Food getFoodIDBy(String essensname, long einheit_id){
        for(Food food : essensliste){
            if(food.getEinheit_id() == einheit_id && food.getEssensname().equals(essensname)){
                return food;
            }
        }
        return null;
    }

    public static Fach getOptimalFach(long categorieID){
        HashMap<Fach, Integer> count = new HashMap<>();
        for(Fach fach : faecher){
            count.put(fach, 0);
        }
        for(Food food : essensliste){
            if(food.getKategorie_id() == categorieID && food.getLastFach() != null){
                Integer integer = count.get(food.getLastFach());
                integer = integer + 1;
            }
        }
        Fach maxFach = ObjectCollections.getFaecher().getLast();
        for(Fach fach : count.keySet()){
            if(count.get(fach) > count.get(maxFach)){
                maxFach = fach;
            }
        }
        return maxFach;

    }

    // alle Methoden fuer Table gefrierschrank
    public static boolean addToFreezer(GefrierschrankElement gefrierschrankElement, boolean changeFreezer){
        if(changeFreezer){
            gefrierschrankContentChanged = true;
            addedGefrierschrankElts.add(gefrierschrankElement.getId());
            if(removedGefrierschrankElts.contains(gefrierschrankElement.getId())){
                removedGefrierschrankElts.remove(gefrierschrankElement.getId());
            }
        }
        return gefrierschrankElements.add(gefrierschrankElement);
    }

    public static boolean removeFromFreezer(GefrierschrankElement gefrierschrankElement){
        gefrierschrankContentChanged = true;
        removedGefrierschrankElts.add(gefrierschrankElement.getId());
        if(addedGefrierschrankElts.contains(gefrierschrankElement.getId())){
            addedGefrierschrankElts.remove(gefrierschrankElement.getId());
        }
        return gefrierschrankElements.remove(gefrierschrankElement);
    }

    public static LinkedList<GefrierschrankElement> getGefrierschrankElements(){
        return (LinkedList<GefrierschrankElement>) gefrierschrankElements;
    }

    public static LinkedList<GefrierschrankElement> getGefrierschrankElementsIn(Fach fach){
        LinkedList<GefrierschrankElement> gefrierschrankElementsFach = new LinkedList<GefrierschrankElement>();
        for(GefrierschrankElement gefrierschrankElement : gefrierschrankElements){
            if(gefrierschrankElement.getFach() == fach){
                gefrierschrankElementsFach.add(gefrierschrankElement);
            }
        }
        return gefrierschrankElementsFach;
    }

    public static LinkedList<GefrierschrankElement> getGefrierschrankElementsIn(Gefrierschrank gefrierschrank, int changedTemperature){

        LinkedList<GefrierschrankElement> gefrierschrankElements2 = new LinkedList<GefrierschrankElement>();
        Fach[] faecher = ObjectCollections.getFaecherOf(gefrierschrank);
        for(Fach fach : faecher){
            LinkedList<GefrierschrankElement> gefrierschrankElements1 = ObjectCollections.getGefrierschrankElementsIn(fach);
            for(GefrierschrankElement gefrierschrankElement : gefrierschrankElements1){
                switch (changedTemperature){
                    case HaushaltContract.temperatureFreezer:
                        if(gefrierschrankElement.getFood().getDurabilityFreezer() == -1){
                            gefrierschrankElements2.add(gefrierschrankElement);
                        }
                        break;
                    case HaushaltContract.temperatureFridge:
                        if(gefrierschrankElement.getFood().getDurabilityFridge() == -1){
                            gefrierschrankElements2.add(gefrierschrankElement);
                        }
                        break;
                    case HaushaltContract.temperatureRoom:
                        if(gefrierschrankElement.getFood().getDurabilityRoomTemperature() == -1){
                            gefrierschrankElements2.add(gefrierschrankElement);
                        }
                        break;
                    default:
                        return null;
                }
            }
        }
        return gefrierschrankElements2;
    }

    public static GefrierschrankElement getGefrierschrankEltByID(long id){
        for(GefrierschrankElement gefrierschrankElement : gefrierschrankElements){
            if(gefrierschrankElement.getId() == id){
                return gefrierschrankElement;
            }
        }
        return null;
    }

    //alle Methoden fuer Table einkaufszettel
    public static boolean addToShoppingList(EinkaufszettelElement einkaufszettelElement, boolean changeEinkaufszettel){
        if(changeEinkaufszettel){
            einkaufszettelChanged = true;
            addedEinkaufsElt.add(einkaufszettelElement.getId());
            if(removedEinkaufsElt.contains(einkaufszettelElement.getId())){
                removedEinkaufsElt.remove(einkaufszettelElement.getId());
            }
        }
        return einkaufszettelElements.add(einkaufszettelElement);
    }

    public static boolean removeFromShoppingList(EinkaufszettelElement einkaufszettelElement){
        einkaufszettelChanged = true;
        removedEinkaufsElt.add(einkaufszettelElement.getId());
        if (addedEinkaufsElt.contains(einkaufszettelElement.getId())) {
            addedEinkaufsElt.remove(einkaufszettelElement.getId());
        }

        return einkaufszettelElements.remove(einkaufszettelElement);
    }

    public static LinkedList<EinkaufszettelElement> getEinkaufszettelElements(){
        return (LinkedList<EinkaufszettelElement>) einkaufszettelElements;
    }

    public static EinkaufszettelElement getEinkaufszettelEltBy(long id){
        for(EinkaufszettelElement einkaufszettelElement : einkaufszettelElements){
            if(einkaufszettelElement.getId() == id){
                return einkaufszettelElement;
            }
        }
        return null;
    }

    // alle Methoden fuer den Table fach
    public static boolean addFach(Fach fach, boolean changeFaecher){
        if(changeFaecher) {
            faecherChanged = true;
            addedFach.add(fach.getId());
            if (removedFaecher.contains(fach.getId())) {
                removedFaecher.remove(fach.getId());
            }
        }
        return faecher.add(fach);
    }

    public static boolean removeFach(Fach fach){
        faecherChanged = true;
        removedFaecher.add(fach.getId());
        if(addedFach.contains(fach.getId())){
            addedFach.remove(fach.getId());
        }
        return faecher.remove(fach);
    }

    public static LinkedList<Fach> getFaecher(){
        return (LinkedList<Fach>) faecher;
    }

    public static Fach[] getFaecherOf(Gefrierschrank gefrierschrank){
        Fach[] faecher = new Fach[gefrierschrank.getAnzahlFaecher()];
        for(int i = 1; i <= faecher.length; i++){
            faecher[i-1] = getFachBy(i, gefrierschrank);
        }
        return faecher;

    }

    public static Fach getFachBy(long id){
        for(Fach fach : faecher){
            if(fach.getId() == id){
                return fach;
            }
        }
        return null;
    }

    public static Fach getFachBy(int fachNummer, Gefrierschrank gefrierschrank){
        for(Fach fach : faecher){
            if(fach.getFachNummer() == fachNummer && fach.getGefrierschrank().getId() == gefrierschrank.getId()){
                return fach;
            }
        }
        return null;
    }

    // alle Methoden fuer den Table gefrierschraenke

    public static boolean addGefrierschrank(Gefrierschrank gefrierschrank, boolean changeGefrierschrank){
        if(changeGefrierschrank) {
            gefrierschrankChanged = true;
            addedGefrierschrank.add(gefrierschrank.getId());
            if (removedGefrierschrank.contains(gefrierschrank.getId())) {
                removedGefrierschrank.remove(gefrierschrank.getId());
            }
        }
        return gefrierschraenke.add(gefrierschrank);
    }

    public static boolean removeGefrierschrank(Gefrierschrank gefrierschrank){
        gefrierschrankChanged = true;
        removedGefrierschrank.add(gefrierschrank.getId());
        if(addedGefrierschrank.contains(gefrierschrank.getId())){
            addedGefrierschrank.remove(gefrierschrank.getId());
        }
        return gefrierschraenke.remove(gefrierschrank);
    }

    public static Gefrierschrank getGefrierschrankBy(String label){
        for(Gefrierschrank gefrierschrank : gefrierschraenke){
            if(gefrierschrank.getLabel().equals(label)){
                return gefrierschrank;
            }
        }
        return null;
    }

    public static Gefrierschrank getGefrierschrankBy(long _id){
        for(Gefrierschrank gefrierschrank : gefrierschraenke){
            if(gefrierschrank.getId() == _id){
                return gefrierschrank;
            }
        }
        return null;
    }

    public static LinkedList<Gefrierschrank> getGefrierschraenke(){
        return gefrierschraenke;
    }

}
