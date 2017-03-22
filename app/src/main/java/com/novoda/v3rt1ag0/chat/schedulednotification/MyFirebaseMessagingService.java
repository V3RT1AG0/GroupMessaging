package com.novoda.v3rt1ag0.chat.schedulednotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.chat.ChatActivity;

import java.util.Map;
import java.util.Random;

/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    private static final String TAG = "MyFirebaseMsgService";
    Map mymap;
    int value;
    String key;
    Intent intent;
    Random rand;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        mymap=remoteMessage.getData();
        rand= new Random();

        if(mymap.containsKey("ChannelId"))
        {
            value = Integer.parseInt(mymap.get("Blogid").toString());
            key="Blogid";
            intent = new Intent(this, ChatActivity.class);
        }
        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getBody(),value,key);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody,int value,String key)
    {
        //Intent intent = new Intent(this, FloatingPost.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(key,value);
        /**this intent is only used when application is running and the given extras are provided.
         * ELSE if the application is closed the Splash screen will receive intent directly from Firebase and not frome here
         * Application ON:Firebase-This Service with extras from firebase-ACtivity specified by pendingintent with extras specified in this service
         * Application Closed:Firebase-Splash Screen with extras from firebase-Activity specified in Splash
         */
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("The Devotee Network")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notifyID = rand.nextInt(100)+1;
        notificationManager.notify(notifyID, notificationBuilder.build());
    }
}
