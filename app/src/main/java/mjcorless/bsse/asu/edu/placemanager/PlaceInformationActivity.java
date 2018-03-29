package mjcorless.bsse.asu.edu.placemanager;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

/**
 * A basic view that allows the user to enter in place information
 */
public class PlaceInformationActivity extends AppCompatActivity
{

	// the categories for autocomplete textfield
	private static final String[] CATEGORIES = new String[]{"Residence", "Business", "Travel", "Hike", "Food"};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_information);


		ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, CATEGORIES);
		AutoCompleteTextView categoryTextView = findViewById(R.id.category);
		categoryTextView.setAdapter(adapter);
	}
}

