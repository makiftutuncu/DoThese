package tr.edu.iyte.ceng389.dothese.adapter;

import java.util.Date;

import tr.edu.iyte.ceng389.dothese.R;
import tr.edu.iyte.ceng389.dothese.database.TaskProvider;
import tr.edu.iyte.ceng389.dothese.model.Task;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/** A cursor adapter that binds the tasks with the list UI
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class TaskAdapter extends CursorAdapter
{
	/** Holder class that will keep the references to the components of an item
	 * layout */
	public static class Holder
	{
		public long id;
		public CheckBox status;
		public TextView description;
	}
	
	/** {@link Context} of the activity that uses this adapter */
	private Context mContext;
	
	/** {@link LayoutInflater} for creating new views */
	private LayoutInflater inflater;
	
	// Constructor
	public TaskAdapter(Context context)
	{
		super(context, null, false);
	
		mContext = context;
		
		// Get layout inflater
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		// Get the holder of this view
		Holder holder = (Holder) view.getTag();
		
		// Read values from cursor
		long id = cursor.getLong(cursor.getColumnIndex(TaskProvider.KEY_ID));
		String description = cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_DESCRIPTION));
		Task.Status status = Task.Status.valueOf(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_STATUS)));
		Date deadline = new Date(cursor.getLong(cursor.getColumnIndex(TaskProvider.KEY_DEADLINE)));
		
		// Set the UI from the information of the current task
		if(status.equals(Task.Status.COMPLETED))
		{
			holder.description.setTextColor(mContext.getResources().getColor(R.color.task_done));
		}
		else
		{
			if(deadline.before(new Date()))
			{
				holder.description.setTextColor(mContext.getResources().getColor(R.color.task_overdue));
			}
			else
			{
				holder.description.setTextColor(mContext.getResources().getColor(R.color.task_not_done));
			}
		}
		holder.id = id;
		holder.status.setChecked(status.equals(Task.Status.COMPLETED) ? true : false);
		holder.description.setText(description);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		// Inflate a new view
		View view = inflater.inflate(R.layout.item_task, parent, false);
		
		// Create a new holder for this view
		Holder newHolder = new Holder();
		
		// Keep the references to this view's components in the holder
		newHolder.description = (TextView) view.findViewById(R.id.tV_description);
		newHolder.status = (CheckBox) view.findViewById(R.id.cb_status);
		
		/* Set the check box on checked change listener so when the state is
		 * changed, status attribute of the task will be changed as well */
		newHolder.status.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				View viewParent = (View) buttonView.getParent();
				Holder holder = (Holder) viewParent.getTag();
				
				if(holder != null)
				{
					long id = holder.id;
					ContentValues values = new ContentValues();
					values.put(TaskProvider.KEY_STATUS, isChecked ? Task.Status.COMPLETED.toString() : Task.Status.NOT_COMPLETED.toString());
					
					mContext.getContentResolver().update(Uri.withAppendedPath(TaskProvider.CONTENT_URI, "" + id), values, null, null);
				}
			}
		});
		
		/* Set the holder as the tag of this view object so it can be
		 * accessed later */ 
		view.setTag(newHolder);
		
		return view;
	}
}