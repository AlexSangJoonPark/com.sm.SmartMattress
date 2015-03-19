package com.example.com.sm.smartmattress;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class BackgroundService extends Service {

	 private MyThread mythread;
	 public boolean isRunning = false;
	 public String Notification = "notification";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		Log.v("Service", "onCreate");
		 mythread  = new MyThread();
	}
 
	@Override
	public void onStart(Intent intent, int startId) 
	{
	//	Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		 if(!isRunning){
	            mythread.start();
	            isRunning = true;
		 }
		Log.v("Service", "onStart");	
	}
	 
	@Override
	public void onDestroy() {
	//Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
	Log.v("Service", "onDestroy");
	try {
			if(isRunning){
				mythread.interrupt();
				mythread.stop();
				isRunning = false;
			}
	}
	catch 
		(Exception e)
	{	
		e.printStackTrace();
	}
}
	
	 public void readWebPage(){
         HttpClient client = new DefaultHttpClient();
         HttpGet request = new HttpGet(Constants.SERVERURL + Notification);
         // Get the response
         ResponseHandler<String> responseHandler = new BasicResponseHandler();
         String response_str = null;
         try {
            response_str = client.execute(request, responseHandler);
            if(!response_str.equals("")){
                Log.v("Service", response_str);
                NotificationCompat.Builder mBuilder =
                	    new NotificationCompat.Builder(this)
                	    .setContentTitle("Smart Mattress Alert!")
                	    .setSmallIcon(R.drawable.warning)
                	    .setContentText(response_str);
                
                NotificationManager mNotificationManager =
                	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                	    
                	// notificationID allows you to update the notification later on.
                	mNotificationManager.notify(1, mBuilder.build());
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

   }
    
   class MyThread extends Thread{
       static final long DELAY = 1000;
       @Override
       public void run(){         
           while(isRunning){
               Log.v("Service","Running");
               try {                  
                   readWebPage();
                   Thread.sleep(DELAY);
               } catch (InterruptedException e) {
                   isRunning = false;
                   e.printStackTrace();
               }
           }
       }
   }

}
