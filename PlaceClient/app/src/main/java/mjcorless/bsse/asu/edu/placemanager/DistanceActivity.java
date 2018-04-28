package mjcorless.bsse.asu.edu.placemanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mjcorless.bsse.asu.edu.placemanager.database.PlaceManagerDbHelper;

/**
 * Copyright 2018 Matthew Corless
 * This code is free to use for educational purposes.
 *
 * @author Matthew Corless
 * mailto: mjcorless@asu.edu
 * @version April 14, 2018
 * <p>
 * Allows the user to view distance between two places.
 */
public class DistanceActivity extends AppCompatActivity implements OnItemSelectedListener
{
	private Spinner spinner1;
	private Spinner spinner2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distance);

		spinner1 = findViewById(R.id.spinner);
		spinner2 = findViewById(R.id.spinner2);

		PlaceManagerDbHelper dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

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
		spinner1.setAdapter(adapter);
		spinner2.setAdapter(adapter);

		spinner1.setOnItemSelectedListener(this);
		spinner2.setOnItemSelectedListener(this);

		updateDistance();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		updateDistance();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		// Another interface callback
	}

	private void updateDistance()
	{
		// get id of item 1
		String itemValue = (String) spinner1.getSelectedItem();
		int idIndex = itemValue.indexOf('.');
		int placeId1 = Integer.parseInt(itemValue.substring(0, idIndex));

		// get id of item 2
		itemValue = (String) spinner2.getSelectedItem();
		idIndex = itemValue.indexOf('.');
		int placeId2 = Integer.parseInt(itemValue.substring(0, idIndex));

		PlaceManagerDbHelper dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		//nt elevation1 = 0;
		float longitude1 = 0;
		float latitude1 = 0;
		//int elevation2 = 0;
		float longitude2 = 0;
		float latitude2 = 0;

		Cursor cursor = db.rawQuery("SELECT * FROM PlaceDescription where PlaceId = ?", new String[]{Integer.toString(placeId1)});
		while (cursor.moveToNext())
		{
			//elevation1 = cursor.getInt(cursor.getColumnIndexOrThrow("Elevation"));
			longitude1 = cursor.getFloat(cursor.getColumnIndexOrThrow("Longitude"));
			latitude1 = cursor.getFloat(cursor.getColumnIndexOrThrow("Latitude"));
		}

		Cursor cursor2 = db.rawQuery("SELECT * FROM PlaceDescription where PlaceId = ?", new String[]{Integer.toString(placeId2)});
		while (cursor2.moveToNext())
		{
			//elevation2 = cursor.getInt(cursor.getColumnIndexOrThrow("Elevation"));
			longitude2 = cursor2.getFloat(cursor2.getColumnIndexOrThrow("Longitude"));
			latitude2 = cursor2.getFloat(cursor2.getColumnIndexOrThrow("Latitude"));
		}
		cursor.close();
		cursor2.close();

		TextView circleTxt = findViewById(R.id.circleTxt);
		String x = String.format("Great Circle Distance (nautical miles): %f", getGreatCircle(longitude1, latitude1, longitude2, latitude2));
		circleTxt.setText(x);


		TextView headingTxt = findViewById(R.id.headingTxt);
		String y = String.format("Initial Heading (Bearing Degrees): %f", getHeadingAngle(longitude1, latitude1, longitude2, latitude2));
		headingTxt.setText(y);
	}


	private double getGreatCircle(float longitude1, float latitude1, float longitude2, float latitude2)
	{
		if (longitude1 == longitude2 && latitude1 == latitude2)
		{
			return 0;
		}

		double pk = 180 / Math.PI;

		double a1 = latitude1 / pk;
		double a2 = longitude1 / pk;
		double b1 = latitude2 / pk;
		double b2 = longitude2 / pk;

		double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
		double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
		double t3 = Math.sin(a1) * Math.sin(b1);
		double tt = Math.acos(t1 + t2 + t3);

		return 3959 * tt;
	}

	private double getHeadingAngle(float longitude1, float latitude1, float longitude2, float latitude2)
	{
		double longDiff = Math.toRadians(longitude2 - longitude1);
		double y = Math.sin(longDiff) * Math.cos(latitude2);
		double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

		double r = (Math.toDegrees(Math.atan2(y, x)) + 360);

		return r % 360;
	}
}


