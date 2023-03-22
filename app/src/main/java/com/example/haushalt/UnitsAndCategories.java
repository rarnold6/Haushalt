package com.example.haushalt;

import com.example.haushalt.data.HaushaltContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class UnitsAndCategories {

    private static HashMap<Long, String> units = new HashMap<Long, String>();
    public static boolean unitsChanged = false;
    public static ArrayList<Long> addedUnits = new ArrayList<Long>();
    public static ArrayList<Long> removedUnits = new ArrayList<Long>();

    private static HashMap<Long, String> categories = new HashMap<Long, String>();
    public static boolean categoriesChanged = false;
    public static ArrayList<Long> addedCategories = new ArrayList<Long>();
    public static ArrayList<Long> removedCategories = new ArrayList<Long>();

    public static final int unit = 0;
    public static final int category = 1;

    /*
    public Units(HashMap<Long, String> units){
        this.units = units;
    }
    */
    private UnitsAndCategories(){}



    //gibt ein Array zurueck mit den Einheiten, sowie dem String "Einheit hinzufuegen"
    public static String[] getNames(int i){
        HashMap<Long, String> hashMap;
        switch (i){
            case 0: hashMap = units; break;
            case 1: hashMap = categories; break;
            default: return null;
        }

        String[] nameArray;

        int j = 0;
        if(i == 1){
            nameArray = new String[hashMap.size() + 2];
            nameArray[j] = HaushaltContract.BITTE_WAEHLEN;
            j++;
        } else {
            nameArray = new String[hashMap.size() + 1];
        }
        for(Long id : hashMap.keySet()){
            nameArray[j] = hashMap.get(id);
            j++;
        }
        switch (i){
            case 0: nameArray[j] = HaushaltContract.Einheit_Hinzufuegen; break;
            case 1: nameArray[j] = HaushaltContract.Kategorie_Hinzufuegen; break;
            default: break;
        }

        return nameArray;
    }

    public static String getUnit(Long key){
        return units.get(key);
    }

    public static ArrayList<Long> getAddedUnits(){
        return addedUnits;
    }

    public static ArrayList<Long> getRemovedUnits(){
        return removedUnits;
    }

    public static String getCategory(Long key){
        return categories.get(key);
    }

    public static ArrayList<Long> getAddedCategories(){
        return addedCategories;
    }

    public static ArrayList<Long> getRemovedCategories(){
        return removedCategories;
    }

    // fuegt eine Einheit der HashMap hinzu an passender Stelle und updatet die Listen
    public static long addUnitCategory(int i, String label, boolean nonInitialize){
        HashMap<Long, String> hashMap;
        ArrayList<Long> addedName;
        ArrayList<Long> removedName;
        boolean nameChanged;
        switch (i){
            case 0:
                hashMap = units;
                addedName = addedUnits;
                removedName = removedUnits;
                break;
            case 1:
                hashMap = categories;
                addedName = addedUnits;
                removedName = removedUnits;
                break;
            default: return -1;
        }
        if(hashMap.containsValue(label)){
            return -1;
        }
        long key = findLowestFreeKey(hashMap.keySet());
        hashMap.put(key, label);
        if(nonInitialize) {
            addedName.add(key);
            removedName.remove(key);
            switch(i){
                case 0: unitsChanged = true; break;
                case 1: categoriesChanged = true; break;
                default: break;
            }
        }
        return key;
    }

    //finde freie Stelle in der HashMap
    private static long findLowestFreeKey(Set<Long> keys){
        int i = 1;
        for(Long key : keys){
            if(key != i){
                return i;
            }
            i++;
        }
        return i;
    }

    public static long removeUnit(int i, String label){
        HashMap<Long, String> hashMap;
        ArrayList<Long> addedName;
        ArrayList<Long> removedName;
        boolean nameChanged;
        switch (i){
            case 0:
                hashMap = units;
                addedName = addedUnits;
                removedName = removedUnits;
                break;
            case 1:
                hashMap = categories;
                addedName = addedUnits;
                removedName = removedUnits;
                break;
            default: return 0;
        }
        if(!removable(label)){
            return -1;
        }

        Set<Long> ids = hashMap.keySet();
        for(Long id : ids){
            if(hashMap.get(id).equals(label)){
                hashMap.remove(id);
                switch(i){
                    case 0: unitsChanged = true; break;
                    case 1: categoriesChanged = true; break;
                    default: break;
                }
                removedName.add(id);
                if(addedName.contains(id)){
                    addedName.remove(id);
                }
                return id;
            }
        }
        return -1;
    }

    // NOCH ZU IMPLEMENTIEREN: IST EINHEIT IM GEFRIERSCHRANK/ESSEN/EINKAUFSZETTEL?
    private static boolean removable(String unit){
        return true;
    }

    public static long getID(int i, String label){
        switch (i){
            case 0:
                for(Long id : units.keySet()){
                    if(units.get(id).equals(label)){
                        return id;
                    }
                }
                break;
            case 1:
                for(Long id : categories.keySet()){
                    if(categories.get(id).equals(label)){
                        return id;
                    }
                }
                break;
        }
        return -1;
    }

    public static boolean didUnitsChanged(){
        return unitsChanged;
    }

    public static boolean didCategoriesChanged(){ return categoriesChanged; }


}
