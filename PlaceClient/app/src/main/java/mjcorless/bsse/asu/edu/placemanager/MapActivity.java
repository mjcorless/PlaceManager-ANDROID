package mjcorless.bsse.asu.edu.placemanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import mjcorless.bsse.asu.edu.placemanager.database.PlaceManagerDbHelper;

/*
 * Copyright 2018 Matthew Corless
 * This code is free to use for educational purposes.
 *
 * @author Matthew Corless
 * @version April 2018
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener
{
	private double latitude;
	private double longitude;
	private GoogleMap mMap;
	private Spinner spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		spinner = findViewById(R.id.spinner);

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);


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
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		setPlace();
	}


	/**
	 * Manipulates the map once available.
	 * This callback is triggered when the map is ready to be used.
	 * This is where we can add markers or lines, add listeners or move the camera. In this case,
	 * we just add a marker near Sydney, Australia.
	 * If Google Play services is not installed on the device, the user will be prompted to install
	 * it inside the SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		mMap = googleMap;

		// Add a marker in Sydney and move the camera
		LatLng place = new LatLng(latitude, longitude);
		mMap.addMarker(new MarkerOptions().position(place).title("Your marker"));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(place));

		mMap.setOnMarkerClickListener(this);
		mMap.setOnMapClickListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
	{
		// get id of item 1
		String itemValue = (String) spinner.getSelectedItem();
		int idIndex = itemValue.indexOf('.');
		int placeId = Integer.parseInt(itemValue.substring(0, idIndex));


		updatePlace(placeId);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		// Another interface callback
	}

	private void updatePlace(int placeId)
	{
		PlaceManagerDbHelper dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		longitude = 0;
		latitude = 0;

		Cursor cursor = db.rawQuery("SELECT * FROM PlaceDescription where PlaceId = ?", new String[]{Integer.toString(placeId)});
		while (cursor.moveToNext())
		{
			//elevation1 = cursor.getInt(cursor.getColumnIndexOrThrow("Elevation"));
			longitude = cursor.getFloat(cursor.getColumnIndexOrThrow("Longitude"));
			latitude = cursor.getFloat(cursor.getColumnIndexOrThrow("Latitude"));
		}

		cursor.close();

		if (mMap != null)
		{
			mMap.clear();
			LatLng place = new LatLng(latitude, longitude);
			mMap.addMarker(new MarkerOptions().position(place).title("Your marker"));
			mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
		}
	}

	private void setPlace()
	{
		String itemValue = (String) spinner.getSelectedItem();
		int idIndex = itemValue.indexOf('.');
		int placeId = Integer.parseInt(itemValue.substring(0, idIndex));

		PlaceManagerDbHelper dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		longitude = 0;
		latitude = 0;

		Cursor cursor = db.rawQuery("SELECT * FROM PlaceDescription where PlaceId = ?", new String[]{Integer.toString(placeId)});
		while (cursor.moveToNext())
		{
			longitude = cursor.getFloat(cursor.getColumnIndexOrThrow("Longitude"));
			latitude = cursor.getFloat(cursor.getColumnIndexOrThrow("Latitude"));
		}

		cursor.close();

		if (mMap != null)
		{
			mMap.clear();
			LatLng place = new LatLng(latitude, longitude);
			mMap.addMarker(new MarkerOptions().position(place).title("Your marker"));
			mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
		}
	}

	@Override
	public boolean onMarkerClick(final Marker marker)
	{
		mMap.clear();
		return true;
	}

	@Override
	public void onMapClick(LatLng place)
	{
		mMap.clear();
		mMap.addMarker(new MarkerOptions().position(place).title("Your marker"));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(place));

		Intent intent = new Intent(this, CreatePlaceActivity.class);

		intent.putExtra("lat", place.latitude);
		intent.putExtra("lon", place.longitude);


		startActivity(intent);
	}
}
