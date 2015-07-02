package com.liferay.mobile.screens.osos.push;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.liferay.mobile.screens.osos.R;
import com.liferay.mobile.screens.osos.activities.MainActivity;
import com.liferay.mobile.screens.util.LiferayLogger;

import org.json.JSONException;
import org.json.JSONObject;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LiferayLogger.e("Push Received: " + intent.toString());

		String payload = intent.getExtras().getString("payload");

		try {
			JSONObject jsonObject = new JSONObject(payload);

			String message = jsonObject.getString("body");

			setResultCode(Activity.RESULT_OK);

			sendNotification(context, message);
		}
		catch (JSONException e) {
			LiferayLogger.e("Error trying to parse message", e);
		}
	}

	private void sendNotification(Context context, String message) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
			PendingIntent.FLAG_ONE_SHOT);

		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_stat_ic_notification)
			.setContentTitle("OSOS Notification.")
			.setContentText(message)
			.setAutoCancel(true)
			.setSound(defaultSoundUri)
			.setContentIntent(pendingIntent);

		NotificationManager notificationManager =
			(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
	}
}