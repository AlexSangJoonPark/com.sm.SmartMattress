package com.example.com.sm.smartmattress;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

//import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity.Header;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {

public Button quitButton, calibrateButton, sensorAdjust, helpButton, activateButton;
private int TIMEOUT_MILLSEC = 10000;
public String Activate = "activate";
public String Calibrate = "calibrate";
public String PROJECT_NUMBER = "watchful-gear-88814";
public String regid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		HttpMethodGetTask HMGT = new HttpMethodGetTask(MainActivity.this);	
		activateButton = (Button)findViewById(R.id.activateButton);
		calibrateButton = (Button)findViewById(R.id.calibrateButton);
		helpButton = (Button)findViewById(R.id.helpButton);
		quitButton = (Button)findViewById(R.id.quitButton);
		sensorAdjust = (Button)findViewById(R.id.sensorAdjustButton);
		
		helpButton.setOnClickListener(this);
		quitButton.setOnClickListener(this);
		calibrateButton.setOnClickListener(this);
		activateButton.setOnClickListener(this);
		sensorAdjust.setOnClickListener(this);
		
		
		if (HMGT.getStatus() != AsyncTask.Status.RUNNING)
		{
			try
			{
				HMGT.execute(Constants.SERVERURL + Activate);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onClick(View v) {		
		switch (v.getId())
		{	
			case R.id.activateButton:
			{	
				HttpMethodPostTask HMPT = new HttpMethodPostTask(MainActivity.this);
			//	Toast.makeText(MainActivity.this, "No connection to the internet detected :(", Toast.LENGTH_SHORT).show();
				if (HMPT.getStatus() != AsyncTask.Status.RUNNING)
				{
					try
					{
						HMPT.execute(Constants.SERVERURL + Activate);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
					Log.v("This", activateButton.getText().toString());
					
					if (activateButton.getText().toString().equals("Activate"))
					{
						startService(new Intent(this, BackgroundService.class));
						Log.v("This", "Service activated");
					}
					
					else if (activateButton.getText().toString().equals("Deactivate"))
					{
						stopService(new Intent(this, BackgroundService.class));
						Log.v("This", "Service deactivated");
					} 
				}
				break;
			}
		
			case R.id.sensorAdjustButton:
			{
				Intent i = new Intent(this, SensitivitySlider.class);
				startActivity(i);
				finish();
				break;
			}
		
			case R.id.calibrateButton:
			{	
				
				CalibrateHttpMethodPostTask CHMPT = new CalibrateHttpMethodPostTask(MainActivity.this);
				if (CHMPT.getStatus() != AsyncTask.Status.RUNNING)
				{
					try
					{
						CHMPT.execute(Constants.SERVERURL + Calibrate);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				break;
			}
			
			case R.id.helpButton:
			{
				Intent i = new Intent(this, HelpBlurb.class);
				startActivity(i);
				finish();
				break;
			}
			
			case R.id.quitButton:
			{
				finish();
				break;
			}
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		HttpMethodGetTask HMGT = new HttpMethodGetTask(MainActivity.this);
		if (HMGT.getStatus() != AsyncTask.Status.RUNNING)
		{
			try
			{
				HMGT.execute(Constants.SERVERURL + Activate);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	 class HttpMethodGetTask extends AsyncTask<String, Void, String> {

		String result;
		private int totalContactCount = 0, unmatchedContactCount = 0;
		private ProgressDialog mProgressDialog;
		public HttpClient httpclient = new DefaultHttpClient();
		public HttpGet httpget;
		private  Context context;
		public HttpMethodGetTask(Context context) {
			// TODO Auto-generated constructor stub
			context = context;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setTitle("Activating Smart Mattress");
			mProgressDialog.setMessage("Just a second..");
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.show();
		}
		
		@Override
		protected String doInBackground(String...url) 
		{
			HttpResponse response;
			
		    try {
		    	httpget = new HttpGet(url[0]);
		        response = httpclient.execute(httpget);
		        // Examine the response status
		        Log.i("test",response.getStatusLine().toString());

		        // Get hold of the response entity
		        HttpEntity entity = response.getEntity();
		        
		        if (entity != null) {

		            // A Simple JSON Response Read
		            InputStream instream = entity.getContent();
		            result = convertStreamToString(instream);
		            // now you have the string representation of the HTML request
		            instream.close();
		        }

		    } 
		    
		    catch (Exception e) {
		    	e.printStackTrace();
		    }
		    
			return result;
		}
				
		
		@Override
		public void onPostExecute(String result)
		{
			
			Log.v("This", "Button returned " + result);
	
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
			}
			
			result = result.trim();
			if (result.equals("true"))
				{
					activateButton.setText("Deactivate");
					Log.v("This", activateButton.getText().toString());
				}
			else if (result.equals("false"))
				{
					activateButton.setText("Activate");
					Log.v("This", activateButton.getText().toString());
				}
}
		
		private String convertStreamToString(InputStream is) {
		    /*
		     * To convert the InputStream to String we use the BufferedReader.readLine()
		     * method. We iterate until the BufferedReader return null which means
		     * there's no more data to read. Each line will appended to a StringBuilder
		     * and returned as String.
		     */
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    try {
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        try {
		            is.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    return sb.toString();
		}
	 }


	 class HttpMethodPostTask extends AsyncTask<String, Void, String> {

		
		private int totalContactCount = 0, unmatchedContactCount = 0;
		private ProgressDialog mProgressDialog;
		public HttpClient httpclient = new DefaultHttpClient();
		private  Context context;
		public HttpMethodPostTask(Context context) {
			// TODO Auto-generated constructor stub
			context = context;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setTitle("Activating Smart Mattress");
			mProgressDialog.setMessage("Just a second..");
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.show();
		}
		
		@Override
		protected String doInBackground(String...url) 
		{
			HttpPost httpPost = new HttpPost(url[0]);
			String fleh = "no response?";
			//create a http post request
			Header[] responseHeaders = null;
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, Constants.TIMEOUT_MILLSEC);
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("", ""));
			
			
			try
			{
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs);
				httpPost.setEntity(entity);
			   HttpResponse response = httpclient.execute(httpPost);
			   fleh = EntityUtils.toString(response.getEntity());
			}
			catch (ClientProtocolException e)
				{
				Log.v("This", "client protocol exception!");				
				} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v("This", fleh);
			return fleh;
		}
				
		@Override
		public void onPostExecute(String result)
		{
			Log.v("This", "result is " + result);
	
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
			}
			
			if (result.equals("true"))
			{

				activateButton.setText("Deactivate");
			}
		else if (result.equals("false"))
			{
				activateButton.setText("Activate");
				
			}
}
		
		private String convertStreamToString(InputStream is) {
		    /*
		     * To convert the InputStream to String we use the BufferedReader.readLine()
		     * method. We iterate until the BufferedReader return null which means
		     * there's no more data to read. Each line will appended to a StringBuilder
		     * and returned as String.
		     */
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    try {
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        try {
		            is.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    return sb.toString();
		}
	 }
	 
	 
	 class CalibrateHttpMethodPostTask extends AsyncTask<String, Void, String> {

			
			private int totalContactCount = 0, unmatchedContactCount = 0;
			private ProgressDialog mProgressDialog;
			public HttpClient httpclient = new DefaultHttpClient();
			private  Context context;
			public CalibrateHttpMethodPostTask(Context context) {
				// TODO Auto-generated constructor stub
				context = context;
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				mProgressDialog = new ProgressDialog(MainActivity.this);
				mProgressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
				mProgressDialog.setTitle("Calibrating Smart Mattress");
				mProgressDialog.setMessage("Just a second..");
				mProgressDialog.setCancelable(false);
				mProgressDialog.setIndeterminate(true);
				mProgressDialog.show();
			}
			
			@Override
			protected String doInBackground(String...url) 
			{
				HttpPost httpPost = new HttpPost(url[0]);
				String fleh = "no response?";
				//create a http post request
				Header[] responseHeaders = null;
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, Constants.TIMEOUT_MILLSEC);
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("", ""));
				
				
				try
				{
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs);
					httpPost.setEntity(entity);
				   HttpResponse response = httpclient.execute(httpPost);
				   fleh = EntityUtils.toString(response.getEntity());
				}
				catch (ClientProtocolException e)
					{
					Log.v("This", "client protocol exception!");				
					} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.v("This", fleh);
				return fleh;
			}
					
			@Override
			public void onPostExecute(String result)
			{
				Log.v("This", "result is " + result);
		
				if (mProgressDialog != null)
				{
					mProgressDialog.dismiss();
				}
	}
			
			private String convertStreamToString(InputStream is) {
			    /*
			     * To convert the InputStream to String we use the BufferedReader.readLine()
			     * method. We iterate until the BufferedReader return null which means
			     * there's no more data to read. Each line will appended to a StringBuilder
			     * and returned as String.
			     */
			    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			    StringBuilder sb = new StringBuilder();

			    String line = null;
			    try {
			        while ((line = reader.readLine()) != null) {
			            sb.append(line + "\n");
			        }
			    } catch (IOException e) {
			        e.printStackTrace();
			    } finally {
			        try {
			            is.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
			    }
			    return sb.toString();
			}
		 }
}