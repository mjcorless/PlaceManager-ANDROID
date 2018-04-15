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

import mjcorless.bsse.asu.edu.placemanager.database.PlaceManagerDbHelper;
import mjcorless.bsse.asu.edu.placemanager.models.Address;
import mjcorless.bsse.asu.edu.placemanager.models.PlaceDescription;

/**
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
	private PlaceManagerDbHelper dbHelper;
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
		if (placeId > 0)
		{
			bindView(placeId);
		}

		createSaveBtnOnClick(placeId);
	}

	private void createSaveBtnOnClick(int placeId)
	{
		final Button button = findViewById(R.id.savePlaceBtn);
		button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				placeDescription = new PlaceDescription();
				placeDescription.setName(nameTV.getText().toString());
				placeDescription.setCategory(categoryTV.getText().toString());
				placeDescription.setDescription(descriptionTV.getText().toString());

				Address address = new Address();
				address.setTitle(addressTitleTV.getText().toString());
				address.setPostalAddress(addressFullTV.getText().toString());

				placeDescription.setAddress(address);
				placeDescription.setElevation(Integer.parseInt(elevationTV.getText().toString()));
				placeDescription.setLongitude(Float.parseFloat(longitudeTV.getText().toString()));
				placeDescription.setLatitude(Float.parseFloat(latitudeTV.getText().toString()));

				Bundle bundle = new Bundle();

				addOrUpdatePlace();
				goToListView(bundle);
			}
		});
	}

	private void bindView(int placeId)
	{
		dbHelper = new PlaceManagerDbHelper(this);
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
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("Name", placeDescription.getName());
		values.put("Category", placeDescription.getCategory());
		values.put("Description", placeDescription.getDescription());
		values.put("AddressId", 1);
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

	/**
	 * Creates an intent with the given bundle. Starts the PlaceListActivity.
	 *
	 * @param bundle Bundle to encapsulate in the new activity's intent
	 */
	private void goToListView(Bundle bundle)
	{
		Intent intent = new Intent(this, PlaceListActivity.class);
		startActivity(intent);
	}
}