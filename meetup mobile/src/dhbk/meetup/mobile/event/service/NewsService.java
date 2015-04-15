package dhbk.meetup.mobile.event.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Utils;

public class NewsService extends Service implements 
				LocationListener, ConnectionCallbacks, OnConnectionFailedListener{

	public static final String EVENT_GETNOTIFY = "listnotify";
	public static final String EVENT_GETINVITE = "listinvite";
	public static final String UPDATE_MYLOCATION = "location";
	public static final String ACTION_NONE = "action none";
	public static final long TIME_REPOST_NOTCONNECT = 90000;
	public static final long TIME_REPOST_CONNECT = 30000;
	
	public String iduser = "0";
	public HttpConnect connNotify, connInvite, connLocation;
	public Handler handlerNotify, handlerInvite, handlerLocation;
	public LocationManager locationManager;
	private GoogleApiClient googleApiClient;
	private LocationRequest locationRequest;
	
	public double lat = 0, lng = 0;
	
	public Runnable getlistNotify = new Runnable() {
		
		@SuppressLint("NewApi")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Utils.isConnectNetwork(getApplicationContext())) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					System.out.println("NOTIFY EXE MULTI");
					new UpdateNotify(NewsService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					System.out.println("NOTIFY EXE SINGLE");
					new UpdateNotify(NewsService.this).execute() ;
				}
			} else {
				handlerNotify.postDelayed(getlistNotify, TIME_REPOST_NOTCONNECT);
			}
		}
	};
	
	public Runnable getlistInvite = new Runnable() {
		
		@SuppressLint("NewApi")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Utils.isConnectNetwork(getApplicationContext())) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					System.out.println("INVITE EXE MULTI");
					new UpdateInvite(NewsService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					System.out.println("INVITE EXE SINGLE");
					new UpdateInvite(NewsService.this).execute() ;
				}
			} else {
				handlerInvite.postDelayed(getlistInvite, TIME_REPOST_NOTCONNECT);
			}
		}
	};
	
	public Runnable updateMyLocation = new Runnable() {
		
		@SuppressLint("NewApi")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Utils.isConnectNetwork(getApplicationContext()) && Utils.isGPSEnable(locationManager)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					System.out.println("LOCATION EXE MULTI");
					new UpdateLocation(NewsService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					System.out.println("LOCATION EXE SINGLE");
					new UpdateLocation(NewsService.this).execute() ;
				}
			} else {
				handlerLocation.postDelayed(updateMyLocation, TIME_REPOST_NOTCONNECT);
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
		handlerLocation = new Handler();
		connNotify = new HttpConnect();
		connInvite = new HttpConnect();
		connLocation = new HttpConnect();
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		locationRequest = new LocationRequest();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
        googleApiClient = new GoogleApiClient.Builder(this)
								.addApi(LocationServices.API)
								.addConnectionCallbacks(this)
								.addOnConnectionFailedListener(this)
								.build();
		
		
//		handlerInvite.postDelayed(getlistInvite, (long)(TIME_REPOST_CONNECT));
//		handlerNotify.postDelayed(getlistNotify, (long)(TIME_REPOST_CONNECT * 1.33));
//		handlerLocation.postDelayed(updateMyLocation, (long)(TIME_REPOST_CONNECT * 1.66));
//        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS)
//        	googleApiClient.connect();
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
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handlerInvite.removeCallbacks(getlistInvite);
		handlerNotify.removeCallbacks(getlistNotify);
		handlerLocation.removeCallbacks(updateMyLocation);
		
		if(googleApiClient.isConnected() || googleApiClient.isConnecting())
			googleApiClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		lat = arg0.getLatitude();
		lng = arg0.getLongitude();
		System.out.println("SERVICE LOCATION CHANGE");
	}
}
