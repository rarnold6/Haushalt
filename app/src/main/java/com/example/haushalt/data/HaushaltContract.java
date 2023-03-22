package com.example.haushalt.data;

import android.content.ContentResolver;
import android.net.Uri;

// Klasse zum Definieren aller Konstanten, die fuer Datenbankabfragen benoetigt werden
public class HaushaltContract {

    // privater Konstruktor, da fuer diese Klasse keine Instanz von aussen erstellt werden soll
    private HaushaltContract(){
    }

    public static final String CONTENT_AUTHORITY = "com.example.haushalt";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAECHER = "faecher";
    public static final String PATH_GEFRIERSCHRANKELEMENTE = "gefrierschrankElemente";
    public static final String PATH_EINKAUFSZETTEL = "einkaufszettel";
    public static final String PATH_ESSEN = "essen";
    public static final String PATH_KATEGORIEN = "kategorien";
    public static final String PATH_EINHEITEN = "einheiten";
    public static final String PATH_GEFRIERSCHRAENKE = "gefrierschraenke";

    //sonstige Konstanten
    public static final String Einheit_Hinzufuegen = "Einheit hinzufügen";
    public static final String Kategorie_Hinzufuegen = "Kategorie hinzufügen";
    public static final String BITTE_WAEHLEN = "Bitte wählen";
    public static final int temperatureFreezer = 0;
    public static final int temperatureFridge = 1;
    public static final int temperatureRoom = 2;


        public static final class FaecherEntry {
        public static final String TABLE_FAECHER = "faecher";
        public static final String FAECHER_ID = "_id";
        public static final String FAECHER_FACH = "fach";
        public static final String FAECHER_BESCHRIFTUNG = "beschriftung";
        public static final String FAECHER_GEFRIERSCHRANK = "gefrierschrank_id";

        public static final String CONTENT_LIST_TYPE_FAECHER =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAECHER;
        public static final String CONTENT_ITEM_TYPE_FAECHER =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAECHER;

        public static final Uri CONTENT_URI_FAECHER = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FAECHER);
    }

    public static final class GefrierschrankElementeEntry {
        public static final String TABLE_GEFRIERSCHRANKELEMENTE = "gefrierschrankElemente";

        public static final String GEFRIERSCHRANKELEMENTE_ID = "_id";
        public static final String GEFRIERSCHRANKELEMENTE_ESSENID = "ref_essen";
        public static final String GEFRIERSCHRANKELEMENTE_ANZAHL = "anzahl";
        // public static final String GEFRIERSCHRANK_EINHEIT = "einheit";
        //public static final String GEFRIERSCHRANKELEMENTE_GEFRIERSCHRANK = "gefrierschrank_id";
        public static final String GEFRIERSCHRANKELEMENTE_FACH = "fach";
        // public static final String GEFRIERSCHRANK_KATEGORIE = "kategorie";
        public static final String GEFRIERSCHRANKELEMENTE_DATUM = "datum";


        public static final String CONTENT_LIST_TYPE_GEFRIERSCHRANKELEMENTE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GEFRIERSCHRANKELEMENTE;
        public static final String CONTENT_ITEM_TYPE_GEFRIERSCHRANKELEMENTE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GEFRIERSCHRANKELEMENTE;

        public static final Uri CONTENT_URI_GEFRIERSCHRANKELEMENTE = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GEFRIERSCHRANKELEMENTE);
    }

    public static final class EinkaufszettelEntry {
        public static final String TABLE_EINKAUFSZETTEL = "einkaufszettel";

        public static final String EINKAUFSZETTEL_ID = "_id";
        public static final String EINKAUFSZETTEL_ESSENID = "ref_essen";
        public static final String EINKAUFSZETTEL_ANZAHL_WUNSCH = "anzahlWunsch";
        public static final String EINKAUFSZETTEL_EINHEIT = "einheiten_id";
        public static final String EINKAUFSZETTEL_EINKAUFSLADEN = "einkaufsladen";
        public static final String EINKAUFSZETTEL_KATEGORIE = "kategorien_id";
        public static final String EINKAUFSZETTEL_ANZAHL_BEKOMMEN = "anzahlBekommen";


        public static final String CONTENT_LIST_TYPE_EINKAUFSZETTEL =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EINKAUFSZETTEL;
        public static final String CONTENT_ITEM_TYPE_EINKAUFSZETTEL =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EINKAUFSZETTEL;

        public static final Uri CONTENT_URI_EINKAUFSZETTEL = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EINKAUFSZETTEL);
    }

    public static final class EssenEntry {
        public static final String TABLE_ESSEN = "essen";

        public static final String ESSEN_ID = "_id";
        public static final String ESSEN_ESSENSNAME = "essensname";
        public static final String ESSEN_EINHEIT = "einheit_id";
        public static final String ESSEN_KATEGORIE = "kategorie_id";
        //public static final String ESSEN_LASTGEFRIERSCHRANK = "lastGefrierschrank";
        public static final String ESSEN_LASTFACH = "lastFach";
        public static final String ESSEN_STOREDINPAST = "stored_past";
        public static final String ESSEN_DURABILITYFREEZER = "durabilityFreezer";
        public static final String ESSEN_DURABILITYFRIDGE = "durabilityFridge";
        public static final String ESSEN_DURABILITYROOMTEMP = "durabilityRoomTemperature";

        public static final String CONTENT_LIST_TYPE_ESSEN =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ESSEN;
        public static final String CONTENT_ITEM_TYPE_ESSEN =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ESSEN;

        public static final Uri CONTENT_URI_ESSEN = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ESSEN);
    }

    public static final class EinheitenEntry {
        public static final String TABLE_EINHEITEN = "einheiten";

        public static final String EINHEITEN_ID = "_id";
        public static final String EINHEITEN_EINHEIT = "einheit";

        public static final String CONTENT_LIST_TYPE_EINHEITEN =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EINHEITEN;
        public static final String CONTENT_ITEM_TYPE_EINHEITEN =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EINHEITEN;

        public static final Uri CONTENT_URI_EINHEITEN = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EINHEITEN);
    }

    public static final class KategorienEntry {
        public static final String TABLE_KATEGORIEN= "kategorien";

        public static final String KATEGORIEN_ID = "_id";
        public static final String KATEGORIEN_KATEGORIE = "kategorie";

        public static final String CONTENT_LIST_TYPE_KATEGORIEN =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_KATEGORIEN;
        public static final String CONTENT_ITEM_TYPE_KATEGORIEN =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_KATEGORIEN;

        public static final Uri CONTENT_URI_KATEGORIEN = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_KATEGORIEN);
    }

    public static final class GefrierschraenkeEntry{
        public static final String TABLE_GEFRIERSCHRAENKE = "gefrierschraenke";

        public static final String GEFRIERSCHRAENKE_ID = "_id";
        public static final String GEFRIERSCHRAENKE_LABEL = "label";
        public static final String GEFRIERSCHRAENKE_ANZAHLFAECHER = "anzahlFaecher";
        public static final String GEFRIERSCHRAENKE_TEMPERATURE = "temperature";

        public static final String CONTENT_LIST_TYPE_GEFRIERSCHRAENKE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GEFRIERSCHRAENKE;
        public static final String CONTENT_ITEM_TYPE_GEFRIERSCHRAENKE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GEFRIERSCHRAENKE;

        public static final Uri CONTENT_URI_GEFRIERSCHRAENKE = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GEFRIERSCHRAENKE);

    }
}
