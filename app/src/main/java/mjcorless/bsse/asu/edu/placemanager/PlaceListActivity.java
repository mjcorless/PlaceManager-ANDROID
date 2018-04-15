package mjcorless.bsse.asu.edu.placemanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import mjcorless.bsse.asu.edu.placemanager.database.PlaceManagerDbHelper;

/**
 * Copyright 2018 Your Name
 * This code is free to use for educational purposes.
 *
 * @author Matthew Corless
 * mailto: mjcorless@asu.edu
 * <p>
 * Lists the places in the database. Acts as the main activity of the app.
 */
public class PlaceListActivity extends AppCompatActivity
{
	private PlaceManagerDbHelper dbHelper;
	private ListView listView;
	private Button addBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_list);

		// Get ListView object from xml
		listView = findViewById(R.id.list);

		dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT * FROM PlaceDescription where PlaceId > ?", new String[]{"0"});
		List names = new ArrayList();
		while (cursor.moveToNext())
		{
			int place = cursor.getInt(cursor.getColumnIndexOrThrow("PlaceId"));
			String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
			names.add(place + ".  " + name);
		}
		cursor.close();

		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
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
				int placeId = Integer.parseInt(itemValue.substring(0, idIndex));

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
}
