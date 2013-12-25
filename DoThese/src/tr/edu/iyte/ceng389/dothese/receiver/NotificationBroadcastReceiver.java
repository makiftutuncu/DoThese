package tr.edu.iyte.ceng389.dothese.receiver;

import tr.edu.iyte.ceng389.dothese.R;
import tr.edu.iyte.ceng389.dothese.activity.DetailsActivity;
import tr.edu.iyte.ceng389.dothese.database.TaskProvider;
import tr.edu.iyte.ceng389.dothese.utility.Constants;
import tr.edu.iyte.ceng389.dothese.utility.Log;
import tr.edu.iyte.ceng389.dothese.utility.TaskNotificationManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/** A broadcast receiver that will be launched whenever it is time for a task
 * notification 
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class NotificationBroadcastReceiver extends BroadcastReceiver
{
	private long taskId = -1;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// Get task id from intent and query for that task
		Bundle extras = intent.getExtras();
		if(extras != null)
		{
			taskId = extras.getLong(Constants.EXTRA_TASK_DETAILS_ID, -1);
		}
		
		if(taskId != -1)
		{
			Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(TaskProvider.CONTENT_URI, "" + taskId), new String[] {TaskProvider.KEY_DESCRIPTION}, null, null, null);
			if(cursor != null)
			{
				Log.d("AlarmBroadcastReceiver", "Creating notification for task with id " + taskId + "...");
				
				cursor.moveToFirst();
				String description = cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_DESCRIPTION));
				String remainingMinutes = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREFERENCE_NOTIFICATION_TIME, context.getString(R.string.notificationPreferences_time_default));
				
				createNotification(context, description, remainingMinutes);
				
				TaskNotificationManager.removeNotification(context, taskId);
			}
		}
	}
	
	/**
	 * Creates and schedules a notification as a reminder
	 * 
	 * @param context Context of the activity
	 * @param description Name of the person
	 * @param remainingMinutes Remaining minutes to the deadline of the task
	 */
	private void createNotification(Context context, String description, String remainingMinutes)
	{
		Intent intent = new Intent(context, DetailsActivity.class);
		intent.putExtra(Constants.EXTRA_TASK_DETAILS_ID, taskId);
		intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_RECEIVER_REPLACE_PENDING);
    	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    	
    	Intent doneTaskIntent = new Intent(intent);
    	doneTaskIntent.setAction(Constants.EXTRA_TASK_DONE);
    	PendingIntent pendingDoneTaskIntent = PendingIntent.getActivity(context, 0, doneTaskIntent, 0);
    	
    	NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
		.setContentTitle(description)
		.setContentText(context.getString(R.string.notification_info, remainingMinutes))
		.setTicker(description)
		.setAutoCancel(true)
		.setSmallIcon(R.drawable.icon_notification_small)
		.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_notification_large))
		.addAction(R.drawable.icon_accept, context.getString(R.string.notification_action_done), pendingDoneTaskIntent)
		.setContentIntent(pendingIntent);
		
    	Notification notification = notificationBuilder.build();
    	
    	notification.sound = Uri.parse("content://settings/system/notification_sound");
    	notification.defaults |= Notification.DEFAULT_VIBRATE;
    	
    	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);
	}
}