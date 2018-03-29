package placemanager.mjcorless.bsse.asu.edu.placemanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PlaceDescriptionActivity extends AppCompatActivity
{
	// the categories for autocomplete textfield
	private static final String[] CATEGORIES = new String[]{"Residence", "Business", "Travel", "Hike", "Food"};


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}
