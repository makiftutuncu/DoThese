package tr.edu.iyte.ceng389.dothese.fragment;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TextView;

/** A fragment for showing a date picker dialog
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
	private Calendar cal;
	private TextView dateTV;
	
	public void setCalendar(Calendar cal)
	{
		this.cal = cal;
	}
	
	public void setResultingTextView(TextView dateTV)
	{
		this.dateTV = dateTV;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// Use the current date as the default date in the picker
        if(cal == null)
        	cal = Calendar.getInstance();
        
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        // Do something with the date chosen by the user
    	cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		
		dateTV.setText(DateFormat.getDateFormat(getActivity()).format(cal.getTime()));
    }
}