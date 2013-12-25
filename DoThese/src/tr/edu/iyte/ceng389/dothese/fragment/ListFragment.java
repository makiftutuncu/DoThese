package tr.edu.iyte.ceng389.dothese.fragment;

import tr.edu.iyte.ceng389.dothese.adapter.TaskAdapter;
import tr.edu.iyte.ceng389.dothese.database.TaskProvider;
import tr.edu.iyte.ceng389.dothese.model.Task;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

/** Fragment in which the list of tasks will be shown
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class ListFragment extends android.support.v4.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	public interface OnSetFilterListener
	{
		public void onSetFilter(ListFragment fragment);
	}
	
	private TaskAdapter adapter;
	private OnSetFilterListener onSetFilterListener;
	private int filterIndex;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		try
		{
			onSetFilterListener = (OnSetFilterListener) activity;
		}
		catch(ClassCastException e)
		{
			throw new ClassCastException("Activity must implement ListFragment.OnSetFilterListener!");
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		adapter = new TaskAdapter(getActivity().getApplicationContext());
		setListAdapter(adapter);
		
		onSetFilterListener.onSetFilter(this);
		
		getLoaderManager().initLoader(1, null, this);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		String[] columns = new String[]
		{
			TaskProvider.KEY_ID,
			TaskProvider.KEY_DESCRIPTION,
			TaskProvider.KEY_DEADLINE,
			TaskProvider.KEY_STATUS,
			TaskProvider.KEY_PRIORITY
		};
		
		Task.Priority priority = null;
		
		if(filterIndex == 1)
			priority = Task.Priority.HIGH;
		if(filterIndex == 2)
			priority = Task.Priority.NORMAL;
		if(filterIndex == 3)
			priority = Task.Priority.LOW;
		
		if(priority != null)
			return new CursorLoader(getActivity(), TaskProvider.CONTENT_URI, columns, TaskProvider.KEY_PRIORITY + "=?", new String[] {priority.toString()}, null);
		else
			return new CursorLoader(getActivity(), TaskProvider.CONTENT_URI, columns, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
	{
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		adapter.swapCursor(null);
	}
	
	public void setFilterIndex(int filterIndex)
	{
		this.filterIndex = filterIndex;
	}
}