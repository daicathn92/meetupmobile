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

	public static final String EVENT_GETNOTIFY = "listnotify";
	public static final String EVENT_GETINVITE = "listinvite";
	public static final String ACTION_NONE = "action none";
	public static final long TIME_REPOST_NOTCONNECT = 60000;
	public static final long TIME_REPOST_CONNECT = 15000;
	
	public String iduser = "0";
	public HttpConnect connNotify, connInvite;
	public Handler handlerNotify, handlerInvite;
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
				handlerNotify.postDelayed(getlistNotify, TIME_REPOST_NOTCONNECT);
			}
		}
	};
	
	public Runnable getlistInvite = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Utils.isConnectNetwork(getApplicationContext())) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new UpdateInvite(NewsService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new UpdateInvite(NewsService.this).execute() ;
				}
			} else {
				handlerInvite.postDelayed(getlistInvite, TIME_REPOST_NOTCONNECT);
			}
		}
	};
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("Create service");
		handlerNotify = new Handler();
		handlerInvite = new Handler();
		connNotify = new HttpConnect();
		connInvite = new HttpConnect();
		
		handlerNotify.postDelayed(getlistNotify, TIME_REPOST_CONNECT);
		handlerInvite.postDelayed(getlistInvite, (long)(TIME_REPOST_CONNECT * 1.5));
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
