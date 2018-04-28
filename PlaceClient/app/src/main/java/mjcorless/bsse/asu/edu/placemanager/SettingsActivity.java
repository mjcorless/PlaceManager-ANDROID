package mjcorless.bsse.asu.edu.placemanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

/*
 * Copyright 2018 Matthew Corless
 * This code is free to use for educational purposes.
 *
 * @author Matthew Corless
 * @version April 2018
 */
public class SettingsActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String url = preferences.getString("url", "http://10.0.2.2:8080/");
		EditText urlSetting = findViewById(R.id.urlSetting);
		urlSetting.setText(url);
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		EditText urlSetting = findViewById(R.id.urlSetting);
		String url = urlSetting.getText().toString();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("url", url);
		editor.commit();
	}
}
