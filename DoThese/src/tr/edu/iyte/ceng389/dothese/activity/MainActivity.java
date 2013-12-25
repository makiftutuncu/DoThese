package tr.edu.iyte.ceng389.dothese.activity;

import tr.edu.iyte.ceng389.dothese.R;
import tr.edu.iyte.ceng389.dothese.adapter.TaskAdapter;
import tr.edu.iyte.ceng389.dothese.adapter.TaskAdapter.Holder;
import tr.edu.iyte.ceng389.dothese.fragment.ListFragment;
import tr.edu.iyte.ceng389.dothese.utility.Constants;
import tr.edu.iyte.ceng389.dothese.utility.Log;
import tr.edu.iyte.ceng389.dothese.utility.MenuItemManager;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

/** Main activity, this is the starting point of the application
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class MainActivity extends FragmentActivity implements OnItemClickListener,
													ActionBar.OnNavigationListener,
													ListFragment.OnSetFilterListener
{
	private FragmentManager fragmentManager;
	private ListFragment listFragment;
	
	private int filterIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fragmentManager = getSupportFragmentManager();
		
		// Set up the action bar to show a dropdown list.
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		// Set up the dropdown list navigation in the action bar.
		getActionBar().setListNavigationCallbacks(new ArrayAdapter<String>(getActionBar().getThemedContext(),
				android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.navigationList)), this);
	}
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId)
	{
		initialize(itemPosition);
		
		return true;
	}
	
	@Override
	public void onSetFilter(ListFragment fragment)
	{
		fragment.setFilterIndex(filterIndex);
		
		listFragment.setEmptyText(getString(R.string.info_noTasks));
		listFragment.getListView().setOnItemClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu, menu);
		
		MenuItemManager.setItemEnabled(menu, R.id.action_new, true);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.action_new:
				Log.d("MainActivity", "Starting DetailsActivity to create a new task...");
				
				Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
				intent.putExtra(Constants.EXTRA_CREATE_NEW_TASK, true);
				startActivity(intent);
				return true;
			
			case R.id.action_preferences:
				startActivity(new Intent(MainActivity.this, NotificationPreferencesActivity.class));
				return true;
			
			default:
				return false;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id)
	{
		Log.d("MainActivity", "Task at " + position + " is clicked.");
		
		Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
		
		// Get id of the task from the holder of this view
		TaskAdapter.Holder holder = (Holder) view.getTag();
		long taskId = holder.id;
		
		Log.d("MainActivity", "Starting DetailsActivity to show details of task with id " + taskId + "...");
		
		intent.putExtra(Constants.EXTRA_TASK_DETAILS_ID, taskId);
		
		startActivity(intent);
	}
	
	private void initialize(int filterIndex)
	{
		Log.d("MainActivity", "Initializing for filter index " + filterIndex + "...");
		
		this.filterIndex = filterIndex;
		
		listFragment = new ListFragment();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.fL_listContainer, listFragment);
		transaction.commit();
	}
}