package tr.edu.iyte.ceng389.dothese.activity;

import tr.edu.iyte.ceng389.dothese.R;
import tr.edu.iyte.ceng389.dothese.database.TaskProvider;
import tr.edu.iyte.ceng389.dothese.fragment.TaskFragment;
import tr.edu.iyte.ceng389.dothese.model.Task;
import tr.edu.iyte.ceng389.dothese.utility.Constants;
import tr.edu.iyte.ceng389.dothese.utility.Log;
import tr.edu.iyte.ceng389.dothese.utility.MenuItemManager;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

/** Activity for the details of tasks, this will contain {@link DetailsFragment}
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class DetailsActivity extends FragmentActivity
{
	private TaskFragment taskFragment;
	private Menu menu;
	
	private boolean isCreateTaskMode = false;
	
	private long taskId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		
		// Show home button in the action bar which will go back to main activity
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			taskFragment = new TaskFragment();
			
			if(extras.containsKey(Constants.EXTRA_CREATE_NEW_TASK))
			{
				Log.d("DetailsActivity", "Started DetailsActivity to create new task.");
				
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fL_detailsContainer, taskFragment);
				transaction.commit();
				
				isCreateTaskMode = true;
			}
			else if(extras.containsKey(Constants.EXTRA_TASK_DETAILS_ID))
			{
				taskId = extras.getLong(Constants.EXTRA_TASK_DETAILS_ID);
				
				if(getIntent().getAction() != null && getIntent().getAction().equals(Constants.EXTRA_TASK_DONE))
				{
					Log.d("DetailsActivity", "Marking the task with id " + taskId + " as completed...");
					
					ContentValues values = new ContentValues();
					values.put(TaskProvider.KEY_STATUS, Task.Status.COMPLETED.toString());
					getContentResolver().update(Uri.withAppendedPath(TaskProvider.CONTENT_URI, "" + taskId), values, null, null);
					
					NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            		notificationManager.cancelAll();
            		
            		finish();
            		
            		startActivity(new Intent(this, MainActivity.class));
				}
				
				Log.d("DetailsActivity", "Started DetailsActivity to show details of a task");
				
				Bundle args = new Bundle();
				args.putLong(Constants.EXTRA_TASK_DETAILS_ID, taskId);
				taskFragment.setArguments(args);
				
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.fL_detailsContainer, taskFragment);
				transaction.commit();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu, menu);
		
		if(this.menu == null)
			this.menu = menu;
		
		if(isCreateTaskMode)
		{
			MenuItemManager.setItemEnabled(menu, R.id.action_save, true);
			MenuItemManager.setItemEnabled(menu, R.id.action_cancel, true);
		}
		else
		{
			MenuItemManager.setItemEnabled(menu, R.id.action_edit, true);
			MenuItemManager.setItemEnabled(menu, R.id.action_delete, true);
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.action_edit:
				if(taskFragment != null)
				{
					taskFragment.setEditMode(true);
				}
				return true;
				
			case R.id.action_cancel:
				if(!isCreateTaskMode && taskFragment != null)
				{
					taskFragment.setEditMode(false);
					return true;
				}
				else
				{
					finish();
					return true;
				}
			
			case R.id.action_delete:
				if(!isCreateTaskMode)
					finish();
				return false;
				
			case android.R.id.home:
				finish();
				startActivity(new Intent(DetailsActivity.this, MainActivity.class));
				return true;
			
			case R.id.action_preferences:
				startActivity(new Intent(DetailsActivity.this, NotificationPreferencesActivity.class));
				return true;
			
			default:
				return false;
		}
	}
}