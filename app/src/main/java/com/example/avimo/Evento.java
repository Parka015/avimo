package com.example.avimo;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.TimeZone;

public class Evento extends AppCompatActivity {

    public Boolean crearEvento(String title, long startMillis, long endMillis, String loc,
                               String tags, int all_day, int has_alarm){


        try {
            final ContentValues event = new ContentValues();
            event.put(CalendarContract.Events.CALENDAR_ID, 3);
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

            getApplicationContext().getContentResolver().insert(baseUri, event);

            //Toast.makeText(getApplicationContext(), "Event Created", Toast.LENGTH_SHORT).show();

        } catch (Exception e){
            return false;
        }

        return true;

    }
}
