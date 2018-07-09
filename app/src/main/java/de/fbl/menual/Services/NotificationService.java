package de.fbl.menual.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;

import de.fbl.menual.Receivers.AlarmReceiver;

public class NotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Timer mTimer;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();

        alarmMgr = (AlarmManager)getBaseContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);

    // Set the alarm to start at 11:30 PM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 30);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
        SimpleDateFormat format = new SimpleDateFormat();

        System.out.println("DataFormat" + format.format(calendar.getTimeInMillis()));
    }
}
