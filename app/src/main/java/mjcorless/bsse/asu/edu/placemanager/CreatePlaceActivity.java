package mjcorless.bsse.asu.edu.placemanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import mjcorless.bsse.asu.edu.placemanager.database.PlaceManagerDbHelper;
import mjcorless.bsse.asu.edu.placemanager.models.Address;
import mjcorless.bsse.asu.edu.placemanager.models.PlaceDescription;

/**
 * A basic view that allows the user to enter in place information
 */
public class CreatePlaceActivity extends AppCompatActivity
{

	// the categories for autocomplete textfield
	private static final String[] CATEGORIES = new String[]{"Residence", "Business", "Travel", "Hike", "Food"};
	public TextView nameTV;
	public TextView descriptionTV;
	public AutoCompleteTextView categoryTV;
	public TextView addressTitleTV;
	public TextView addressFullTV;
	public TextView elevationTV;
	public TextView longitudeTV;
	public TextView latitudeTV;
	public PlaceDescription placeDescription;
	private PlaceManagerDbHelper dbHelper;

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


		dbHelper = new PlaceManagerDbHelper(this);

		createPlaceBtnOnClick();
	}

	private void createPlaceBtnOnClick()
	{
		final Button button = findViewById(R.id.addPlaceBtn);
		button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View view)
			{
				Log.d("On Click", "Begin");
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
				bundle.putSerializable("placeDescription", placeDescription);

				Log.d("On Click", "SQLITE crap");
				SQLiteDatabase db = dbHelper.getWritableDatabase();
/*
				// I need to insert a dummy address for the FK constraint
				ContentValues testvalues = new ContentValues();
				testvalues.put("Title", "Home");
				testvalues.put("Address", "Antelope");
				testvalues.put("City", "Layton");
				testvalues.put("State", "UT");
				testvalues.put("ZipCode", 12345);
				long testnewRowId = db.insert("Address", null, testvalues);
*/

				ContentValues values = new ContentValues();
				values.put("Name", placeDescription.getName());
				//values.put("Category", placeDescription.getCategory());
				values.put("Description", placeDescription.getDescription());
				values.put("AddressId", 1);
				values.put("Elevation", placeDescription.getElevation());
				values.put("Latitude", placeDescription.getLatitude());
				values.put("Longitude", placeDescription.getLongitude());

				long newRowId = db.insert("PlaceDescription", null, values);

				Log.d("On Click", "End Sqlite crap");
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
		Intent intent = new Intent(this, PlaceListActivity.class);
		//intent.putExtras(bundle);
		startActivity(intent);
	}
}