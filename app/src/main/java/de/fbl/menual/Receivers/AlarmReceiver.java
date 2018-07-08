package de.fbl.menual.Receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import de.fbl.menual.DietPreferencesActivity;
import de.fbl.menual.R;
import de.fbl.menual.utils.Constants;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        notifiy(context);
    }

    public void notifiy(Context context) {
        Intent intent = new Intent(context, DietPreferencesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        createNotificationChannel(context);
        Bitmap mealExample = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.bitmap_example);
/*
        String name = c.getString(str_url);
        URL url_value = new URL(name);
        ImageView profile = (ImageView)v.findViewById(R.id.vdo_icon);
        if (profile != null) {
            Bitmap mIcon1 =
                    BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
            profile.setImageBitmap(mIcon1);
        }*/
        //PendingIntent disLikeIntent = new PendingIntent();
        NotificationCompat.Action likeAction = new NotificationCompat.Action.Builder(R.drawable.like_action, "Like", null).build();
        NotificationCompat.Action dislikeAction = new NotificationCompat.Action.Builder(R.drawable.dislike_action, "Dislike", null).build();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Time to eat!")
                .setContentText("Why don't you try this amazing dish?")
                .setLargeIcon(mealExample)
                .addAction(likeAction)
                .addAction(dislikeAction)
               // .setCustomBigContentView(remoteView("Custom View"))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(mealExample)
                        .bigLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.app_big_icon)))
               // .setStyle(new NotificationCompat.BigTextStyle()
                 //      .bigText("Why don't you try this amazing dish? Available near you until 2:30 pm."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent).setAutoCancel(true);



        // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //first paramater = notification ID
        notificationManager.notify(1, mBuilder.build());
    }
    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Constants.CHANNEL_NAME;
            String description = Constants.CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
