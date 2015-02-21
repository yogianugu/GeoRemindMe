package edu.uncc.georemindme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class ProximityIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		Boolean entering = intent.getBooleanExtra(key, false);
		if (entering) {
			Log.d(getClass().getSimpleName(), "entering");
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent notificationIntent = new Intent(context,
					NotifyActivity.class);
			notificationIntent.putExtra("text", "You entered the proximity mentioned by:" + intent.getStringExtra("name"));
			notificationIntent.putExtra("title", intent.getStringExtra("desc"));
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);

			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
					context)
					.setWhen(System.currentTimeMillis())
					.setContentText("You entered the proximity mentioned by:" + intent.getStringExtra("name"))
					.setContentTitle(intent.getStringExtra("desc"))
					.setSmallIcon(R.drawable.ic_launcher)
					.setAutoCancel(true)
					.setTicker("Reminder")
					.setLights(Color.YELLOW, 1500, 1500)
					.setDefaults(
							Notification.DEFAULT_VIBRATE
									| Notification.DEFAULT_SOUND)
					.setContentIntent(pendingIntent);

			Notification notification = notificationBuilder.build();

			notificationManager.notify((int) System.currentTimeMillis(),
					notification);
		} else {
			Log.d(getClass().getSimpleName(), "exiting");
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent notificationIntent = new Intent(context,
					NotifyActivity.class);
			notificationIntent.putExtra("text", "You entered the proximity mentioned by:" + intent.getStringExtra("name"));
			notificationIntent.putExtra("title", intent.getStringExtra("desc"));
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);


			NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
					context)
					.setWhen(System.currentTimeMillis())
					.setContentText("You exited the proximity mentioned by:" + intent.getStringExtra("name"))
					.setContentTitle(intent.getStringExtra("desc"))
					.setSmallIcon(R.drawable.ic_launcher)
					.setAutoCancel(true)
					.setTicker("Reminder")
					.setLights(Color.YELLOW, 1500, 1500)
					.setDefaults(
							Notification.DEFAULT_VIBRATE
									| Notification.DEFAULT_SOUND)
					.setContentIntent(pendingIntent);

			Notification notification = notificationBuilder.build();

			
			notificationManager.notify((int) System.currentTimeMillis(),
					notification);
		}
		
	}
	

}
