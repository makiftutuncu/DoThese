package tr.edu.iyte.ceng389.dothese.utility;

import tr.edu.iyte.ceng389.dothese.BuildConfig;

/** A utility class for logging
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class Log
{
	/** Tag prefix for logging */
	private static final String LOG_TAG = "DoThese/";
	
	/** Logs a message with debug level */
	public static void d(String tag, String message)
	{
		d(tag, message, null);
	}
	
	/** Logs a message and a stack trace with debug level */
	public static void d(String tag, String message, Throwable tr)
	{
		if(BuildConfig.DEBUG)
		{
			if(tr != null)
				android.util.Log.d(LOG_TAG + tag, message, tr);
			else
				android.util.Log.d(LOG_TAG + tag, message);
		}
	}
	
	/** Logs a message with warning level */
	public static void w(String tag, String message)
	{
		w(tag, message, null);
	}
	
	/** Logs a message and a stack trace with warning level */
	public static void w(String tag, String message, Throwable tr)
	{
		if(tr != null)
			android.util.Log.w(LOG_TAG + tag, message, tr);
		else
			android.util.Log.w(LOG_TAG + tag, message);
	}
	
	/** Logs a message with error level */
	public static void e(String tag, String message)
	{
		e(tag, message, null);
	}
	
	/** Logs a message and a stack trace with error level */
	public static void e(String tag, String message, Throwable tr)
	{
		if(tr != null)
			android.util.Log.e(LOG_TAG + tag, message, tr);
		else
			android.util.Log.e(LOG_TAG + tag, message);
	}
}