package com.example.haushalt.fragments_gefrierschrank;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.haushalt.EinkaufszettelElement;
import com.example.haushalt.Fach;
import com.example.haushalt.GefrierschrankElement;
import com.example.haushalt.R;
import com.example.haushalt.UnitsAndCategories;
import com.example.haushalt.activity.ObjectCollections;
import com.example.haushalt.data.HaushaltContract;

import java.util.Iterator;
import java.util.LinkedList;


public class FachBaseAdapter extends BaseAdapter {

    private Context context;
    private Fach fach;
    private LinkedList<GefrierschrankElement> gefrierschrankElements;
    private View rootView;
    private GefrierschrankElement focusElement;

    public FachBaseAdapter(Context context, Fach fach, LinkedList<GefrierschrankElement> gefrierschrankElements, GefrierschrankElement focusElement) {
        this.context = context;
        this.fach = fach;
        this.gefrierschrankElements = new LinkedList<GefrierschrankElement>();
        this.focusElement = focusElement;

        Iterator<GefrierschrankElement> it = gefrierschrankElements.iterator();
        while(it.hasNext()){
            GefrierschrankElement nextElt = it.next();
            if(nextElt.getFach() == fach){
                this.gefrierschrankElements.add(nextElt);
            }
        }
    }


    @Override
    public int getCount() {
        return gefrierschrankElements.size();
    }

    @Override
    public Object getItem(int i) {
        return gefrierschrankElements.get(i);
    }

    @Override
    public long getItemId(int i) {
        return gefrierschrankElements.get(i).getId();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.essenselement, viewGroup, false);

        final GefrierschrankElement gefrierschrankElement = (GefrierschrankElement) getItem(i);
        if(gefrierschrankElement == focusElement){
            view.setBackgroundColor(Color.YELLOW);
        }

        TextView essensnameTextView = (TextView) view.findViewById(R.id.essensname);
        final TextView anzahlTextView = (TextView) view.findViewById(R.id.anzahl);
        TextView einheitTextView = (TextView) view.findViewById(R.id.einheit);
        Button decrement = (Button) view.findViewById(R.id.decrementButton);
        Button increment = (Button) view.findViewById(R.id.incrementButton);
        ImageButton delete = (ImageButton) view.findViewById(R.id.deleteButton);

        this.rootView = viewGroup;

        essensnameTextView.setText(gefrierschrankElement.getFood().getEssensname());
        anzahlTextView.setText(String.valueOf(gefrierschrankElement.getAnzahl()));
        einheitTextView.setText(UnitsAndCategories.getUnit(gefrierschrankElement.getFood().getEinheit_id()));

        final View finalView = view;
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                /*
                    Unterscheidung der unterschiedlich hohen Anzahl (aufgrund der unterschiedlichen Einheiten)
                     und der automatischen Verminderung um eine sinnvolle Menge
                 */
                if(gefrierschrankElement.getAnzahl() == 100 || gefrierschrankElement.getAnzahl() == 1){
                    gefrierschrankElement.getFood().setLastFach(gefrierschrankElement.getFach());

                    // loeschen des Eintrags mHv Hilfsmethoden
                    openPopUpWindow(context, gefrierschrankElement);
                    deleteNotification(gefrierschrankElement);
                    ObjectCollections.removeFromFreezer(gefrierschrankElement);
                    gefrierschrankElements.remove(gefrierschrankElement);
                    notifyDataSetChanged();
                    return;
                }

