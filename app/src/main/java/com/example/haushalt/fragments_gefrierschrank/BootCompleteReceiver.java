package com.example.haushalt.fragments_gefrierschrank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.haushalt.data.DBHelper;
import com.example.haushalt.data.HaushaltContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            Cursor cursor = getDatabaseCursor(context);
            try {
                setAlarms(cursor, context);
            } catch (ParseException e) {
                e.printStackTrace();
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

    private void setAlarms(Cursor cursor, Context context) throws ParseException {
        while(cursor.moveToNext()){
            int temperature = cursor.getInt(cursor.getColumnIndex(HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE + "." + HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_TEMPERATURE));
            long durability = 0;
            switch (temperature){
                case HaushaltContract.temperatureFreezer:
                    durability = cursor.getInt(cursor.getColumnIndex(HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_DURABILITYFREEZER));
                    break;
                case HaushaltContract.temperatureFridge:
                    durability = cursor.getInt(cursor.getColumnIndex(HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_DURABILITYFRIDGE));
                    break;
                case HaushaltContract.temperatureRoom:
                    durability = cursor.getInt(cursor.getColumnIndex(HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_DURABILITYROOMTEMP));
                    break;
                default:
                    break;
            }
            if(durability > 0){
                long id = cursor.getLong(cursor.getColumnIndex(HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE + "." + HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_ID));
                String dateString = cursor.getString(cursor.getColumnIndex(HaushaltContract.GefrierschrankElementeEntry.TABLE_GEFRIERSCHRANKELEMENTE + "." + HaushaltContract.GefrierschrankElementeEntry.GEFRIERSCHRANKELEMENTE_DATUM));
                Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(dateString);
                String foodName = cursor.getString(cursor.getColumnIndex(HaushaltContract.EssenEntry.TABLE_ESSEN + "." + HaushaltContract.EssenEntry.ESSEN_ESSENSNAME));
                String deviceLabel = cursor.getString(cursor.getColumnIndex(HaushaltContract.GefrierschraenkeEntry.TABLE_GEFRIERSCHRAENKE + "." + HaushaltContract.GefrierschraenkeEntry.GEFRIERSCHRAENKE_LABEL));
                int fachNummer = cursor.getInt(cursor.getColumnIndex(HaushaltContract.FaecherEntry.TABLE_FAECHER + "." + HaushaltContract.FaecherEntry.FAECHER_FACH));

                long timeOfAlarm = date.getTime() + durability * 24 * 60 * 60 * 1000;
                // zum Testen:
                //long timeOfAlarm = date.getTime() + durability * 1000 * 10;
                Utils.setAlarm(context, timeOfAlarm, id, foodName, deviceLabel, fachNummer);
            }
        }
    }
}
