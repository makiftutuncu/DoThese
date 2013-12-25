package tr.edu.iyte.ceng389.dothese.activity;

import tr.edu.iyte.ceng389.dothese.fragment.NotificationPreferencesFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

/** Notification preference activity to choose the time for the notifications
 * that will be created before a task deadline
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class NotificationPreferencesActivity extends FragmentActivity
{
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        getFragmentManager().beginTransaction().replace(android.R.id.content, new NotificationPreferencesFragment()).commit();
        
        // Show home button in the action bar which will go back to main activity
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				finish();
				return true;
			
			default:
				return false;
		}
	}
}