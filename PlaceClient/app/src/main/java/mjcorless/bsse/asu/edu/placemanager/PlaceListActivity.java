package mjcorless.bsse.asu.edu.placemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mjcorless.bsse.asu.edu.placemanager.database.PlaceManagerDbHelper;
import mjcorless.bsse.asu.edu.placemanager.models.AsyncCollectionConnect;
import mjcorless.bsse.asu.edu.placemanager.models.MethodInformation;
import mjcorless.bsse.asu.edu.placemanager.models.PlaceDescription;

/**
 * Copyright 2018 Matthew Corless
 * This code is free to use for educational purposes.
 *
 * @author Matthew Corless
 * mailto: mjcorless@asu.edu
 * @version April 14, 2018
 * <p>
 * Lists the places in the database. Acts as the main activity of the app.
 */
public class PlaceListActivity extends AppCompatActivity
{
	private PlaceManagerDbHelper dbHelper;
	private ListView listView;
	private Button addBtn;
	public ArrayAdapter<String> adapter;
	public String[] names;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_list);

		names = new String[]{"unknown"};
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<>(Arrays.asList(names)));
		// Get ListView object from xml
		listView = findViewById(R.id.list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				// ListView Clicked item index
				int itemPosition = position;

				// ListView Clicked item value
				String itemValue = (String) listView.getItemAtPosition(position);
				int idIndex = itemValue.indexOf('.');
				int placeId = getPlaceID(itemValue);
				Bundle bundle = new Bundle();
				bundle.putInt("PlaceId", placeId);
				goToListView(bundle);
			}
		});

		addBtn = findViewById(R.id.addPlaceBtn);
		addBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Bundle bundle = new Bundle();
				bundle.putInt("PlaceId", 0);
				goToListView(bundle);
			}
		});

		Button distanceBtn = findViewById(R.id.distanceBtn);
		distanceBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				goToDistanceView();
			}
		});

		Button syncBtn = findViewById(R.id.syncBtn);
		syncBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				syncDatabase();
			}
		});

		Button mapBtn = findViewById(R.id.mapBtn);
		mapBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				goToMapView();
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Bundle extras = getIntent().getExtras();
		String deleteName;

		if (extras != null)
		{
			System.out.println("DELETE");
			deleteName = extras.getString("delete");

			syncFromDataBase();

			// add the names to server
			MethodInformation mi = new MethodInformation(this, "http://10.0.2.2:8080/", "remove", new Object[]{deleteName});
			AsyncCollectionConnect ac = (AsyncCollectionConnect) new AsyncCollectionConnect().execute(mi);

			adapter.remove(deleteName);
			adapter.notifyDataSetChanged();
		}
		else
		{
			initialSync();
		}
	}

	/**
	 * Creates an intent with the given bundle. Starts the PlaceListActivity.
	 *
	 * @param bundle Bundle to encapsulate in the new activity's intent
	 */
	private void goToListView(Bundle bundle)
	{
		Intent intent = new Intent(this, CreatePlaceActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * Creates an intent to go to the DistanceActivity.
	 */
	private void goToDistanceView()
	{
		Intent intent = new Intent(this, DistanceActivity.class);
		startActivity(intent);
	}

	/**
	 * Initializes the database and list with the server's places
	 */
	private void initialSync()
	{
		// get URL
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String urlString = preferences.getString("url", "http://10.0.2.2:8080/");

		try
		{
			// get the names from server
			MethodInformation mi = new MethodInformation(this, urlString, "getNames", new String[]{});
			AsyncCollectionConnect ac = (AsyncCollectionConnect) new AsyncCollectionConnect().execute(mi);
		}
		catch (Exception e)
		{
			Toast.makeText(this, "Unable to initialize database with server:.", Toast.LENGTH_LONG).show();
		}
	}

	private void syncFromDataBase()
	{
		dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		//for (String name: )
		// get the local names
		Cursor cursor = db.rawQuery("SELECT * FROM PlaceDescription where PlaceId > ?", new String[]{"0"});
		List<String> dbNames = new ArrayList<String>();

		while (cursor.moveToNext())
		{
			String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
			dbNames.add(name);
		}
		cursor.close();

		adapter.clear();
		adapter.addAll(dbNames);
	}

	private void syncDatabase()
	{
		Button syncBtn = findViewById(R.id.syncBtn);
		syncBtn.setText("Syncing");

		// get URL
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String urlString = preferences.getString("url", "http://10.0.2.2:8080/");

		dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		//for (String name: )
		// get the local names
		Cursor cursor = db.rawQuery("SELECT * FROM PlaceDescription where PlaceId > ?", new String[]{"0"});
		List<PlaceDescription> localPlaces = new ArrayList<PlaceDescription>();
		while (cursor.moveToNext())
		{
			PlaceDescription place = new PlaceDescription();
			place.setName(cursor.getString(cursor.getColumnIndexOrThrow("Name")));
			place.setCategory(cursor.getString(cursor.getColumnIndexOrThrow("Category")));
			place.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("Description")));
			place.setAddressTitle(cursor.getString(cursor.getColumnIndexOrThrow("AddressTitle")));
			place.setAddressPostal(cursor.getString(cursor.getColumnIndexOrThrow("Address")));
			place.setElevation(Float.parseFloat(cursor.getString(cursor.getColumnIndexOrThrow("Elevation"))));
			place.setLongitude(Float.parseFloat(cursor.getString(cursor.getColumnIndexOrThrow("Longitude"))));
			place.setLatitude(Float.parseFloat(cursor.getString(cursor.getColumnIndexOrThrow("Latitude"))));
			localPlaces.add(place);
		}
		cursor.close();

		for (PlaceDescription place : localPlaces)
		{
			try
			{
				// add the names to server
				MethodInformation mi = new MethodInformation(this, urlString, "add", new Object[]{place.toJson()});
				AsyncCollectionConnect ac = (AsyncCollectionConnect) new AsyncCollectionConnect().execute(mi);
			}
			catch (Exception e)
			{
				Toast.makeText(this, "Unable to add " + place.getName() + " to server", Toast.LENGTH_LONG).show();
			}

		}


		syncBtn.setText(getResources().getString(R.string.syncBtn));
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_favorite:
				// User chose the "Settings" item, show the app settings UI...
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return true;

			default:
				// If we got here, the user's action was not recognized.
				// Invoke the superclass to handle it.
				return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	private int getPlaceID(String name)
	{
		dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		int id = 0;
		//for (String name: )
		// get the local names
		Cursor cursor = db.rawQuery("SELECT * FROM PlaceDescription where Name = ?", new String[]{name});
		while (cursor.moveToNext())
		{
			id = cursor.getInt(cursor.getColumnIndexOrThrow("PlaceId"));
		}
		cursor.close();
		return id;
	}

	private void goToMapView()
	{
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}
}
