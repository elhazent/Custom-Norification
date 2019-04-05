package com.elhazent.picodiploma.customnotificationapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import static com.elhazent.picodiploma.customnotificationapp.NotificationService.CHANNEL_ID;
import static com.elhazent.picodiploma.customnotificationapp.NotificationService.CHANNEL_NAME;
import static com.elhazent.picodiploma.customnotificationapp.NotificationService.REPLY_ACTION;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String KEY_NOTIFICATION_ID = "key_noticiation_id";
    private static String KEY_MESSAGE_ID = "key_message_id";

    public static Intent getReplyMessageIntent(Context context, int notificationId, int messageId){
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction(REPLY_ACTION);
        intent.putExtra(KEY_MESSAGE_ID, messageId);
        intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
        return intent;
    }

    public NotificationBroadcastReceiver(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (REPLY_ACTION.equals(intent.getAction())){
            CharSequence message = NotificationService.getReplyMessage(intent);
            int messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0);

            Toast.makeText(context, "Message ID : " + messageId + "\nMessage: " + message, Toast.LENGTH_SHORT).show();
            int notifyId = intent.getIntExtra(KEY_NOTIFICATION_ID,1);
            updateNotification(context, notifyId);
        }
    }

    private void updateNotification(Context context, int notifyId){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(context.getString(R.string.notif_title_sent))
                .setContentText(context.getString(R.string.notif_content_sent));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            builder.setChannelId(CHANNEL_ID);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }


        Notification notification = builder.build();

        if (notificationManager != null){
            notificationManager.notify(notifyId, notification);
        }
    }
}
