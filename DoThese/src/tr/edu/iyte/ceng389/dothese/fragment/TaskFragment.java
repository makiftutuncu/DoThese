package tr.edu.iyte.ceng389.dothese.fragment;

import java.util.Calendar;
import java.util.Date;

import tr.edu.iyte.ceng389.dothese.R;
import tr.edu.iyte.ceng389.dothese.database.TaskProvider;
import tr.edu.iyte.ceng389.dothese.model.Task;
import tr.edu.iyte.ceng389.dothese.model.Task.Priority;
import tr.edu.iyte.ceng389.dothese.utility.Constants;
import tr.edu.iyte.ceng389.dothese.utility.Log;
import tr.edu.iyte.ceng389.dothese.utility.MenuItemManager;
import tr.edu.iyte.ceng389.dothese.utility.TaskNotificationManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/** Fragment in which attributes of a task is shown and edited 
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class TaskFragment extends Fragment implements OnClickListener
{
	private EditText descriptionET;
	private ImageButton deadlineDateIB;
	private TextView deadlineDateTV;
	private ImageButton deadlineTimeIB;
	private TextView deadlineTimeTV;
	private CheckBox notifyCB;
	private Spinner priorityS;
	
	private Calendar deadline;
	private Task.Priority priority;
	
	private Menu menu;
	
	private long taskId = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		deadline = Calendar.getInstance();
		deadline.set(Calendar.SECOND, 0);
		deadline.set(Calendar.MILLISECOND, 0);
		priority = Priority.NORMAL;
		
		setHasOptionsMenu(true);
		
		Log.d("TaskFragment", "Task fragment created.");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_task, container, false);
		
		descriptionET = (EditText) view.findViewById(R.id.eT_task_description);
		deadlineDateIB = (ImageButton) view.findViewById(R.id.iB_task_setDeadlineDate);
		deadlineDateTV = (TextView) view.findViewById(R.id.tV_task_deadlineDate);
		deadlineTimeIB = (ImageButton) view.findViewById(R.id.iB_task_setDeadlineTime);
		deadlineTimeTV = (TextView) view.findViewById(R.id.tV_task_deadlineTime);
		notifyCB = (CheckBox) view.findViewById(R.id.cb_task_notify);
		priorityS = (Spinner) view.findViewById(R.id.s_task_priority);
		
		deadlineDateIB.setOnClickListener(this);
		deadlineTimeIB.setOnClickListener(this);
		deadlineDateTV.setOnClickListener(this);
		deadlineTimeTV.setOnClickListener(this);
		
		deadlineDateTV.setText(DateFormat.getDateFormat(getActivity()).format(deadline.getTime()));
		deadlineTimeTV.setText(DateFormat.getTimeFormat(getActivity()).format(deadline.getTime()));
		
		priorityS.setSelection(1);
		priorityS.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, int position, long id)
			{
				priority = position != AdapterView.INVALID_POSITION ? Priority.values()[position] : Priority.NORMAL;
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter)
			{
				priority = Priority.NORMAL;
			}
		});
		
		if(getArguments() != null)
		{
			setEditMode(!getArguments().containsKey(Constants.EXTRA_TASK_DETAILS_ID));
			
			taskId = getArguments().getLong(Constants.EXTRA_TASK_DETAILS_ID, -1);
			
			load();
		}
		else
			setEditMode(true);
		
		Log.d("TaskFragment", "Task fragment UI is created.");
		
		return view;
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.iB_task_setDeadlineDate || v.getId() == R.id.tV_task_deadlineDate)
		{
			DatePickerFragment datePickerFragment = new DatePickerFragment();
			datePickerFragment.setCalendar(deadline);
			datePickerFragment.setResultingTextView(deadlineDateTV);
		    datePickerFragment.show(getFragmentManager(), "datePicker");
		}
		else if(v.getId() == R.id.iB_task_setDeadlineTime || v.getId() == R.id.tV_task_deadlineTime)
		{
			TimePickerFragment timePickerFragment = new TimePickerFragment();
			timePickerFragment.setCalendar(deadline);
			timePickerFragment.setResultingTextView(deadlineTimeTV);
		    timePickerFragment.show(getFragmentManager(), "timePicker");
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		
		this.menu = menu;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.action_save:
				if(validateInput())
				{
					Task task = new Task(descriptionET.getText().toString(), deadline.getTime(), Task.Status.NOT_COMPLETED, priority, notifyCB.isChecked());
					
					if(taskId != -1)
						task.setId(taskId);
					
					save(task);
					
					setEditMode(false);
					
					Toast.makeText(getActivity(), R.string.info_saved, Toast.LENGTH_SHORT).show();
					
					if(task.getNotifyState())
					{
						TaskNotificationManager.setNotification(getActivity(), task.getId());
						
						// Notify user
			            Toast.makeText(getActivity(), R.string.info_notificationSet, Toast.LENGTH_SHORT).show();
					}
					else
					{
						TaskNotificationManager.removeNotification(getActivity(), task.getId());
						
						// Notify user
			            Toast.makeText(getActivity(), R.string.info_notificationRemoved, Toast.LENGTH_SHORT).show();
					}
					
					return true;
				}
				
				return false;
				
			case R.id.action_delete:
				delete();
				
				return true;
			
			default:
				return false;
		}
	}

	public boolean validateInput()
	{
		boolean isDescriptionValid = descriptionET.length() > 0;
		boolean isDeadlineValid = Calendar.getInstance().before(deadline);
		
		if(!isDescriptionValid)
		{
			Toast.makeText(getActivity(), R.string.error_invalidDescription, Toast.LENGTH_LONG).show();
			
			return false;
		}
		
		if(!isDeadlineValid)
		{
			Toast.makeText(getActivity(), R.string.error_invalidDeadline, Toast.LENGTH_LONG).show();
			
			return false;
		}
		
		return isDescriptionValid && isDeadlineValid;
	}
	
	public void setEditMode(boolean isEditMode)
	{
		descriptionET.setEnabled(isEditMode);
		deadlineDateIB.setEnabled(isEditMode);
		deadlineDateTV.setEnabled(isEditMode);
		deadlineTimeIB.setEnabled(isEditMode);
		deadlineTimeTV.setEnabled(isEditMode);
		notifyCB.setEnabled(isEditMode);
		priorityS.setEnabled(isEditMode);
		
		MenuItemManager.setItemEnabled(menu, R.id.action_save, isEditMode);
		MenuItemManager.setItemEnabled(menu, R.id.action_cancel, isEditMode);
		MenuItemManager.setItemEnabled(menu, R.id.action_edit, !isEditMode);
		MenuItemManager.setItemEnabled(menu, R.id.action_delete, !isEditMode);
	}
	
	private void load()
	{
		String[] columns = new String[]
		{
			TaskProvider.KEY_DESCRIPTION,
			TaskProvider.KEY_DEADLINE,
			TaskProvider.KEY_PRIORITY,
			TaskProvider.KEY_NOTIFY
		};
		
		Cursor cursor = getActivity().getContentResolver().query(Uri.withAppendedPath(TaskProvider.CONTENT_URI, "" + taskId), columns, null, null, null);
		
		if(cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			
			String description = cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_DESCRIPTION));
			descriptionET.setText(description);
			
			Date deadlineDate = new Date(cursor.getLong(cursor.getColumnIndex(TaskProvider.KEY_DEADLINE)));
			deadline.setTime(deadlineDate);
			deadlineDateTV.setText(DateFormat.getDateFormat(getActivity()).format(deadline.getTime()));
			deadlineTimeTV.setText(DateFormat.getTimeFormat(getActivity()).format(deadline.getTime()));
			
			boolean notifyStatus = cursor.getInt(cursor.getColumnIndex(TaskProvider.KEY_NOTIFY)) == 1 ? true : false;
			notifyCB.setChecked(notifyStatus);
			
			priority = Task.Priority.valueOf(cursor.getString(cursor.getColumnIndex(TaskProvider.KEY_PRIORITY)));
			priorityS.setSelection(priority.numericValue - 1);
		}
	}
	
	public void save(Task task)
	{
		ContentValues values = new ContentValues();
		values.put(TaskProvider.KEY_DESCRIPTION, task.getDescription());
		values.put(TaskProvider.KEY_DEADLINE, task.getDeadline().getTime());
		values.put(TaskProvider.KEY_STATUS, task.getStatus().toString());
		values.put(TaskProvider.KEY_PRIORITY, task.getPriority().toString());
		values.put(TaskProvider.KEY_NOTIFY, notifyCB.isChecked());
		
		if(task.getId() == -1)
		{
			Uri uri = getActivity().getContentResolver().insert(TaskProvider.CONTENT_URI, values);
			long id = Long.valueOf(uri.getPathSegments().get(1));
			task.setId(id);
			
			Log.d("TaskFragment", "Task added: " + task.toString());
		}
		else
		{
			values.put(TaskProvider.KEY_ID, task.getId());
			
			getActivity().getContentResolver().update(Uri.withAppendedPath(TaskProvider.CONTENT_URI, "" + task.getId()), values, null, null);
			
			Log.d("TaskFragment", "Task updated: " + task.toString());
		}
	}
	
	public void delete()
	{
		if(taskId != -1)
		{
			getActivity().getContentResolver().delete(Uri.withAppendedPath(TaskProvider.CONTENT_URI, "" + taskId), null, null);
			
			TaskNotificationManager.removeNotification(getActivity(), taskId);
			
			Toast.makeText(getActivity(), R.string.info_deleted, Toast.LENGTH_SHORT).show();
			
			Log.d("TaskFragment", "Task with id " + taskId + " is deleted.");
		}
	}
}