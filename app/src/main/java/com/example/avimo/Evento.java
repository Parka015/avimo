package com.example.avimo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class Evento extends AppCompatActivity {

    public Boolean crearEvento(Activity act, String title, long startMillis, long endMillis, String loc,
                            String tags, int all_day, int has_alarm){

        try {
            final ContentValues event = new ContentValues();
            event.put(CalendarContract.Events.CALENDAR_ID, 1);
            event.put(CalendarContract.Events.TITLE, title);
            event.put(CalendarContract.Events.DESCRIPTION, tags);
            event.put(CalendarContract.Events.EVENT_LOCATION, loc);
            event.put(CalendarContract.Events.DTSTART, startMillis);//startTimeMillis
            event.put(CalendarContract.Events.DTEND, endMillis);//endTimeMillis
            event.put(CalendarContract.Events.ALL_DAY, all_day);   // 0 for false, 1 for true
            event.put(CalendarContract.Events.HAS_ALARM, has_alarm); // 0 for false, 1 for true
            String timeZone = TimeZone.getDefault().getID();
            event.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone);
            Uri baseUri;

            if (Build.VERSION.SDK_INT >= 8) {
                baseUri = Uri.parse("content://com.android.calendar/events");
            } else {
                baseUri = Uri.parse("content://calendar/events");
            }

            act.getApplicationContext().getContentResolver().insert(baseUri, event);

            //Toast.makeText(getApplicationContext(), "Event Created", Toast.LENGTH_SHORT).show();

        } catch (Exception e){
            return false;
        }

        return true;
    }

    public void listarEventos(Activity act,ArrayList<Calendar> fechas,
                              ArrayList<String> nombre_eventos, ArrayList<String> fechas_ini)
    {

        if(!fechas.isEmpty()) {

            long startMillis = fechas.get(0).getTimeInMillis();
            long endMillis = fechas.get(1).getTimeInMillis();

            // Construct the query with the desired date range.
            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);

            Cursor cursor = act.getContentResolver()
                    .query(
                            builder.build(),
                            new String[]{"calendar_id", "title", "description",
                                    "dtstart"}, null,
                            null, "dtstart");

            cursor.moveToFirst();
            // fetching calendars name
            int tam = cursor.getCount();


            for (int i = 0; i < tam; i++) {

                nombre_eventos.add(cursor.getString(1));
                fechas_ini.add(cursor.getString(3));

                cursor.moveToNext();

            }
        }

    }

    public String buscarEvento(Activity act,String titulo, Calendar cal_ini){

        String id = "";

        int prox_2_anio = Calendar.getInstance().get(Calendar.YEAR)+2;
        Calendar cal_fin = Calendar.getInstance();
        cal_fin.set(Calendar.YEAR,prox_2_anio);

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, Calendar.getInstance().getTimeInMillis());
        ContentUris.appendId(builder, cal_fin.getTimeInMillis());

        Cursor cursor = act.getContentResolver()
                .query(
                        builder.build(),
                        new String[] { "event_id", "title","dtstart"},
                        null,
                        null, "dtstart");

        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];


        Boolean encontrado = false;

        for (int i = 0; i < CNames.length && !encontrado; i++) {

            if(titulo.equals(cursor.getString(1).toLowerCase()) ) {

                id = cursor.getString(0);
                cal_ini.setTimeInMillis(Long.parseLong(cursor.getString(2)));
                encontrado = true;

            }else
                cursor.moveToNext();

        }

        return id;

    }

    public Boolean modificarEvento(Activity act, String id, String title, long startMillis, long endMillis,
                               String loc, String tag)
    {
        try {

            long eventID = Long.parseLong(id);

            ContentValues values = new ContentValues();
            Uri updateUri = null;

            //Atributos a cambiar

            if(title !="")
                values.put(CalendarContract.Events.TITLE, title);

            if(startMillis != -1 && startMillis != -1) {
                values.put(CalendarContract.Events.DTSTART, startMillis);
                values.put(CalendarContract.Events.DTEND, endMillis);
            }

            if(loc !="")
                values.put(CalendarContract.Events.EVENT_LOCATION, loc);

            if(tag !="")
                values.put(CalendarContract.Events.DESCRIPTION, tag);


            updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
            int rows = act.getApplicationContext().getContentResolver().update(updateUri, values, null, null);

        } catch (Exception e){
            return false;
        }

        return true;
    }

    public Boolean eliminarEvento(Activity act, String id){
        try {

            long eventID = Long.parseLong(id);

            ContentValues values = new ContentValues();
            Uri deleteUri = null;


            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
            int rows = act.getApplicationContext().getContentResolver().delete(deleteUri, null, null);

        } catch (Exception e){
            return false;
        }

        return true;
    }
}
