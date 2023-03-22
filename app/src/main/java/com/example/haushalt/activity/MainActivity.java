package com.example.haushalt.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import com.example.haushalt.EinkaufszettelElement;
import com.example.haushalt.Fach;
import com.example.haushalt.Food;
import com.example.haushalt.FragmentChangeListener;
import com.example.haushalt.Gefrierschrank;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.R;
import com.example.haushalt.UnitsAndCategories;
import com.example.haushalt.data.DBHelper;
import com.example.haushalt.data.HaushaltContract;
import com.example.haushalt.einkaufszettel.EinkaufszettelFragment;
import com.example.haushalt.fragments_gefrierschrank.FachFragment;
import com.example.haushalt.fragments_gefrierschrank.GefrierschrankFragment;
import com.example.haushalt.fragments_gefrierschrank.Utils;
import com.example.haushalt.initializing.InitialFragment;
import com.example.haushalt.suche.SuchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements FragmentChangeListener {

    BottomNavigationView bottomNavigation;
    private Toolbar toolbar;
    private AutoSaveThread autoSaveThread;



    // uebergebenes Fragment soll auf dem Bildschirm angezeigt werden
    public void openFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        for(int i = 1; i < fragmentManager.getBackStackEntryCount(); ++i){
            fragmentManager.popBackStack();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void addFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /*
        die App wird gestartet und diese Methode wird ausgefuehrt
        Ziel ist die Gefrierschrankuebersicht anzuzeigen
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(this.toolbar);

        createNotificationChannel();

        //erstellen der NavigationBar
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        //Daten aus der Datenbank in internen Datenstrukturen speichern, falls dies zuvor noch nicht geschehen ist (reopen of the activity)
        if(ObjectCollections.getGefrierschraenke().size() == 0 && ObjectCollections.getEinkaufszettelElements().size() == 0) {
            try {
                retrieveData();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(ObjectCollections.getGefrierschraenke().size() == 0){
            openFragment(InitialFragment.newInstance());
        } else {
            openFragment(GefrierschrankFragment.newInstance(true, ObjectCollections.getGefrierschraenke().getFirst()));
        }




        //falls Notification angeklickt wird
        Intent notifyIntent = getIntent();
        Bundle extras = notifyIntent.getExtras();
        if(extras != null){
            if(extras.containsKey(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID)){
                long gefrierschrankelementID = notifyIntent.getLongExtra(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID, -1);
                GefrierschrankElement gefrierschrankElement = ObjectCollections.getGefrierschrankEltByID(gefrierschrankelementID);
                FachFragment fachFragment = FachFragment.newInstance(gefrierschrankElement.getFach(), gefrierschrankElement);
                addFragment(fachFragment);
            }
        }



        this.autoSaveThread = new AutoSaveThread();
        this.autoSaveThread.start();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.gefrierschrank:
                            if(ObjectCollections.getGefrierschraenke().size() == 0){
                                openFragment(InitialFragment.newInstance());
                            } else {
                                openFragment(GefrierschrankFragment.newInstance(true, ObjectCollections.getGefrierschraenke().getFirst()));
                            }
                            return true;
                        case R.id.einkaufszettel:
                            openFragment(EinkaufszettelFragment.newInstance(null));
                            return true;
                        case R.id.suchen:
                            openFragment(SuchFragment.newInstance());
                            return true;
                        default:
                            return false;
                    }
                }
            };

    // abrufen der Daten aus der Datenbank und speichern der Daten in der internen (gleichen) Datenstruktur
    private void retrieveData() throws ParseException {
        Cursor cursorGefrierschrank = getQueryCompleteTableCursor(HaushaltContract.GefrierschraenkeEntry.CONTENT_URI_GEFRIERSCHRAENKE);
        Cursor cursorEinheiten = getQueryCompleteTableCursor(HaushaltContract.EinheitenEntry.CONTENT_URI_EINHEITEN);
        Cursor cursorKategorien = getQueryCompleteTableCursor(HaushaltContract.KategorienEntry.CONTENT_URI_KATEGORIEN);
        Cursor cursorFaecher = getQueryCompleteTableCursor(HaushaltContract.FaecherEntry.CONTENT_URI_FAECHER);
        Cursor cursorEssen = getQueryCompleteTableCursor(HaushaltContract.EssenEntry.CONTENT_URI_ESSEN);
        Cursor cursorGefrierschrankElement = getQueryCompleteTableCursor(HaushaltContract.GefrierschrankElementeEntry.CONTENT_URI_GEFRIERSCHRANKELEMENTE);
        Cursor cursorEinkaufszettel = getQueryCompleteTableCursor(HaushaltContract.EinkaufszettelEntry.CONTENT_URI_EINKAUFSZETTEL);


        while(cursorGefrierschrank.moveToNext()){
            Gefrierschrank gefrierschrank = new Gefrierschrank(
                    cursorGefrierschrank.getLong(cursorGefrierschrank.getColumnIndex(HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_ID)),
                    cursorGefrierschrank.getString(cursorGefrierschrank.getColumnIndex(HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_LABEL)),
                    cursorGefrierschrank.getInt(cursorGefrierschrank.getColumnIndex(HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_ANZAHLFAECHER)),
                    cursorGefrierschrank.getInt(cursorGefrierschrank.getColumnIndex(HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_TEMPERATURE))
            );
            ObjectCollections.addGefrierschrank(gefrierschrank,false);
        }

        while(cursorEinheiten.moveToNext()){
            String einheit = cursorEinheiten.getString(cursorEinheiten.getColumnIndex(HaushaltContract.EinheitenEntry.EINHEITEN_EINHEIT));
            UnitsAndCategories.addUnitCategory(UnitsAndCategories.unit, einheit, false);
        }

        while(cursorKategorien.moveToNext()){
            String kategorie = cursorKategorien.getString(cursorKategorien.getColumnIndex(HaushaltContract.KategorienEntry.KATEGORIEN_KATEGORIE));
            UnitsAndCategories.addUnitCategory(UnitsAndCategories.category, kategorie, false);
        }

        while(cursorFaecher.moveToNext()){
            long gefrierschrank_id = cursorFaecher.getLong(cursorFaecher.getColumnIndex(HaushaltContract.FaecherEntry.FAECHER_GEFRIERSCHRANK));
            Gefrierschrank gefrierschrank = ObjectCollections.getGefrierschrankBy(gefrierschrank_id);
            Fach fach = new Fach(
                    cursorFaecher.getLong(cursorFaecher.getColumnIndex(HaushaltContract.FaecherEntry.FAECHER_ID)),
                    cursorFaecher.getInt(cursorFaecher.getColumnIndex(HaushaltContract.FaecherEntry.FAECHER_FACH)),
                    cursorFaecher.getString(cursorFaecher.getColumnIndex(HaushaltContract.FaecherEntry.FAECHER_BESCHRIFTUNG)),
                    gefrierschrank
            );
            ObjectCollections.addFach(fach, false);
        }

        while(cursorEssen.moveToNext()){
            long fach_id = cursorEssen.getLong(cursorEssen.getColumnIndex(HaushaltContract.EssenEntry.ESSEN_LASTFACH));
            Fach fach = ObjectCollections.getFachBy(fach_id);
            Food food = new Food(
                    cursorEssen.getLong(cursorEssen.getColumnIndex(HaushaltContract.EssenEntry.ESSEN_ID)),
                    cursorEssen.getString(cursorEssen.getColumnIndex(HaushaltContract.EssenEntry.ESSEN_ESSENSNAME)),
                    cursorEssen.getLong(cursorEssen.getColumnIndex(HaushaltContract.EssenEntry.ESSEN_EINHEIT)),
                    cursorEssen.getLong(cursorEssen.getColumnIndex(HaushaltContract.EssenEntry.ESSEN_KATEGORIE)),
                    fach,
                    cursorEssen.getInt(cursorEssen.getColumnIndex(HaushaltContract.EssenEntry.ESSEN_STOREDINPAST)),
                    cursorEssen.getInt(cursorEssen.getColumnIndex(HaushaltContract.EssenEntry.ESSEN_DURABILITYFREEZER)),
                    cursorEssen.getInt(cursorEssen.getColumnIndex(HaushaltContract.EssenEntry.ESSEN_DURABILITYFRIDGE)),
                    cursorEssen.getInt(cursorEssen.getColumnIndex(HaushaltContract.EssenEntry.ESSEN_DURABILITYROOMTEMP))
                    );
            ObjectCollections.insertFood(food, false);
        }

        while (cursorGefrierschrankElement.moveToNext()){
            long essen_id = cursorGefrierschrankElement.getLong(cursorGefrierschrankElement.getColumnIndex(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ESSENID));
            Food food = ObjectCollections.getFoodByID(essen_id);
            if(food == null){
                continue;
            }
            long fach_id = cursorGefrierschrankElement.getLong(cursorGefrierschrankElement.getColumnIndex(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_FACH));
            Fach fach = ObjectCollections.getFachBy(fach_id);
            if(fach == null){
                continue;
            }
            String time = cursorGefrierschrankElement.getString(cursorGefrierschrankElement.getColumnIndex(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_DATUM));
            Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(time);

            GefrierschrankElement gefrierschrankElement = new GefrierschrankElement(
                    cursorGefrierschrankElement.getLong(cursorGefrierschrankElement.getColumnIndex(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID)),
                    food,
                    cursorGefrierschrankElement.getInt(cursorGefrierschrankElement.getColumnIndex(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ANZAHL)),
                    fach,
                    date
            );
            ObjectCollections.addToFreezer(gefrierschrankElement, false);
        }

        while(cursorEinkaufszettel.moveToNext()){
            long essen_id = cursorEinkaufszettel.getLong(cursorEinkaufszettel.getColumnIndex(HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ESSENID));
            Food food = ObjectCollections.getFoodByID(essen_id);
            if(food == null){
                continue;
            }
            EinkaufszettelElement einkaufszettelElement = new EinkaufszettelElement(
                    cursorEinkaufszettel.getLong(cursorEinkaufszettel.getColumnIndex(HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ID)),
                    food,
                    cursorEinkaufszettel.getInt(cursorEinkaufszettel.getColumnIndex(HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ANZAHL_WUNSCH)),
                    cursorEinkaufszettel.getInt(cursorEinkaufszettel.getColumnIndex(HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_EINKAUFSLADEN)),
                    cursorEinkaufszettel.getInt(cursorEinkaufszettel.getColumnIndex(HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ANZAHL_BEKOMMEN))
            );
            ObjectCollections.addToShoppingList(einkaufszettelElement, false);
        }
    }

    private Cursor getQueryCompleteTableCursor(Uri uri){
        return getContentResolver().query(uri,
                new String[]{"*"},
                null,
                null,
                null);
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channel_id = getString(R.string.channel_id);
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /*
        mHv replaceFragment soll das übergebene Fragment angezeigt werden
     */
    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    //bei Schliessen der App sollen die Daten noch in der Datenbank gespeichert werden
    @Override
    protected void onStop() {
        super.onStop();
        this.autoSaveThread.running = false;
        saveGefrierschraenke();
        saveUnitData();
        saveCategoryData();
        saveFachData();
        saveFoodData();
        saveGefrierschrankEltData();
        saveGroceryList();
    }

    synchronized private void saveGefrierschraenke(){
        if(ObjectCollections.gefrierschrankChanged){
            for(Long id : ObjectCollections.removedGefrierschrank){
                deleteRowIn(HaushaltContract.GefrierschraenkeEntry.CONTENT_URI_GEFRIERSCHRAENKE, id);
            }
            for(Long id : ObjectCollections.addedGefrierschrank){
                Gefrierschrank gefrierschrank = ObjectCollections.getGefrierschrankBy(id);
                ContentValues contentValues = new ContentValues();
                contentValues.put(HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_ID, id);
                contentValues.put(HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_LABEL, gefrierschrank.getLabel());
                contentValues.put(HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_ANZAHLFAECHER, gefrierschrank.getAnzahlFaecher());
                contentValues.put(HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_TEMPERATURE, gefrierschrank.getTemperature());
                Uri insertUri = getContentResolver().insert(HaushaltContract.GefrierschraenkeEntry.CONTENT_URI_GEFRIERSCHRAENKE, contentValues);
                if(insertUri == null || ContentUris.parseId(insertUri) == -1){
                    contentValues.remove(HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_ID);
                    Uri updateUri = Uri.withAppendedPath(HaushaltContract.GefrierschraenkeEntry.CONTENT_URI_GEFRIERSCHRAENKE, String.valueOf(id));
                    getContentResolver().update(updateUri, contentValues, null, null);
                }
            }
            ObjectCollections.gefrierschrankChanged = false;
            ObjectCollections.removedGefrierschrank.clear();
            ObjectCollections.addedGefrierschrank.clear();
        }
    }

    synchronized private void saveUnitData(){
        if(UnitsAndCategories.didUnitsChanged()){
            for(Long id : UnitsAndCategories.getRemovedUnits()) {
                deleteRowIn(HaushaltContract.EinheitenEntry.CONTENT_URI_EINHEITEN, id);
            }
            for(Long id : UnitsAndCategories.getAddedUnits()){
                ContentValues contentValues = new ContentValues();
                contentValues.put(HaushaltContract.EinheitenEntry.EINHEITEN_ID, id);
                contentValues.put(HaushaltContract.EinheitenEntry.EINHEITEN_EINHEIT, UnitsAndCategories.getUnit(id));
                Uri uri = getContentResolver().insert(HaushaltContract.EinheitenEntry.CONTENT_URI_EINHEITEN, contentValues);
                if(uri == null){
                    contentValues.remove(HaushaltContract.EinheitenEntry.EINHEITEN_ID);
                    Uri updateUri = Uri.withAppendedPath(HaushaltContract.EinheitenEntry.CONTENT_URI_EINHEITEN, String.valueOf(id));
                    getContentResolver().update(updateUri, contentValues, null, null);
                }
            }
            UnitsAndCategories.unitsChanged = false;
            UnitsAndCategories.addedUnits.clear();
            UnitsAndCategories.removedUnits.clear();
        }
    }

    synchronized private void saveCategoryData(){
        if(UnitsAndCategories.didCategoriesChanged()){
            for(Long id : UnitsAndCategories.getRemovedCategories()) {
                deleteRowIn(HaushaltContract.KategorienEntry.CONTENT_URI_KATEGORIEN, id);
            }
            for(Long id : UnitsAndCategories.getAddedCategories()){
                ContentValues contentValues = new ContentValues();
                contentValues.put(HaushaltContract.KategorienEntry.KATEGORIEN_ID, id);
                contentValues.put(HaushaltContract.KategorienEntry.KATEGORIEN_KATEGORIE, UnitsAndCategories.getCategory(id));
                Uri uri = getContentResolver().insert(HaushaltContract.KategorienEntry.CONTENT_URI_KATEGORIEN, contentValues);
                if(uri == null){
                    contentValues.remove(HaushaltContract.KategorienEntry.KATEGORIEN_ID);
                    Uri updateUri = Uri.withAppendedPath(HaushaltContract.KategorienEntry.CONTENT_URI_KATEGORIEN, String.valueOf(id));
                    getContentResolver().update(updateUri, contentValues, null, null);
                }
            }
            UnitsAndCategories.categoriesChanged = false;
            UnitsAndCategories.addedCategories.clear();
            UnitsAndCategories.removedCategories.clear();
        }
    }

    synchronized private void saveFoodData(){
        if(ObjectCollections.essenslisteChanged){
            for(Long removedFoodID : ObjectCollections.removedFood){
                deleteRowIn(HaushaltContract.EssenEntry.CONTENT_URI_ESSEN, removedFoodID);
            }
            for(Long addedFoodID : ObjectCollections.addedFood){
                Food food = ObjectCollections.getFoodByID(addedFoodID);
                ContentValues values = new ContentValues();
                values.put(HaushaltContract.EssenEntry.ESSEN_ID, food.getId());
                values.put(HaushaltContract.EssenEntry.ESSEN_ESSENSNAME, food.getEssensname());
                values.put(HaushaltContract.EssenEntry.ESSEN_EINHEIT, food.getEinheit_id());
                values.put(HaushaltContract.EssenEntry.ESSEN_KATEGORIE, food.getKategorie_id());
                if(food.getLastFach() != null) {
                    values.put(HaushaltContract.EssenEntry.ESSEN_LASTFACH, food.getLastFach().getFachNummer());
                }
                values.put(HaushaltContract.EssenEntry.ESSEN_STOREDINPAST, food.getStoredInPast());
                values.put(HaushaltContract.EssenEntry.ESSEN_DURABILITYFREEZER, food.getDurabilityFreezer());
                values.put(HaushaltContract.EssenEntry.ESSEN_DURABILITYFRIDGE, food.getDurabilityFridge());
                values.put(HaushaltContract.EssenEntry.ESSEN_DURABILITYROOMTEMP, food.getDurabilityRoomTemperature());
                Uri uri = getContentResolver().insert(HaushaltContract.EssenEntry.CONTENT_URI_ESSEN, values);

                if(uri == null || ContentUris.parseId(uri) == -1){
                    values.remove(HaushaltContract.EssenEntry.ESSEN_ID);
                    Uri updateUri = Uri.withAppendedPath(HaushaltContract.EssenEntry.CONTENT_URI_ESSEN, String.valueOf(food.getId()));
                    getContentResolver().update(updateUri, values, null, null);
                }
            }
            ObjectCollections.essenslisteChanged = false;
            ObjectCollections.removedFood.clear();
            ObjectCollections.addedFood.clear();
        }
    }

    synchronized private void saveFachData(){
        if(ObjectCollections.faecherChanged){
            for(long id : ObjectCollections.removedFaecher){
                deleteRowIn(HaushaltContract.FaecherEntry.CONTENT_URI_FAECHER, id);
            }
            for(long id : ObjectCollections.addedFach){
                Fach fach = ObjectCollections.getFachBy(id);
                ContentValues contentValues = new ContentValues();
                contentValues.put(HaushaltContract.FaecherEntry.FAECHER_ID, fach.getId());
                contentValues.put(HaushaltContract.FaecherEntry.FAECHER_FACH, fach.getFachNummer());
                contentValues.put(HaushaltContract.FaecherEntry.FAECHER_BESCHRIFTUNG, fach.getBeschriftung());
                contentValues.put(HaushaltContract.FaecherEntry.FAECHER_GEFRIERSCHRANK, fach.getGefrierschrank().getId());
                Uri uri = getContentResolver().insert(HaushaltContract.FaecherEntry.CONTENT_URI_FAECHER, contentValues);

                if(uri == null || ContentUris.parseId(uri) == -1){
                    contentValues.remove(HaushaltContract.FaecherEntry.FAECHER_ID);
                    Uri updateUri = Uri.withAppendedPath(HaushaltContract.FaecherEntry.CONTENT_URI_FAECHER, String.valueOf(id));
                    getContentResolver().update(updateUri, contentValues, null, null);
                }
            }
            ObjectCollections.faecherChanged = false;
            ObjectCollections.removedFaecher.clear();
            ObjectCollections.addedFach.clear();
        }
    }

    synchronized private void saveGefrierschrankEltData(){
        if(ObjectCollections.gefrierschrankContentChanged){
            for(long id : ObjectCollections.removedGefrierschrankElts){
                deleteRowIn(HaushaltContract.GefrierschrankElementeEntry.CONTENT_URI_GEFRIERSCHRANKELEMENTE, id);
            }
            for(long id : ObjectCollections.addedGefrierschrankElts){
                GefrierschrankElement gefrierschrankElement = ObjectCollections.getGefrierschrankEltByID(id);
                ContentValues contentValues = new ContentValues();
                contentValues.put(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID, gefrierschrankElement.getId());
                contentValues.put(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ESSENID, gefrierschrankElement.getFood().getId());
                contentValues.put(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ANZAHL, gefrierschrankElement.getAnzahl());
                contentValues.put(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_FACH, gefrierschrankElement.getFach().getId());
                contentValues.put(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_DATUM, gefrierschrankElement.getDate().toString());
                Uri uri = getContentResolver().insert(HaushaltContract.GefrierschrankElementeEntry.CONTENT_URI_GEFRIERSCHRANKELEMENTE, contentValues);
                if(uri == null || ContentUris.parseId(uri) == -1){
                    contentValues.remove(HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID);
                    Uri updateUri = Uri.withAppendedPath(HaushaltContract.GefrierschrankElementeEntry.CONTENT_URI_GEFRIERSCHRANKELEMENTE, String.valueOf(id));
                    getContentResolver().update(updateUri, contentValues, null, null);
                }
            }
            ObjectCollections.gefrierschrankContentChanged = false;
            ObjectCollections.removedGefrierschrankElts.clear();
            ObjectCollections.addedGefrierschrankElts.clear();
        }
    }

    synchronized private void saveGroceryList(){
        if(ObjectCollections.einkaufszettelChanged){
            for(long id : ObjectCollections.removedEinkaufsElt){
                deleteRowIn(HaushaltContract.EinkaufszettelEntry.CONTENT_URI_EINKAUFSZETTEL, id);
            }
            for(long id : ObjectCollections.addedEinkaufsElt){
                EinkaufszettelElement einkaufszettelElement = ObjectCollections.getEinkaufszettelEltBy(id);
                ContentValues contentValues = new ContentValues();
                contentValues.put(HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ID, einkaufszettelElement.getId());
                contentValues.put(HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ESSENID, einkaufszettelElement.getFood().getId());
                contentValues.put(HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ANZAHL_WUNSCH, einkaufszettelElement.getAnzahlWunsch());
                contentValues.put(HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ANZAHL_BEKOMMEN, einkaufszettelElement.getAnzahlBekommen());
                Uri uri = getContentResolver().insert(HaushaltContract.EinkaufszettelEntry.CONTENT_URI_EINKAUFSZETTEL, contentValues);

                if(uri == null || ContentUris.parseId(uri) == -1){
                    contentValues.remove(HaushaltContract.EinkaufszettelEntry.EINKAUFSZETTEL_ID);
                    Uri updateUri = Uri.withAppendedPath(HaushaltContract.EinkaufszettelEntry.CONTENT_URI_EINKAUFSZETTEL, String.valueOf(id));
                    getContentResolver().update(updateUri, contentValues, null, null);
                }
            }
            ObjectCollections.einkaufszettelChanged = false;
            ObjectCollections.removedEinkaufsElt.clear();
            ObjectCollections.addedEinkaufsElt.clear();
        }
    }

    synchronized private void deleteRowIn(Uri deleteUri, long id){
        Uri updateUri = Uri.withAppendedPath(deleteUri, String.valueOf(id));
        getContentResolver().delete(updateUri,
                null,
                null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    private class AutoSaveThread extends Thread {
        public boolean running = true;
        @Override
        public void run() {
            while(running){
                saveGefrierschraenke();
                saveUnitData();
                saveCategoryData();
                saveFachData();
                saveFoodData();
                System.out.println("Hallo");
                saveGefrierschrankEltData();
                saveGroceryList();
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private Cursor getDatabaseCursor(Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String table = "(" + HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE +
                " JOIN " + HaushaltContract.FaecherEntry.TABLE_FAECHER + " ON " + HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE + "." + HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_FACH +
                " = " + HaushaltContract.FaecherEntry.TABLE_FAECHER + "." + HaushaltContract.FaecherEntry.FAECHER_ID + ") JOIN " +
                HaushaltContract.EssenEntry.TABLE_ESSEN + " ON " + HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_ID + " = " + HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE + "." + HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ESSENID +
                " JOIN " + HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE + " ON " + HaushaltContract.FaecherEntry.TABLE_FAECHER + "." + HaushaltContract.FaecherEntry.FAECHER_GEFRIERSCHRANK + " = " + HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE + "." + HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_ID;

        String[] columns = new String[]{HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE + "." + HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID,
                HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE + "." + HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_DATUM,
                HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_ESSENSNAME,
                HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_DURABILITYFREEZER,
                HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_DURABILITYFRIDGE,
                HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_DURABILITYROOMTEMP,
                HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE + "." + HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_LABEL,
                HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE + "." + HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_TEMPERATURE,
                HaushaltContract.FaecherEntry.TABLE_FAECHER + "." + HaushaltContract.FaecherEntry.FAECHER_FACH};

        String selection = HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_DURABILITYFREEZER + " > 0 OR " +
                HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_DURABILITYFRIDGE + " > 0 OR " +
                HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_DURABILITYROOMTEMP + " > 0";

        return database.query(table, columns, selection, null, null, null, null);
    }
}