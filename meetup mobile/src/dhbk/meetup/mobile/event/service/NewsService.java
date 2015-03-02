package dhbk.meetup.mobile.event.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Utils;

public class NewsService extends Service{

	public static final String ACTION_NONE = "action none";
	
	public String iduser = "0";
	public HttpConnect connNotify;
	public Handler handlerNotify;
	public Runnable getlistNotify = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Utils.isConnectNetwork(getApplicationContext())) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new UpdateNotify(NewsService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new UpdateNotify(NewsService.this).execute() ;
				}
			} else {
				handlerNotify.postDelayed(getlistNotify, 60000);
			}
		}
	};
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("Create service");
		handlerNotify = new Handler();
		connNotify = new HttpConnect();
		handlerNotify.postDelayed(getlistNotify, 10000);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent != null) {
			try {
				iduser = intent.getExtras().getString("iduser");
				System.out.println("service set id = " + iduser);
			} catch (Exception e) {}
		}
		return START_STICKY;
	}
	
}
