package com.example.haushalt.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Klasse zur Erstellung der Datenbank bei dem ersten Oeffnen der App
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "haushalt.db";
    private static final int DATABASE_VERSION = 1;
    private static final String[] units = {"g","kg","Pack","Stück","Würfel","Leib","Gläser","Flaschen","l", "ml"};
    public static final String[] categories = {"Fleisch", "Fisch", "Milchprodukt", "Gemüse", "Obst", "Süßwaren", "Getränke", "Getreideprodukte", "Sonstiges"};

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     Datenstruktur in der Datenbank:
     Tables: kategorien, einheiten, essen, gefrierschrank (hier wird das Essen
        sowie das zugehoerige Fach drin gespeichert),
        einkaufszettel, faecher
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        String sqlKategorien = "CREATE TABLE kategorien (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "kategorie String NOT NULL);";
        database.execSQL(sqlKategorien);
        for(String item : categories){
            ContentValues contentValues = new ContentValues();
            contentValues.put(HaushaltContract.KategorienEntry.KATEGORIEN_KATEGORIE, item);
            database.insert(HaushaltContract.KategorienEntry.TABLE_KATEGORIEN, null, contentValues);
        }

        String sqlEinheiten = "CREATE TABLE einheiten (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "einheit String NOT NULL);";
        database.execSQL(sqlEinheiten);
        for(String item : units){
            ContentValues contentValues = new ContentValues();
            contentValues.put("einheit", item);
            database.insert(HaushaltContract.EinheitenEntry.TABLE_EINHEITEN, null, contentValues);
        }

        String sqlEssen = "CREATE TABLE essen (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "essensname TEXT NOT NULL, " +
                "einheit_id INTEGER NOT NULL, " +
                "kategorie_id INTEGER, " +
                "lastFach INTEGER DEFAULT 1, " +
                "stored_past INTEGER DEFAULT 0," +
                "durabilityFreezer INTEGER, " +
                "durabilityFridge INTEGER, " +
                "durabilityRoomTemperature INTEGER, " +
                "FOREIGN KEY (einheit_id) references einheiten (_id)," +
                "FOREIGN KEY (kategorie_id) references kategorien (_id))";

        database.execSQL(sqlEssen);

        String sqlGefrierschrankElemente = "CREATE TABLE gefrierschrankElemente (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ref_essen INTEGER NOT NULL, " +
                "anzahl INTEGER NOT NULL, " +
                "fach INTEGER NOT NULL, " +
                "datum TEXT NOT NULL, " +
                "FOREIGN KEY (ref_essen) references essen (_id))";
        database.execSQL(sqlGefrierschrankElemente);

        String sqlEinkaufszettel = "CREATE TABLE einkaufszettel (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ref_essen INTEGER NOT NULL," +
                "anzahlWunsch INTEGER, " +
                "einheiten_id INTEGER, " +
                "einkaufsladen TEXT, " +
                "kategorien_id INTEGER, " +
                "anzahlBekommen INTEGER DEFAULT 0," +
                "FOREIGN KEY (ref_essen) references essen (_id), " +
                "FOREIGN KEY (einheiten_id) references einheiten (_id), " +
                "FOREIGN KEY (kategorien_id) references kategorien (_id));";
        database.execSQL(sqlEinkaufszettel);

        String sqlFaecher = "CREATE TABLE faecher (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fach INTEGER NOT NULL," +
                "beschriftung TEXT," +
                "gefrierschrank_id INTEGER NOT NULL," +
                "FOREIGN KEY (gefrierschrank_id) references gefrierschraenke (_id))";
        database.execSQL(sqlFaecher);

        // row temperature: 0 means freezer, 1 means fridge and 2 means room temperature
        String sqlGefrierschraenke = "CREATE TABLE gefrierschraenke (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "label TEXT NOT NULL UNIQUE," +
                "anzahlFaecher INTEGER NOT NULL," +
                "temperature INTEGER NOT NULL)";
        database.execSQL(sqlGefrierschraenke);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
