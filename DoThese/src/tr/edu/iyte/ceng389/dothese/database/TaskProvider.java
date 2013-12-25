package tr.edu.iyte.ceng389.dothese.database;

import java.util.Arrays;

import tr.edu.iyte.ceng389.dothese.utility.Log;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/** A ContentProvider class to provide access to the task data and expose it to
 * outside of the application
 * 
 * @author ezgihacihalil - 180201058
 * @author mehmetakiftutuncu - 170201018 */
public class TaskProvider extends ContentProvider
{
	/** Name of the tasks database and the tasks table in it */
	private static final String DB_NAME = "tasks";
	/** Version of the tasks database */
	private static final int DB_VERSION = 1;
	
	/** Authority for TaskProvider */
	public static final String AUTHORITY = "tr.edu.iyte.ceng389.dothese.TaskProvider";
	
	/** URI of the tasks provided by TaskProvider */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_NAME);
	
	/** Column name for the row id of the task */
	public static final String KEY_ID = "_id";
	/** Column name for the description of the task */
	public static final String KEY_DESCRIPTION = "description";
	/** Column name for the deadline of the task */
	public static final String KEY_DEADLINE = "deadline";
	/** Column name for the status of the task */
	public static final String KEY_STATUS = "status";
	/** Column name for the priority of the task */
	public static final String KEY_PRIORITY = "priority";
	/** Column name for the notify state of the task */
	public static final String KEY_NOTIFY = "notify";
	
	/** Type of the single content provided by TaskProvider */
	public static final String CONTENT_ITEM_TYPE = "dothese.task";
	/** Type of the directory content provided by TaskProvider */
	public static final String CONTENT_DIR_TYPE = "dothese.tasks";
	
	/** An SQLiteOpenHelper class to provide a database for storing tasks */
	public class TasksDBOpenHelper extends SQLiteOpenHelper
	{
		/** SQL statement that creates the tasks database */
		private static final String CREATE_SQL = "CREATE TABLE " + DB_NAME + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_DESCRIPTION + " TEXT NOT NULL,"
				+ KEY_DEADLINE + " INTEGER NOT NULL,"
				+ KEY_STATUS + " TEXT NOT NULL,"
				+ KEY_PRIORITY + " TEXT NOT NULL,"
				+ KEY_NOTIFY + " INTEGER NOT NULL);";
		
		public TasksDBOpenHelper(Context context)
		{
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(TasksDBOpenHelper.class.getName(), "Upgrading tasks database from version "
					+ oldVersion + " to " + newVersion + " which will destroy all data...");
			
			db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
			
			onCreate(db);
		}
	}
	
	/** SQLiteOpenHelper to provide access to the actual database */
	private TasksDBOpenHelper dbOpenHelper;
	
	// URI match codes for different types
	private static final int ALL_ROWS = 1;
	private static final int SINGLE_ROW = 2;

	// Static initialization of the UriMatcher to match the possible types of queries
	private static final UriMatcher uriMatcher;
	static
	{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "tasks", ALL_ROWS);
		uriMatcher.addURI(AUTHORITY, "tasks" + "/#", SINGLE_ROW);
	}
	
	@Override
	public boolean onCreate()
	{
		dbOpenHelper = new TasksDBOpenHelper(getContext());
		
		return true;
	}

	@Override
	public String getType(Uri uri)
	{
		switch(uriMatcher.match(uri))
		{
			case ALL_ROWS:
				return ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_DIR_TYPE;
			
			case SINGLE_ROW:
				return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_ITEM_TYPE;
			
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		switch(uriMatcher.match(uri))
		{
			case ALL_ROWS:
				SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
				
				long id = db.insert(DB_NAME, null, values);
				
				if(id > -1)
				{
					Uri insertedUri = ContentUris.withAppendedId(CONTENT_URI, id);
					
					getContext().getContentResolver().notifyChange(uri, null, false);
					
					return insertedUri;
				}
				else
					return null;
			
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String whereClause, String[] whereArgs, String sortOrder)
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		builder.setTables(DB_NAME);
		
		switch(uriMatcher.match(uri))
		{
			case SINGLE_ROW:
				String newWhere = TaskProvider.KEY_ID + "=?";
				whereClause = !TextUtils.isEmpty(whereClause) ? whereClause + " AND (" + newWhere + ")" : newWhere;
				
				String rowId = uri.getPathSegments().get(1);
				if(whereArgs != null)
				{
					String[] newWhereArgs = Arrays.copyOf(whereArgs, whereArgs.length);
					newWhereArgs[newWhereArgs.length - 1] = rowId;
					whereArgs = newWhereArgs;
				}
				else
				{
					whereArgs = new String[] {rowId};
				}
				
				Cursor singleCursor = builder.query(db, projection, whereClause, whereArgs, null, null, sortOrder);
				singleCursor.setNotificationUri(getContext().getContentResolver(), uri);
				
				return singleCursor;
			
			case ALL_ROWS:
				Cursor allCursor = builder.query(db, projection, whereClause, whereArgs, null, null, sortOrder);
				allCursor.setNotificationUri(getContext().getContentResolver(), uri);
				
				return allCursor;
			
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs)
	{
		switch(uriMatcher.match(uri))
		{
			case SINGLE_ROW:
				SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
				
				String rowID = uri.getPathSegments().get(1);
				whereClause = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(whereClause) ? " AND (" + whereClause + ")" : "");
				
				int updateCount = db.update(DB_NAME, values, whereClause, whereArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				
				return updateCount;
			
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}
	
	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs)
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int deleteCount = 0;
		
		switch(uriMatcher.match(uri))
		{
			case SINGLE_ROW:
				String rowID = uri.getPathSegments().get(1);
				whereClause = KEY_ID + "=" + rowID + (!TextUtils.isEmpty(whereClause) ? " AND (" + whereClause + ")" : "");
				
				deleteCount = db.delete(DB_NAME, whereClause, whereArgs);
				break;
			
			case ALL_ROWS:
				deleteCount = db.delete(DB_NAME, "1=1", whereArgs);
				break;
			
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return deleteCount;
	}
}