package mjcorless.bsse.asu.edu.placemanager.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import mjcorless.bsse.asu.edu.placemanager.database.PlaceManagerDbHelper;

/*
 * Copyright 2018 Matthew Corless
 * This code is free to use for educational purposes.
 *
 * This class is largely based on an instructor example of an AsyncTask for a student collection
 *
 * Purpose: Android application that uses an AsyncTask to accomplish syncing with JSONRPC server
 *
 * @author Matthew Corless
 * @version April 2018
 */

// AsyncTask. Generic type parameters:
//    first is base type of array input to doInBackground method
//    second is type for input to onProgressUpdate method. also base type of an array
//    third is the return type for the doInBackground method, whose value is the argument
//          to the onPostExecute method.
public class AsyncCollectionConnect extends AsyncTask<MethodInformation, Integer, MethodInformation>
{

	@Override
	protected void onPreExecute()
	{
		android.util.Log.d(this.getClass().getSimpleName(), "in onPreExecute on " + (Looper.myLooper() == Looper.getMainLooper() ? "Main thread" : "Async Thread"));
	}

	@Override
	protected MethodInformation doInBackground(MethodInformation... aRequest)
	{
		// array of methods to be called. Assume exactly one input, a single MethodInformation object
		android.util.Log.d(this.getClass().getSimpleName(), "in doInBackground on " + (Looper.myLooper() == Looper.getMainLooper() ? "Main thread" : "Async Thread"));
		try
		{
			JSONArray ja = new JSONArray(aRequest[0].params);
			android.util.Log.d(this.getClass().getSimpleName(), "params: " + ja.toString());
			String requestData = "{ \"jsonrpc\":\"2.0\", \"method\":\"" + aRequest[0].method + "\", \"params\":" + ja.toString() + ",\"id\":3}";
			android.util.Log.d(this.getClass().getSimpleName(), "requestData: " + requestData + " url: " + aRequest[0].urlString);
			JsonRPCRequestViaHttp conn = new JsonRPCRequestViaHttp((new URL(aRequest[0].urlString)), aRequest[0].parent);
			String resultStr = conn.call(requestData);
			aRequest[0].resultAsJson = resultStr;
		}
		catch (Exception ex)
		{
			android.util.Log.d(this.getClass().getSimpleName(), "exception in remote call " + ex.getMessage());
		}
		return aRequest[0];
	}

	@Override
	protected void onPostExecute(MethodInformation res)
	{
		android.util.Log.d(this.getClass().getSimpleName(), "in onPostExecute on " + (Looper.myLooper() == Looper.getMainLooper() ? "Main thread" : "Async Thread"));
		android.util.Log.d(this.getClass().getSimpleName(), " resulting is: " + res.resultAsJson);
		try
		{
			if (res.method.equals("getNames"))
			{
				JSONObject jo = new JSONObject(res.resultAsJson);
				JSONArray ja = jo.getJSONArray("result");
				ArrayList<String> al = new ArrayList<String>();
				for (int i = 0; i < ja.length(); i++)
				{
					al.add(ja.getString(i));
				}
				String[] names = al.toArray(new String[0]);
				res.parent.adapter.clear();
				for (int i = 0; i < names.length; i++)
				{
					res.parent.adapter.add(names[i]);
				}
				res.parent.adapter.notifyDataSetChanged();
				System.out.println("The names are: " + Arrays.toString(names));
				for (int i = 0; i < names.length; i++)
				{
					try
					{

						// got the list of student names from the server, so now create a new async task
						// to get the student information about the first student and populate the UI with
						// that student's information.
						MethodInformation mi = new MethodInformation(res.parent, res.urlString, "get", new String[]{names[i]});
						AsyncCollectionConnect ac = (AsyncCollectionConnect) new AsyncCollectionConnect().execute(mi);
					}
					catch (Exception ex)
					{
						android.util.Log.w(this.getClass().getSimpleName(), "Exception processing spinner selection: " + ex.getMessage());
					}
				}
			}
			else if (res.method.equals("get"))
			{
				JSONObject jo = new JSONObject(res.resultAsJson);
				PlaceDescription place = new PlaceDescription(jo.getJSONObject("result"));

				PlaceManagerDbHelper dbHelper = new PlaceManagerDbHelper(res.parent);
				SQLiteDatabase db = dbHelper.getWritableDatabase();

				ContentValues values = new ContentValues();
				values.put("Name", place.getName());
				values.put("Category", place.getCategory());
				values.put("Description", place.getDescription());
				values.put("AddressTitle", place.getAddressTitle());
				values.put("Address", place.getAddressPostal());
				values.put("Elevation", place.getElevation());
				values.put("Latitude", place.getLatitude());
				values.put("Longitude", place.getLongitude());

				Cursor cursor = db.rawQuery("SELECT * FROM PlaceDescription where Name = ?", new String[]{place.getName()});
				int placeId = 0;
				while (cursor.moveToNext())
				{
					placeId = cursor.getInt(cursor.getColumnIndexOrThrow("PlaceId"));
				}
				cursor.close();


				if (placeId > 0)
				{
					System.out.println("Updating " + place.getName());
					db.update("PlaceDescription", values, "PlaceId = ?", new String[]{Integer.toString(placeId)});
				}
				else
				{
					System.out.println("Inserting " + place.getName());
					long newRowId = db.insert("PlaceDescription", null, values);
					if (newRowId <= Integer.MAX_VALUE)
					{
						placeId = (int) newRowId;
					}
				}
			}
			else if (res.method.equals("add"))
			{
				// I guess do nothing ?
			}
		}
		catch (Exception ex)
		{
			android.util.Log.d(this.getClass().getSimpleName(), "Exception: " + ex.getMessage());
		}
	}

}
