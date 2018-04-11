package mjcorless.bsse.asu.edu.placemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaceManagerDbHelper extends SQLiteOpenHelper
{
	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "PlaceManager.db";
	
	private static String PLACEDESCRIPTION_CREATE = "CREATE TABLE PlaceDescription (PlaceId INTEGER PRIMARY KEY,Name TEXT,Description TEXT,AddressId INTEGER,Elevation REAL,Latitude REAL,Longitude REAL)";
	private static String ADDRESS_CREATE = "CREATE TABLE Address (AddressId INTEGER PRIMARY KEY, Title TEXT, Address TEXT, City TEXT, State TEXT, ZipCode TEXT)";
	private static String PLACEDESCRIPTION_DELETE = "DROP TABLE IF EXISTS PlaceDescription";
	private static String ADDRESS_DELETE = "DROP TABLE IF EXISTS PlaceDescription";

	public PlaceManagerDbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(PLACEDESCRIPTION_CREATE);
		db.execSQL(ADDRESS_CREATE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// Since this is for an assignment and not production, I am simply throwing away the
		// old db and creating the new one.
		db.execSQL(PLACEDESCRIPTION_DELETE);
		db.execSQL(ADDRESS_DELETE);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onUpgrade(db, oldVersion, newVersion);
	}

}
