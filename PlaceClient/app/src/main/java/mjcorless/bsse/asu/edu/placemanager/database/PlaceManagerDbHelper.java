package mjcorless.bsse.asu.edu.placemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Copyright 2018 Matthew Corless
 * This code is free to use for educational purposes.
 *
 * @author Matthew Corless
 * mailto: mjcorless@asu.edu
 * @version April 14, 2018
 * <p>
 * Helps manage the SQLite database for the app
 */
public class PlaceManagerDbHelper extends SQLiteOpenHelper
{
	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "PlaceManager.db";

	private static String PLACEDESCRIPTION_CREATE = "CREATE TABLE PlaceDescription (PlaceId INTEGER PRIMARY KEY,Name TEXT,Category TEXT,Description TEXT,AddressTitle TEXT, Address TEXT,Elevation REAL,Latitude REAL,Longitude REAL)";
	private static String PLACEDESCRIPTION_DELETE = "DROP TABLE IF EXISTS PlaceDescription";


	public PlaceManagerDbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(PLACEDESCRIPTION_CREATE);
		initialInserts(db);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// Since this is for an assignment and not production, I am simply throwing away the
		// old db and creating the new one.
		db.execSQL(PLACEDESCRIPTION_DELETE);
		onCreate(db);
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onUpgrade(db, oldVersion, newVersion);
	}

	private void initialInserts(SQLiteDatabase db)
	{
		/* commented out since we are syncing from jsonrpc server
		//"CREATE TABLE PlaceDescription (PlaceId,Name,Category,Description,AddressTitle, Address,Elevation,Latitude,Longitude)";
		ContentValues values1 = new ContentValues();
		values1.put("Name", "Gilbert, Arizona");
		values1.put("Category", "School");
		values1.put("Description", "Arizona State University");
		values1.put("AddressTitle", "Polytechnic Campus");
		values1.put("Address", "7001 E Williams Field Rd, Mesa, AZ 85212");
		values1.put("Elevation", "413");
		values1.put("Longitude", "-111.68");
		values1.put("Latitude", "33.31");

		ContentValues values2 = new ContentValues();
		values2.put("Name", "Madison, Wisconsin");
		values2.put("Category", "School");
		values2.put("Description", "University of Wisconsin: Madison");
		values2.put("AddressTitle", "Admissions Office");
		values2.put("Address", "70702 West Johnson Street Suite 1101  Madison, WI 53715–1007");
		values2.put("Elevation", "263");
		values2.put("Longitude", "-89.40");
		values2.put("Latitude", "43.07");

		ContentValues values3 = new ContentValues();
		values3.put("Name", "White House");
		values3.put("Category", "Government");
		values3.put("Description", "Home of Donald Trump");
		values3.put("AddressTitle", "White House Address");
		values3.put("Address", "1600 Pennsylvania Ave NW, Washington, DC 20500");
		values3.put("Elevation", "19");
		values3.put("Longitude", "-77.03");
		values3.put("Latitude", "38.90");

		ContentValues values4 = new ContentValues();
		values4.put("Name", "Eiffel Tower");
		values4.put("Category", "Tourist Attraction");
		values4.put("Description", "Lattice Tower in Paris, France");
		values4.put("AddressTitle", "Eiffel Tower Address");
		values4.put("Address", "Champ de Mars, 5 Avenue Anatole France, 75007 Paris, France");
		values4.put("Elevation", "33");
		values4.put("Longitude", "2.29");
		values4.put("Latitude", "48.86");

		long newRowId1 = db.insert("PlaceDescription", null, values1);
		long newRowId2 = db.insert("PlaceDescription", null, values2);
		long newRowId3 = db.insert("PlaceDescription", null, values3);
		long newRowId4 = db.insert("PlaceDescription", null, values4);
		*/
	}
}
