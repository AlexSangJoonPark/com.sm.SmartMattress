package com.example.com.sm.smartmattress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

import com.example.com.sm.smartmattress.MainActivity.HttpMethodGetTask;
import com.example.com.sm.smartmattress.MainActivity.HttpMethodPostTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity.Header;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SensitivitySlider extends ActionBarActivity implements OnClickListener, OnSeekBarChangeListener {
	
	public Button backButton;
	public SeekBar bar;
	public TextView textProgress;
	public String Sensitivity = "sensitivity";
	public int Progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slider);
		HttpMethodGetTask HMGT = new HttpMethodGetTask(SensitivitySlider.this);	
		backButton = (Button)findViewById(R.id.backButtonSlider);
		backButton.setOnClickListener(this);
		bar = (SeekBar)findViewById(R.id.slider1);
	    bar.setOnSeekBarChangeListener(this);
	    textProgress = (TextView)findViewById(R.id.seekBarText);
	    
		if (HMGT.getStatus() != AsyncTask.Status.RUNNING)
		{
			try
			{
				HMGT.execute(Constants.SERVERURL + Sensitivity);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	    
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId())
		{	
			case R.id.backButtonSlider:
				Intent i = new Intent(this, MainActivity.class);
				startActivity(i);
				finish();
		}
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
				mProgressDialog = new ProgressDialog(SensitivitySlider.this);
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
				int resultInt = Integer.parseInt(result);
				Log.v("Sensitivity", "Result was " + result);
				
				bar.setProgress(resultInt);
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		
		Progress = progress;
		
		
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	
	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	
		if (Progress == 0)
		{
			Progress = 1;
		}
		
		Log.v("Sensitivity", String.valueOf(Progress));

		HttpMethodPostTask HMPT = new HttpMethodPostTask(SensitivitySlider.this);
		
		if (HMPT.getStatus() != AsyncTask.Status.RUNNING)
		{
			try
			{
				HMPT.execute(Constants.SERVERURL + Sensitivity);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
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
			mProgressDialog = new ProgressDialog(SensitivitySlider.this);
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
			pairs.add(new BasicNameValuePair("range", String.valueOf(Progress)));
			
			
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
