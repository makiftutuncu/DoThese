package tr.edu.iyte.ceng389.dothese.fragment;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

/** A fragment for showing a time picker dialog
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
	private Calendar cal;
	private TextView timeTV;
	
	public void setCalendar(Calendar cal)
	{
		this.cal = cal;
	}
	
	public void setResultingTextView(TextView timeTV)
	{
		this.timeTV = timeTV;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current time as the default values for the picker
		if(cal == null)
        	cal = Calendar.getInstance();
		
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}
	
	public void onTimeSet(TimePicker view, int hourOfDay, int minute)
	{
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		
		timeTV.setText(DateFormat.getTimeFormat(getActivity()).format(cal.getTime()));
	}
}