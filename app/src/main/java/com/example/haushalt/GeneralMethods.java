package com.example.haushalt;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.haushalt.data.HaushaltContract;

public class GeneralMethods {

    //Methode, um zu checken, ob die gesuchte Einheit schon in dem Table "einheiten" gespeichert ist
    public static long checkUnits(Context context, String unit){
        long einheitID;

        //Abfrage des Tables "einheiten" nach der gesuchten Einheit
        Cursor cursor = context.getContentResolver().query(HaushaltContract.EinheitenEntry.CONTENT_URI_EINHEITEN,
                new String[]{HaushaltContract.EinheitenEntry.EINHEITEN_ID},
                HaushaltContract.EinheitenEntry.EINHEITEN_EINHEIT + " = '" + unit + "'",
                null,
                null);
        /*
            falls die Einheit existiert, liefert der Cursor (mindestens) ein Ergebnis
            die ID der gefundenen Einheit wird zurueckgegeben
         */
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            einheitID = cursor.getLong(cursor.getColumnIndex(HaushaltContract.EinheitenEntry.EINHEITEN_ID));
        }
            // ansonsten wird ein neuer Eintrag gemacht und dessen ID zurueckgegeben
            else {
            ContentValues values = new ContentValues();
            values.put(HaushaltContract.EinheitenEntry.EINHEITEN_EINHEIT, unit);
            Uri uriResult = context.getContentResolver().insert(HaushaltContract.EinheitenEntry.CONTENT_URI_EINHEITEN, values);
            einheitID = ContentUris.parseId(uriResult);
        }
        return einheitID;
    }

    /*
        ueberpruefen, ob das uebergebene Essen schon in dem Table "essen" abgespeichert ist
        falls schon vorhanden, dann wird die ID zurueckgegeben, ansonsten wird -1 zurueckgegeben
     */
    public static long checkFood(Context context, String food, long unitID){



        String[] projection = new String[]{HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_ID};
        String selection = HaushaltContract.EssenEntry.ESSEN_ESSENSNAME + " = '" + food +
                "' AND " + HaushaltContract.EssenEntry.ESSEN_EINHEIT + " = " + unitID;
        Cursor cursor = context.getContentResolver().query(HaushaltContract.EssenEntry.CONTENT_URI_ESSEN,
                projection,
                selection,
                null,
                null);
        if(cursor.getCount() == 0){
            return -1;
        } else {
            cursor.moveToFirst();
            return cursor.getLong(cursor.getColumnIndex(HaushaltContract.EssenEntry.ESSEN_ID));
        }
    }

    /*
        einfuegen von Essen in das Table "essen"
     */
    public static long insertFood(Context context, String food, long unitID, long categoryID){
        ContentValues values = new ContentValues();
        values.put(HaushaltContract.EssenEntry.ESSEN_ESSENSNAME, food);
        values.put(HaushaltContract.EssenEntry.ESSEN_EINHEIT, unitID);
        values.put(HaushaltContract.EssenEntry.ESSEN_KATEGORIE, categoryID);
        Uri uri = context.getContentResolver().insert(HaushaltContract.EssenEntry.CONTENT_URI_ESSEN, values);
        return ContentUris.parseId(uri);
    }

    public static long insertOrUpdateFood(Context context, Food food){
        ContentValues values = new ContentValues();
        values.put(HaushaltContract.EssenEntry.ESSEN_ID, food.getId());
        values.put(HaushaltContract.EssenEntry.ESSEN_ESSENSNAME, food.getEssensname());
        values.put(HaushaltContract.EssenEntry.ESSEN_EINHEIT, food.getEinheit_id());
        values.put(HaushaltContract.EssenEntry.ESSEN_KATEGORIE, food.getEinheit_id());

        values.put(HaushaltContract.EssenEntry.ESSEN_LASTFACH, food.getLastFach().getFachNummer());
        Uri uri = context.getContentResolver().insert(HaushaltContract.EssenEntry.CONTENT_URI_ESSEN, values);
        return ContentUris.parseId(uri);
    }

    /*
        einfuegen des Essen mHv dessen ID in die Tabelle "gefrierschrank"
     */
    public static long fillFreezer(Context context, long foodID, int number, int compartment){
        ContentValues values = new ContentValues();
        values.put(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ESSENID, foodID);
        values.put(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ANZAHL, number);
        values.put(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_FACH, compartment);

        Uri uri = context.getContentResolver().insert(HaushaltContract.GefrierschrankElementeEntry.CONTENT_URI_GEFRIERSCHRANKELEMENTE, values);
        return ContentUris.parseId(uri);
    }
}
