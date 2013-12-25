package tr.edu.iyte.ceng389.dothese.fragment;

import tr.edu.iyte.ceng389.dothese.R;
import tr.edu.iyte.ceng389.dothese.utility.Constants;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/** Notification preference fragment to choose the time for the notifications
 * that will be created before a task deadline
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class NotificationPreferencesFragment extends PreferenceFragment
{
	/** Preferences object to read and write preferences */
	private SharedPreferences mPreferences;
	
	/** Preference for alarm */
	private ListPreference mAlarmPreference;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.notification_preferences);
        
        initialize();
    }
	
	/**
	 * Initializes preferences activity
	 */
	private void initialize()
	{
		// Initialize preferences
		mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		mAlarmPreference = (ListPreference) findPreference(Constants.PREFERENCE_NOTIFICATION_TIME);
		mAlarmPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue)
			{
				updateAlarmTimeSummary((String) newValue);
				
				return true;
			}
		});
        
        updateAlarmTimeSummary(mPreferences.getString(Constants.PREFERENCE_NOTIFICATION_TIME, getString(R.string.notificationPreferences_time_default)));
	}
	
	private void updateAlarmTimeSummary(String newValue)
	{
		mAlarmPreference.setSummary(getString(R.string.notificationPreferences_time_info, newValue));
	}
}