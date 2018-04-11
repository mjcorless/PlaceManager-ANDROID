package mjcorless.bsse.asu.edu.placemanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mjcorless.bsse.asu.edu.placemanager.database.PlaceManagerDbHelper;

public class PlaceListActivity extends AppCompatActivity
{
	private PlaceManagerDbHelper dbHelper;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_list);

		Bundle bundle = getIntent().getExtras();
		//PlaceDescription placeDescription = (PlaceDescription) bundle.getSerializable("placeDescription");

		textView = findViewById(R.id.text_view_id);

		dbHelper = new PlaceManagerDbHelper(this);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT Name FROM PlaceDescription WHERE PlaceId > ?", new String[]{"0"});
		List names = new ArrayList();
		while (cursor.moveToNext())
		{
			names.add(cursor.getColumnIndexOrThrow("Name"));
		}
		cursor.close();

		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_place_list, names);
		textView.setText(names.toString());
	}
}
