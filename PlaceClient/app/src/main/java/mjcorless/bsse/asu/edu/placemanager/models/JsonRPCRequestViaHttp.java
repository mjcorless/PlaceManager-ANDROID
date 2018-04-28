package mjcorless.bsse.asu.edu.placemanager.models;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import mjcorless.bsse.asu.edu.placemanager.PlaceListActivity;

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
 *
 *
 * @author Matthew Corless
 * @version April 2018
 */

public class JsonRPCRequestViaHttp
{

	private final Map<String, String> headers;
	private URL url;
	private String requestData;
	private PlaceListActivity parent;

	public JsonRPCRequestViaHttp(URL url, PlaceListActivity parent)
	{
		URL savedUrl = null;
		try
		{
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
			String urlString = preferences.getString("url", "http://10.0.2.2:8080/");
			savedUrl = new URL(urlString);
		}
		catch (Exception e)
		{
			Toast.makeText(parent, "Unable to resolve URL. Go to settings to fix URL.", Toast.LENGTH_LONG).show();
		}

		this.url = (savedUrl == null) ? url : savedUrl;


		this.parent = parent;
		this.headers = new HashMap<String, String>();
	}

	public void setHeader(String key, String value)
	{
		this.headers.put(key, value);
	}

	public String call(String requestData) throws Exception
	{
		android.util.Log.d(this.getClass().getSimpleName(), "in call, url: " + url.toString() + " requestData: " + requestData);
		String respData = post(url, headers, requestData);
		return respData;
	}

	private String post(URL url, Map<String, String> headers, String data) throws Exception
	{
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		this.requestData = data;
		if (headers != null)
		{
			for (Map.Entry<String, String> entry : headers.entrySet())
			{
				connection.addRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		connection.addRequestProperty("Accept-Encoding", "gzip");
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.connect();
		OutputStream out = null;
		try
		{
			out = connection.getOutputStream();
			out.write(data.getBytes());
			out.flush();
			out.close();
			int statusCode = connection.getResponseCode();
			if (statusCode != HttpURLConnection.HTTP_OK)
			{
				throw new Exception("Unexpected status from post: " + statusCode);
			}
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}
		String responseEncoding = connection.getHeaderField("Content-Encoding");
		responseEncoding = (responseEncoding == null ? "" : responseEncoding.trim());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream in = connection.getInputStream();
		try
		{
			in = connection.getInputStream();
			if ("gzip".equalsIgnoreCase(responseEncoding))
			{
				in = new GZIPInputStream(in);
			}
			in = new BufferedInputStream(in);
			byte[] buff = new byte[1024];
			int n;
			while ((n = in.read(buff)) > 0)
			{
				bos.write(buff, 0, n);
			}
			bos.flush();
			bos.close();
		}
		finally
		{
			if (in != null)
			{
				in.close();
			}
		}
		android.util.Log.d(this.getClass().getSimpleName(), "json rpc request via http returned string " + bos.toString());
		return bos.toString();
	}
}

