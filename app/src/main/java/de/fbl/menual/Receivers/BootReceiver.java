package de.fbl.menual.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.fbl.menual.Services.NotificationService;

public class BootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationService.class));
    }
}
