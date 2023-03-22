package com.example.haushalt.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URI;

/*
    Klasse mit Methoden fuer Datenbankabfragen, sodass man von anderen Klassen nur diese Methoden
    mit passenden Parametern aufrufen muss
 */

public class HaushaltProvider extends ContentProvider {

    public static final String LOG_TAG = HaushaltProvider.class.getSimpleName();

    private DBHelper dbHelper;

    private static final int URI_FAECHER = 100;
    private static final int URI_FAECHER_ID = 101;
    private static final int URI_GEFRIERSCHRANKELEMENT = 102;
    private static final int URI_GEFRIERSCHRANKELEMENT_ID = 103;
    private static final int URI_EINKAUFSZETTEL = 104;
    private static final int URI_EINKAUFSZETTEL_ID = 105;
    private static final int URI_ESSEN = 106;
    private static final int URI_ESSEN_ID = 107;
    private static final int URI_EINHEITEN = 108;
    private static final int URI_EINHEITEN_ID = 109;
    private static final int URI_KATEGORIEN = 110;
    private static final int URI_KATEGORIEN_ID = 111;
    private static final int URI_GEFRIERSCHRANK = 112;
    private static final int URI_GEFRIERSCHRANK_ID = 113;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_FAECHER, URI_FAECHER);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_FAECHER + "/#", URI_FAECHER_ID);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_GEFRIERSCHRANKELEMENTE, URI_GEFRIERSCHRANKELEMENT);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_GEFRIERSCHRANKELEMENTE + "/#", URI_GEFRIERSCHRANKELEMENT_ID);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_EINKAUFSZETTEL, URI_EINKAUFSZETTEL);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_EINKAUFSZETTEL + "/#", URI_EINKAUFSZETTEL_ID);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_ESSEN, URI_ESSEN);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_ESSEN + "/#", URI_ESSEN_ID);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_EINHEITEN, URI_EINHEITEN);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_EINHEITEN + "/#", URI_EINHEITEN_ID);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_KATEGORIEN, URI_KATEGORIEN);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_KATEGORIEN + "/#", URI_KATEGORIEN_ID);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_GEFRIERSCHRAENKE, URI_GEFRIERSCHRANK);
        uriMatcher.addURI(HaushaltContract.CONTENT_AUTHORITY, HaushaltContract.PATH_GEFRIERSCHRAENKE + "/#", URI_GEFRIERSCHRANK_ID);
    }

    @Override
    public boolean onCreate() {
        this.dbHelper = new DBHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs
            , @Nullable String sortOrder) {

        SQLiteDatabase database = this.dbHelper.getReadableDatabase();

        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match){
            case URI_FAECHER:
                cursor = database.query(HaushaltContract.FaecherEntry.TABLE_FAECHER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case URI_FAECHER_ID:
                selection = HaushaltContract.FaecherEntry.FAECHER_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(HaushaltContract.FaecherEntry.TABLE_FAECHER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case URI_GEFRIERSCHRANKELEMENT:
                /*SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(HaushaltContract.GefrierschrankEntry.TABLE_GEFRIERSCHRANK +
                        " LEFT JOIN " + HaushaltContract.EssenEntry.TABLE_ESSEN + " ON " +
                            HaushaltContract.GefrierschrankEntry.GEFRIERSCHRANK_ESSENID + " = " + HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_ID +
                        " LEFT JOIN " + HaushaltContract.EinheitenEntry.TABLE_EINHEITEN + " ON " +
                            HaushaltContract.EssenEntry.ESSEN_EINHEIT + " = " + HaushaltContract.EinheitenEntry.TABLE_EINHEITEN + "." + HaushaltContract.EinheitenEntry.EINHEITEN_ID +
                        " LEFT JOIN " + HaushaltContract.KategorienEntry.TABLE_KATEGORIEN + " ON " +
                            HaushaltContract.EssenEntry.ESSEN_KATEGORIE + " = " + HaushaltContract.KategorienEntry.TABLE_KATEGORIEN + "." + HaushaltContract.KategorienEntry.KATEGORIEN_ID);

                cursor = builder.query(database,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;*/

            cursor = database.query(HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
            break;
            case URI_GEFRIERSCHRANKELEMENT_ID:
                selection = HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
                /*
                 SELECT einkaufszettel._id, beschriftung, anzahlWunsch, einheit, anzahlBekommen
                 FROM einkaufszettel LEFT JOIN einheiten ON einkaufszettel.einheit_id = einheiten._id
                                     LEFT JOIN kategorien ON einkaufszettel.kategorien_id = kategorien._id
                 */
            case URI_EINKAUFSZETTEL:
                /*SQLiteQueryBuilder builder1 = new SQLiteQueryBuilder();
                builder1.setTables(HaushaltContract.EinkaufszettelEntry.TABLE_EINKAUFSZETTEL +
                        " LEFT JOIN " + HaushaltContract.EssenEntry.TABLE_ESSEN + " ON " +
                        HaushaltContract.EinkaufszettelEntry.TABLE_EINKAUFSZETTEL + "." + HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ESSENID + " = " + HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_ID +
                        " LEFT JOIN " + HaushaltContract.EinheitenEntry.TABLE_EINHEITEN + " ON " +
                        HaushaltContract.EinkaufszettelEntry.TABLE_EINKAUFSZETTEL + "." + HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_EINHEIT + " = " + HaushaltContract.EinheitenEntry.TABLE_EINHEITEN + "." + HaushaltContract.EinheitenEntry.EINHEITEN_ID +
                        " LEFT JOIN " + HaushaltContract.KategorienEntry.TABLE_KATEGORIEN + " ON " +
                        HaushaltContract.EinkaufszettelEntry.TABLE_EINKAUFSZETTEL + "." + HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_KATEGORIE + " = " + HaushaltContract.KategorienEntry.TABLE_KATEGORIEN + "." + HaushaltContract.KategorienEntry.KATEGORIEN_ID);

                cursor = builder1.query(database,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break; */

            cursor = database.query(HaushaltContract.EinkaufszettelEntry.TABLE_EINKAUFSZETTEL,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
            break;
            case URI_ESSEN:
                /*SQLiteQueryBuilder builder2 = new SQLiteQueryBuilder();
                builder2.setTables(HaushaltContract.EssenEntry.TABLE_ESSEN +
                        " LEFT JOIN " + HaushaltContract.EinheitenEntry.TABLE_EINHEITEN + " ON " +
                        HaushaltContract.EssenEntry.ESSEN_EINHEIT + " = " + HaushaltContract.EinheitenEntry.TABLE_EINHEITEN + "." + HaushaltContract.EinheitenEntry.EINHEITEN_ID +
                        " LEFT JOIN " + HaushaltContract.KategorienEntry.TABLE_KATEGORIEN + " ON " +
                        HaushaltContract.EssenEntry.ESSEN_KATEGORIE + " = " + HaushaltContract.KategorienEntry.TABLE_KATEGORIEN + "." + HaushaltContract.KategorienEntry.KATEGORIEN_ID);

                cursor = builder2.query(database,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;*/

            cursor = database.query(HaushaltContract.EssenEntry.TABLE_ESSEN,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
            break;
            case URI_ESSEN_ID:
                selection = HaushaltContract.EssenEntry.ESSEN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(HaushaltContract.EssenEntry.TABLE_ESSEN,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case URI_EINHEITEN:
                cursor = database.query(HaushaltContract.EinheitenEntry.TABLE_EINHEITEN,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case URI_EINHEITEN_ID:
                selection = HaushaltContract.EinheitenEntry.EINHEITEN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(HaushaltContract.EinheitenEntry.TABLE_EINHEITEN,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case URI_KATEGORIEN:
                cursor = database.query(HaushaltContract.KategorienEntry.TABLE_KATEGORIEN,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case URI_KATEGORIEN_ID:
                selection = HaushaltContract.KategorienEntry.KATEGORIEN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(HaushaltContract.KategorienEntry.TABLE_KATEGORIEN,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case URI_GEFRIERSCHRANK:
                cursor = database.query(HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case URI_GEFRIERSCHRANK_ID:
                selection = HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case URI_FAECHER:
                return HaushaltContract.FaecherEntry.CONTENT_LIST_TYPE_FAECHER;
            case URI_FAECHER_ID:
                return HaushaltContract.FaecherEntry.CONTENT_ITEM_TYPE_FAECHER;
            case URI_GEFRIERSCHRANKELEMENT:
                return HaushaltContract.GefrierschrankElementeEntry.CONTENT_LIST_TYPE_GEFRIERSCHRANKELEMENTE;
            case URI_GEFRIERSCHRANKELEMENT_ID:
                return HaushaltContract.GefrierschrankElementeEntry.CONTENT_ITEM_TYPE_GEFRIERSCHRANKELEMENTE;
            case URI_EINKAUFSZETTEL:
                return HaushaltContract.EinkaufszettelEntry.CONTENT_LIST_TYPE_EINKAUFSZETTEL;
            case URI_EINKAUFSZETTEL_ID:
                return HaushaltContract.EinkaufszettelEntry.CONTENT_ITEM_TYPE_EINKAUFSZETTEL;
            case URI_ESSEN:
                return HaushaltContract.EssenEntry.CONTENT_LIST_TYPE_ESSEN;
            case URI_ESSEN_ID:
                return HaushaltContract.EssenEntry.CONTENT_ITEM_TYPE_ESSEN;
            case URI_EINHEITEN:
                return HaushaltContract.EinheitenEntry.CONTENT_LIST_TYPE_EINHEITEN;
            case URI_EINHEITEN_ID:
                return HaushaltContract.EinheitenEntry.CONTENT_ITEM_TYPE_EINHEITEN;
            case URI_KATEGORIEN:
                return HaushaltContract.KategorienEntry.CONTENT_LIST_TYPE_KATEGORIEN;
            case URI_KATEGORIEN_ID:
                return HaushaltContract.KategorienEntry.CONTENT_ITEM_TYPE_KATEGORIEN;
            case URI_GEFRIERSCHRANK:
                return HaushaltContract.GefrierschraenkeEntry.CONTENT_LIST_TYPE_GEFRIERSCHRAENKE;
            case URI_GEFRIERSCHRANK_ID:
                return HaushaltContract.GefrierschraenkeEntry.CONTENT_ITEM_TYPE_GEFRIERSCHRAENKE;
            default:
                throw new IllegalArgumentException("Unknown URI "+ uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case URI_FAECHER:
                return insertFach(uri, contentValues);
            case URI_GEFRIERSCHRANKELEMENT:
                return insertGefrierschrankElement(uri, contentValues);
            case URI_EINKAUFSZETTEL:
                return insertEinkaufszettel(uri, contentValues);
            case URI_ESSEN:
                return insertEssen(uri, contentValues);
            case URI_EINHEITEN:
                return insertEinheitenAndCategories(uri, contentValues, HaushaltContract.EinheitenEntry.TABLE_EINHEITEN);
            case URI_KATEGORIEN:
                return insertEinheitenAndCategories(uri, contentValues, HaushaltContract.KategorienEntry.TABLE_KATEGORIEN);
            case URI_GEFRIERSCHRANK:
                return insertGefrierschrank(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for URI " + uri);
        }
    }

    private Uri insertGefrierschrank(Uri uri, ContentValues contentValues){
        SQLiteDatabase database = this.dbHelper.getWritableDatabase();
        long id;
        try {
            id = database.insert(HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE, null, contentValues);
        } catch (SQLiteException e){
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertEinkaufszettel(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = this.dbHelper.getWritableDatabase();
        long id;
        try {
            id = database.insert(HaushaltContract.EinkaufszettelEntry.TABLE_EINKAUFSZETTEL, null, contentValues);
        } catch (SQLiteException e){
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertGefrierschrankElement(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = this.dbHelper.getWritableDatabase();
        //Date date = new Date();
        //contentValues.put(HaushaltContract.GefrierschrankEntry.GEFRIERSCHRANK_DATUM, date.toString());
        long id;
        try{
            id = database.insert(HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE, null, contentValues);
        } catch (SQLiteException e){
            return null;
        }


        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertFach(Uri uri, ContentValues values) {
        //sind angegebene Daten korrekt
        /*Integer fach = values.getAsInteger(GefrierschrankContract.GefrierschrankEntry.FAECHER_FACH);
        if (fach == null || fach < 1){
            throw new IllegalArgumentException("Fach benötigt eine Nummer");
        }
        */
        SQLiteDatabase database = this.dbHelper.getWritableDatabase();

        long id;

        try{
            id = database.insert(HaushaltContract.FaecherEntry.TABLE_FAECHER, null, values);
        } catch (SQLiteException e){
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertEssen(Uri uri, ContentValues values) {
        //sind angegebene Daten korrekt
        /*Integer fach = values.getAsInteger(GefrierschrankContract.GefrierschrankEntry.FAECHER_FACH);
        if (fach == null || fach < 1){
            throw new IllegalArgumentException("Fach benötigt eine Nummer");
        }
        */
        SQLiteDatabase database = this.dbHelper.getWritableDatabase();

        long id;
        try {
            id = database.insert(HaushaltContract.EssenEntry.TABLE_ESSEN, null, values);
        } catch (SQLiteException e){
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertEinheitenAndCategories(Uri uri, ContentValues values, String tablename){
        SQLiteDatabase database = this.dbHelper.getWritableDatabase();
        long id = database.insert(tablename, null, values);

        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = this.dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match){
            case URI_GEFRIERSCHRANKELEMENT_ID:
                selection = HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsUpdated = database.delete(HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE, selection, selectionArgs);
                if (rowsUpdated != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;
            case URI_EINHEITEN_ID:
                selection = HaushaltContract.EinheitenEntry.EINHEITEN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsDeleted = database.delete(HaushaltContract.EinheitenEntry.TABLE_EINHEITEN, selection, selectionArgs);
                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case URI_KATEGORIEN_ID:
                selection = HaushaltContract.KategorienEntry.KATEGORIEN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsdeleted = database.delete(HaushaltContract.KategorienEntry.TABLE_KATEGORIEN, selection, selectionArgs);
                if(rowsdeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsdeleted;
            case URI_ESSEN_ID:
                selection = HaushaltContract.EssenEntry.ESSEN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsdeletedEssen = database.delete(HaushaltContract.EssenEntry.TABLE_ESSEN, selection, selectionArgs);
                if(rowsdeletedEssen != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsdeletedEssen;
            case URI_FAECHER_ID:
                selection = HaushaltContract.FaecherEntry.FAECHER_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsdeletedFach = database.delete(HaushaltContract.FaecherEntry.TABLE_FAECHER, selection, selectionArgs);
                if(rowsdeletedFach != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsdeletedFach;
            case URI_EINKAUFSZETTEL_ID:
                selection = HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsdeletedEinkauf = database.delete(HaushaltContract.EinkaufszettelEntry.TABLE_EINKAUFSZETTEL, selection, selectionArgs);
                if(rowsdeletedEinkauf != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsdeletedEinkauf;
            case URI_GEFRIERSCHRANK_ID:
                selection = HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsdeletedGefrierschrank = database.delete(HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE, selection, selectionArgs);
                if(rowsdeletedGefrierschrank != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsdeletedGefrierschrank;
            default:
                return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = this.dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match){
            case URI_GEFRIERSCHRANKELEMENT:
                getContext().getContentResolver().notifyChange(uri, null);
                return  database.update(
                        HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE,
                        contentValues,
                        selection,
                        selectionArgs
                );
            case URI_GEFRIERSCHRANKELEMENT_ID:
                selection = HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateHaushalt(uri, contentValues, selection, selectionArgs, HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE);
            case URI_EINKAUFSZETTEL:
                getContext().getContentResolver().notifyChange(uri, null);
                return  database.update(
                        HaushaltContract.EinkaufszettelEntry.TABLE_EINKAUFSZETTEL,
                        contentValues,
                        selection,
                        selectionArgs
                );
            case URI_EINKAUFSZETTEL_ID:
                selection = HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateHaushalt(uri, contentValues, selection, selectionArgs, HaushaltContract.EinkaufszettelEntry.TABLE_EINKAUFSZETTEL);
            case URI_ESSEN:
                getContext().getContentResolver().notifyChange(uri, null);
                return  database.update(
                        HaushaltContract.EssenEntry.TABLE_ESSEN,
                        contentValues,
                        selection,
                        selectionArgs
                );
            case URI_ESSEN_ID:
                selection = HaushaltContract.EssenEntry.ESSEN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateHaushalt(uri, contentValues, selection, selectionArgs, HaushaltContract.EssenEntry.TABLE_ESSEN);
            case URI_EINHEITEN_ID:
                selection = HaushaltContract.EinheitenEntry.EINHEITEN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateHaushalt(uri, contentValues, selection, selectionArgs, HaushaltContract.EinheitenEntry.TABLE_EINHEITEN);
            case URI_KATEGORIEN_ID:
                selection = HaushaltContract.KategorienEntry.KATEGORIEN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateHaushalt(uri, contentValues, selection, selectionArgs, HaushaltContract.KategorienEntry.TABLE_KATEGORIEN);
            case URI_FAECHER_ID:
                selection = HaushaltContract.FaecherEntry.FAECHER_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateHaushalt(uri, contentValues, selection, selectionArgs, HaushaltContract.FaecherEntry.TABLE_FAECHER);
            case URI_GEFRIERSCHRANK:
                getContext().getContentResolver().notifyChange(uri, null);
                return  database.update(
                        HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE,
                        contentValues,
                        selection,
                        selectionArgs
                );
            case URI_GEFRIERSCHRANK_ID:
                selection = HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateHaushalt(uri, contentValues, selection, selectionArgs, HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE);
                default:
                return 0;
        }
    }

    private int updateHaushalt(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs, String tablename) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int rowsUpdated = database.update(tablename, contentValues, selection, selectionArgs);
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


}
