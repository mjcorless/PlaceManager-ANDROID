package mjcorless.bsse.asu.edu.placemanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mjcorless.bsse.asu.edu.placemanager.database.PlaceManagerDbHelper;
import mjcorless.bsse.asu.edu.placemanager.models.PlaceDescription;

/**
 * Copyright 2018 Matthew Corless
 * This code is free to use for educational purposes.
 *
 * @author Matthew Corless
 * mailto: mjcorless@asu.edu
 * @version April 14, 2018
 * <p>
 * A basic view that allows the user to enter in place information
 */
public class CreatePlaceActivity extends AppCompatActivity
{

	// the categories for autocomplete textfield
	private static final String[] CATEGORIES = new String[]{"Residence", "Business", "Travel", "Hike", "Food", "School", "Government", "Tourist Attraction"};
	public EditText nameTV;
	public EditText descriptionTV;
	public AutoCompleteTextView categoryTV;
	public EditText addressTitleTV;
	public EditText addressFullTV;
	public EditText elevationTV;
	public EditText longitudeTV;
	public EditText latitudeTV;
	public PlaceDescription placeDescription;
	public int placeId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_information);

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CATEGORIES);
		categoryTV = findViewById(R.id.category);
		categoryTV.setAdapter(adapter);

		nameTV = findViewById(R.id.placeName);
		descriptionTV = findViewById(R.id.description);
		//categoryTV = findViewById(R.id.category); this is already set
		addressTitleTV = findViewById(R.id.addressTitle);
		addressFullTV = findViewById(R.id.addressFull);
		elevationTV = findViewById(R.id.elevation);
		longitudeTV = findViewById(R.id.longitude);
		latitudeTV = findViewById(R.id.latitude);

		placeId = getIntent().getIntExtra("PlaceId", 0);
		double lat = getIntent().getDoubleExtra("lat", -1);
		double lon = getIntent().getDoubleExtra("lon", -1);
		if (placeId > 0)
		{
			bindView(placeId);
		}
		else if (lat >= 0 || lon >= 0)
		{
			longitudeTV.setText(Double.toString(lon));
			latitudeTV.setText(Double.toString(lat));
			Button btn = findViewById(R.id.deletePlaceBtn);
			btn.setText("Cancel");
		}
		else
		{
			Button btn = findViewById(R.id.deletePlaceBtn);
			btn.setText("Cancel");
		}

		createSaveBtnOnClick();
		createDeleteBtnOnClick();
	}

	private void createSaveBtnOnClick()
	{
		final Button button = findViewById(R.id.savePlaceBtn);
		button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{

				if (validInput())
				{
					placeDescription = new PlaceDescription();
					placeDescription.setName(nameTV.getText().toString());
					placeDescription.setCategory(categoryTV.getText().toString());
					placeDescription.setDescription(descriptionTV.getText().toString());
					placeDescription.setAddressTitle(addressTitleTV.getText().toString());
					placeDescription.setAddressPostal(addressFullTV.getText().toString());

					String elevString = elevationTV.getText().toString();
					int elev = elevString == null || elevString.isEmpty() ? 0 : Integer.parseInt(elevString);
					placeDescription.setElevation(elev);

					String longString = longitudeTV.getText().toString();
					float longi = longString == null || longString.isEmpty() ? 0 : Float.parseFloat(longString);
					placeDescription.setLongitude(longi);

					String latString = latitudeTV.getText().toString();
					float lat = latString == null || latString.isEmpty() ? 0 : Float.parseFloat(latString);
					placeDescription.setLatitude(lat);

					Bundle bundle = new Bundle();
					addOrUpdatePlace();
					goToListView(bundle);
				}
				else
				{
					notifyUser();
				}
			}
		});
	}

	private void createDeleteBtnOnClick()
	{
		final Button button = findViewById(R.id.deletePlaceBtn);
		button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				deletePlace();
				Bundle bundle = new Bundle();
				System.out.println("Key: delete | value: " + nameTV.getText().toString());
				bundle.putString("delete", nameTV.getText().toString());
				goToListView(bundle);
			}
		});
	}

	private void bindView(int placeId)
	{
		PlaceManagerDbHelper dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("SELECT * FROM PlaceDescription where PlaceId = ?", new String[]{Integer.toString(placeId)});
		while (cursor.moveToNext())
		{
			nameTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("Name")));
			categoryTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("Category")));
			descriptionTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("Description")));
			addressTitleTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("AddressTitle")));
			addressFullTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("Address")));
			elevationTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("Elevation")));
			longitudeTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("Longitude")));
			latitudeTV.setText(cursor.getString(cursor.getColumnIndexOrThrow("Latitude")));
		}
		cursor.close();
	}

	private void addOrUpdatePlace()
	{
		PlaceManagerDbHelper dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("Name", placeDescription.getName());
		values.put("Category", placeDescription.getCategory());
		values.put("Description", placeDescription.getDescription());
		values.put("AddressTitle", placeDescription.getAddressTitle());
		values.put("Address", placeDescription.getAddressPostal());
		values.put("Elevation", placeDescription.getElevation());
		values.put("Latitude", placeDescription.getLatitude());
		values.put("Longitude", placeDescription.getLongitude());

		if (placeId > 0)
		{
			db.update("PlaceDescription", values, "PlaceId = ?", new String[]{Integer.toString(placeId)});
		}
		else
		{
			long newRowId = db.insert("PlaceDescription", null, values);
			if (newRowId <= Integer.MAX_VALUE)
			{
				placeId = (int) newRowId;
			}
		}
	}

	private boolean validInput()
	{
		boolean isValid = true;

		if (nameTV.getText().toString() == null || categoryTV.getText().toString() == null || descriptionTV.getText().toString() == null || addressTitleTV.getText().toString() == null || addressFullTV.getText().toString() == null || elevationTV.getText().toString() == null || longitudeTV.getText().toString() == null || latitudeTV.getText().toString() == null)
		{
			isValid = false;
		}
		return isValid;
	}

	private void deletePlace()
	{
		PlaceManagerDbHelper dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		if (placeId > 0)
		{
			db.delete("PlaceDescription", "PlaceId = ?", new String[]{Integer.toString(placeId)});
		}
		else
		{
			String placeName = nameTV.getText().toString();
			db.delete("PlaceDescription", "Name = ?", new String[]{(placeName)});
		}

	}

	private void notifyUser()
	{
		Toast.makeText(this, "Could not Add or Update due to an empty field.", Toast.LENGTH_LONG).show();
	}

	/**
	 * Creates an intent with the given bundle. Starts the PlaceListActivity.
	 *
	 * @param bundle Bundle to encapsulate in the new activity's intent
	 */
	private void goToListView(Bundle bundle)
	{
		Intent intent = new Intent(this, PlaceListActivity.class);
		if (bundle != null)
		{
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}
}