                if(gefrierschrankElement.getAnzahl() > 99){
                    gefrierschrankElement.setAnzahl(gefrierschrankElement.getAnzahl()-100);

                } else if(gefrierschrankElement.getAnzahl() > 49){
                    gefrierschrankElement.setAnzahl(gefrierschrankElement.getAnzahl()-10);

                } else if(gefrierschrankElement.getAnzahl() > 24){
                    gefrierschrankElement.setAnzahl(gefrierschrankElement.getAnzahl()-5);

                } else {
                    gefrierschrankElement.setAnzahl(gefrierschrankElement.getAnzahl()-1);

                }
                helpMethodIncAndDec(anzahlTextView, gefrierschrankElement);
                finalView.setBackgroundColor(Color.WHITE);

            }
        });

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues = new ContentValues();
                //contentValues.put(GefrierschrankContract.GefrierschrankEntry.GEFRIERSCHRANK_ESSENSNAME, essensname);
                // wieder sinnvolle Anzahlerweiterung anhand der vorhandenen Anzahl
                if(gefrierschrankElement.getAnzahl() > 99){
                    gefrierschrankElement.setAnzahl(gefrierschrankElement.getAnzahl()+100);

                } else if(gefrierschrankElement.getAnzahl() > 49){
                    gefrierschrankElement.setAnzahl(gefrierschrankElement.getAnzahl()+10);

                } else if(gefrierschrankElement.getAnzahl() > 14){
                    gefrierschrankElement.setAnzahl(gefrierschrankElement.getAnzahl()+5);

                } else {
                    gefrierschrankElement.setAnzahl(gefrierschrankElement.getAnzahl()+1);

                }
                helpMethodIncAndDec(anzahlTextView, gefrierschrankElement);
                finalView.setBackgroundColor(Color.WHITE);
            }
        });

        // komplette Herausnahme eines Lebensmittels aus dem Fach des Gefrierschrankes
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gefrierschrankElement.getFood().setLastFach(gefrierschrankElement.getFach());
                openPopUpWindow(context, gefrierschrankElement);
                deleteNotification(gefrierschrankElement);
                ObjectCollections.removeFromFreezer(gefrierschrankElement);
                gefrierschrankElements.remove(gefrierschrankElement);
                notifyDataSetChanged();

            }
        });


        return view;
    }

    private void deleteNotification(GefrierschrankElement gefrierschrankElement){
        Intent notificationIntent = new Intent(context, AlertReceiver.class);
        notificationIntent.putExtra(AlertReceiver.NOTIFICATION_ID, gefrierschrankElement.getId());
        notificationIntent.putExtra(AlertReceiver.NOTIFICATION_ELEMENT, gefrierschrankElement.getFood().getEssensname());
        switch (gefrierschrankElement.getFach().getGefrierschrank().getTemperature()){
            case HaushaltContract.temperatureFreezer:
                notificationIntent.putExtra(AlertReceiver.NOTIFICATION_DURABILITY, gefrierschrankElement.getFood().getDurabilityFreezer());
                break;
            case HaushaltContract.temperatureFridge:
                notificationIntent.putExtra(AlertReceiver.NOTIFICATION_DURABILITY, gefrierschrankElement.getFood().getDurabilityFridge());
                break;
            case HaushaltContract.temperatureRoom:
                notificationIntent.putExtra(AlertReceiver.NOTIFICATION_DURABILITY, gefrierschrankElement.getFood().getDurabilityRoomTemperature());
                break;
            default:
                return;
        }
        notificationIntent.putExtra(AlertReceiver.NOTIFICATION_PLACE, gefrierschrankElement.getFach().getGefrierschrank().getLabel());
        notificationIntent.putExtra(AlertReceiver.NOTIFICATION_CASE, gefrierschrankElement.getFach().getFachNummer());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) gefrierschrankElement.getId(), notificationIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void helpMethodIncAndDec(TextView anzahlTextView, GefrierschrankElement gefrierschrankElement){
        anzahlTextView.setText(String.valueOf(gefrierschrankElement.getAnzahl()));
        ObjectCollections.gefrierschrankContentChanged = true;
        ObjectCollections.addedGefrierschrankElts.add(gefrierschrankElement.getId());
    }


    private void openPopUpWindow(final Context context, final GefrierschrankElement gefrierschrankElement) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View popupView = (View) inflater.inflate(R.layout.popup_fach, null);

        // festlegen der Groesse des PopUps
        int width = (int) (rootView.getWidth() * 0.95);
        int height = (int) (rootView.getHeight() * 0.4);

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0 ,0);

        //fuellen des PopUps mit den zu loeschenden Lebensmitteldaten
        TextView essensnamePopUp = popupView.findViewById(R.id.essensname_popup);
        essensnamePopUp.setText(gefrierschrankElement.getFood().getEssensname());

        final EditText anzahlPopUp = popupView.findViewById(R.id.anzahl_popup);
        anzahlPopUp.setText(String.valueOf(gefrierschrankElement.getAnzahl()));

        TextView einheitPopUp = popupView.findViewById(R.id.einheit_popup);
        einheitPopUp.setText(UnitsAndCategories.getUnit(gefrierschrankElement.getFood().getEinheit_id()));

        TextView hinweisPopUp = popupView.findViewById(R.id.hinweis_popup);

        costumizeNotice(context, gefrierschrankElement, hinweisPopUp);

        /*
            Falls direkt auf den Einkaufszettel geschrieben werden soll, werden die
            Eintraege des Tables "einkaufszettel" entsprechend veraendert
         */
        Button confirmButton = popupView.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EinkaufszettelElement einkaufszettelElement = new EinkaufszettelElement(gefrierschrankElement.getFood(), Integer.valueOf(anzahlPopUp.getText().toString()));
                ObjectCollections.addToShoppingList(einkaufszettelElement, true);

                Toast toast = Toast.makeText(context.getApplicationContext(), gefrierschrankElement.getFood().getEssensname() + " erfolgreich eingefügt", Toast.LENGTH_SHORT);
                toast.show();
                popupWindow.dismiss();
            }
        });


        Button cancelButton = popupView.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

    }

    // um mittzuteilen, dass sich etwas noch im Gefrierschrank befindet
    private void costumizeNotice(Context context, GefrierschrankElement gefrierschrankElement, TextView hinweisPopUp) {
        // Datenbankabfrage, ob dasselbe Lebensmittel noch irgendwo anders im Gefrierschrank ist
        LinkedList<GefrierschrankElement> gefrierschrankElements = ObjectCollections.getGefrierschrankElements();
        Iterator<GefrierschrankElement> it = gefrierschrankElements.iterator();

        boolean sameFoodExists = false;
        StringBuffer strBuf = new StringBuffer();
        while (it.hasNext()) {
            GefrierschrankElement gefrierschrankElement1 = it.next();
            if (gefrierschrankElement1.getFood().equals(gefrierschrankElement.getFood()) && gefrierschrankElement != gefrierschrankElement1) {
                if (!sameFoodExists) {
                    sameFoodExists = true;
                    strBuf.append("Hinweis:\nDaheim befinden sich aber noch:\n");
                }
                strBuf.append(" - Im ");
                strBuf.append(gefrierschrankElement1.getFach().getGefrierschrank().getLabel() + ": ");
                strBuf.append(gefrierschrankElement1.getFood().getEssensname());
                strBuf.append(": ");
                strBuf.append(gefrierschrankElement1.getAnzahl() + " " + UnitsAndCategories.getUnit(gefrierschrankElement1.getFood().getEinheit_id()));
                strBuf.append("\n");
            }
        }
        if (strBuf.length() != 0) {
            hinweisPopUp.setText(strBuf.toString());
        }
    }
}
