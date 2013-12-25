package tr.edu.iyte.ceng389.dothese.utility;

import java.util.Calendar;

import tr.edu.iyte.ceng389.dothese.R;
import tr.edu.iyte.ceng389.dothese.database.TaskProvider;
import tr.edu.iyte.ceng389.dothese.receiver.NotificationBroadcastReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

/** A utility class for managing notifications of tasks
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class TaskNotificationManager
{
	/**
     * Sets up the notification alarm for the given task
     */
	public static void setNotification(Context context, long taskId)
	{
		// Remove previous notification alarms for this task
		removeNotification(context, taskId);
		
		Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(TaskProvider.CONTENT_URI, "" + taskId), new String[] {TaskProvider.KEY_DEADLINE, TaskProvider.KEY_NOTIFY}, null, null, null);
    	if(cursor != null)
    	{
    		Log.d("TaskNotificationManager", "Removing notification alarm for task with id " + taskId);
    		
    		cursor.moveToFirst();
    		
    		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        	
    		// Set broadcast intent
    		Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
    		intent.setAction(context.getString(R.string.notificationReceiverAction));
    		intent.putExtra(Constants.EXTRA_TASK_DETAILS_ID, taskId);
        	PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        	
        	// Set calendar to the deadline of task
        	Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(TaskProvider.KEY_DEADLINE)));
            
            // Get minutes from shared preferences
            int minutes = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREFERENCE_NOTIFICATION_TIME, context.getString(R.string.notificationPreferences_time_default)));
            
            // Set notification time
            calendar.add(Calendar.MINUTE, minutes * -1);
            
            // Finally set the alarm
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            
            // Update notification status of the task
    		ContentValues values = new ContentValues();
    		values.put(TaskProvider.KEY_NOTIFY, 1);
    		context.getContentResolver().update(Uri.withAppendedPath(TaskProvider.CONTENT_URI, "" + taskId), values, null, null);
    	}
	}
	
	/**
     * Removes the notification alarm for the given task
     */
	public static void removeNotification(Context context, long taskId)
	{
		Log.d("TaskNotificationManager", "Removing notification alarm for task with id " + taskId);
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    	
		// Set broadcast intent
		Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
		intent.setAction(context.getString(R.string.notificationReceiverAction));
		intent.putExtra(Constants.EXTRA_TASK_DETAILS_ID, taskId);
    	PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	// Cancel set the alarm
        alarmManager.cancel(sender);
        
        // Update notification status of the task
		ContentValues values = new ContentValues();
		values.put(TaskProvider.KEY_NOTIFY, 0);
		context.getContentResolver().update(Uri.withAppendedPath(TaskProvider.CONTENT_URI, "" + taskId), values, null, null);
	}
}