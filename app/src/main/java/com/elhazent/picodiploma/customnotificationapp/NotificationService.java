package com.elhazent.picodiploma.customnotificationapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class NotificationService extends IntentService {
    private static final String KEY_REPLY = "key_reply_message";
    public static String REPLY_ACTION = "com.dicoding.notification.directreply.REPLY_ACTION";
    public static String CHANNEL_ID = "channel_01";
    public static CharSequence CHANNEL_NAME = "dicoding channel";

    private int notificationId;
    private int messageId;

    public NotificationService() {
        super("Notification Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            showNotification();
        }
    }

    private void showNotification(){
        notificationId = 1;
        messageId = 123;

        String replyLabel = getString(R.string.notif_action_reply);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build();

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_reply_black_24dp, replyLabel, getReplyPendingIntent())
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(getString(R.string.notif_title_sent))
                .setContentText(getString(R.string.notif_content))
                .setShowWhen(true)
                .addAction(replyAction);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            builder.setChannelId(CHANNEL_ID);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
        Notification notification = builder.build();

        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }
    }

    private PendingIntent getReplyPendingIntent(){
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent = NotificationBroadcastReceiver.getReplyMessageIntent(this, notificationId , messageId);
            return PendingIntent.getBroadcast(getApplicationContext(), 100, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            intent = ReplyActivity.getReplyMessageIntent(this, notificationId, messageId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return PendingIntent.getActivity(this, 100, intent ,PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    public static CharSequence getReplyMessage(Intent intent){
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null){
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }
}
