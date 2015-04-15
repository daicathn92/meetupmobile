package dhbk.meetup.mobile.event.googlemap;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.Utils;

public class TrackGPS extends Activity implements
						OnMapReadyCallback, LocationListener, OnMarkerClickListener,
						ConnectionCallbacks, OnConnectionFailedListener{

	public static final String TRACKGPS_MEMBER = "location";
	public static final long TIMEUPDATE_LOCATION = 30000;
	
	private HttpConnect conn;
	private GoogleMap map;
	private MapFragment mapFragment;
	private Directions directions;
	
	public Location myLocation;
	public LatLng desLocation;
//	public LocationManager locationManager;
	private GoogleApiClient googleApiClient;
	private LocationRequest locationRequest;
	public MarkerOptions markerPlaceMeeting;
	public boolean isMarkerShow = false;
	public ArrayList<Marker> listMarkers = new ArrayList<Marker>();
	
	public String listId = "";
	private boolean locationAvaiable = false;
	public Handler handler;
	public Runnable updateLocation = new Runnable() {
		
		@SuppressLint("NewApi")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Utils.isConnectNetwork(TrackGPS.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncTrackGPS().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new asyncTrackGPS().execute() ;
				}
			} else {
				handler.postDelayed(updateLocation, TIMEUPDATE_LOCATION * 2);
			}
		}
	};
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_tracker);
        
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			System.out.println("YESSSSS");
		} else {
			System.out.println("NOOOO");
			Toast.makeText(getApplicationContext(), "Google Play Service not Avaiable", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
        Intent it = getIntent();
//		boolean onlyView = it.getExtras().getBoolean("onlyview");
		String place = it.getExtras().getString("place");
		listId = it.getExtras().getString("listid");
		System.out.println("LIST ID :" + listId);
		double lat = it.getExtras().getDouble("lat");
		double lng = it.getExtras().getDouble("lng");
		
		conn = new HttpConnect();
		handler = new Handler();
		desLocation = new LatLng(lat, lng);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.maptracker_map);
        mapFragment.getMapAsync(this);
        markerPlaceMeeting = new MarkerOptions()
									.title("Place meeting")
									.snippet("Tai : " + place)
									.anchor(0.5f, 1);
        
        googleApiClient = new GoogleApiClient.Builder(this)
								.addApi(LocationServices.API)
								.addConnectionCallbacks(this)
								.addOnConnectionFailedListener(this)
								.build();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		googleApiClient.connect();
	}
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	googleApiClient.disconnect();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	handler.removeCallbacks(updateLocation);
    }

	@Override
	public void onMapReady(GoogleMap arg0) {
		// TODO Auto-generated method stub
		System.out.println("MAP READY");
		map = arg0;
		map.setOnMarkerClickListener(this);
		directions = new Directions(map);
		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		
		// load my location
//		if(getMyLocation()) {
//			LatLng from = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
////			System.out.println(from.latitude + " : " + from.longitude);
////			System.out.println(desLocation.latitude + " : " + desLocation.longitude);
//			map.animateCamera(CameraUpdateFactory.newLatLngZoom(from, 13));
//			
//			// find path
//			directions.findDirection(from, desLocation, Directions.MODE_WAY_DRIVING);
//		} else {
//			System.out.println("LAST LOCATION NULL");
//		}
		handler.post(updateLocation);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		System.out.println("LOCATION CHANGE");
		if(!locationAvaiable && map != null) {
			myLocation = new Location(location);
			locationAvaiable = true;
			LatLng from = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(from, 13));
			
			markerPlaceMeeting.position(desLocation);
			map.addMarker(markerPlaceMeeting).showInfoWindow();
			// find path
			directions.findDirection(from, desLocation, Directions.MODE_WAY_DRIVING);
			
			System.out.println("LOCATION AVAIABLE");
		}
	}
	
	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		isMarkerShow = !isMarkerShow;
		return isMarkerShow;
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
	}
	
	public boolean getMyLocation () {
		myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
		if(myLocation == null)
			return false;
		return true;
    }
    
    public String loadLocation () {
    	String url = Const.DOMAIN_NAME + TRACKGPS_MEMBER;
    	
    	HttpResponse response = null;
    	
    	try {
    		ArrayList<String[]> values = new ArrayList<String[]>();
    		values.add(new String[] {"listid", listId});
			response = conn.sendRequestGet(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE TRACK GPS PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE TRACK GPS IO";
		} finally {
			if(response != null) {
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
    
    public void updateLocation (String result) {
//    	map.clear();
//    	if(myLocation != null) { 
//			LatLng from = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			
			// find path
//			directions.drawDirection();
//			map.addMarker(markerPlaceMeeting).showInfoWindow();
//		}
    	
    	// add & change position marker member
    	try {
    		System.out.println("RESULT GPS TRACK : " + result);
			JSONObject jso_parent = new JSONObject(result);
			JSONArray jsa_listlocation = jso_parent.getJSONArray("listlocation");
			
			if(listMarkers.size() == 0) {
				for(int i = 0; i < jsa_listlocation.length(); i++) {
					JSONObject jso = jsa_listlocation.getJSONObject(i);
					String _location = jso.getString("location");
					String []__location;
					if(!_location.equals("")) {
						__location = _location.split(";");
					} else {
						__location = new String[] {"0", "0", "Not Update"};
					}
					Marker marker = map.addMarker(new MarkerOptions()
										.title(jso.getString("name"))
										.snippet(Utils.formatTimeRelatively(__location[2]))
										.anchor(0.5f, 1)
										.position(new LatLng(Double.parseDouble(__location[0]), Double.parseDouble(__location[1])))
										.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
										.visible(!_location.equals(""))) ;		
					listMarkers.add(marker);
				}
	    	} else {
	    		for(int i = 0; i < jsa_listlocation.length(); i++) {
					JSONObject jso = jsa_listlocation.getJSONObject(i);
					String _location = jso.getString("location");
					String []__location;
					if(!_location.equals("")) {
						__location = _location.split(";");
					} else {
						__location = new String[] {"0", "0", "Not Update"};
					}
					Marker marker = listMarkers.get(i); 
					marker.setSnippet(Utils.formatTimeRelatively(__location[2]));
					marker.setPosition(new LatLng(Double.parseDouble(__location[0]), Double.parseDouble(__location[1])));
					marker.setVisible(!_location.equals(""));
					if(marker.isInfoWindowShown()) {
						marker.hideInfoWindow();
						marker.showInfoWindow();
					}
				}
	    	}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    private class asyncTrackGPS extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return loadLocation();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result == null) {
				Toast.makeText(getApplicationContext(), "load location member fail", Toast.LENGTH_SHORT).show();
			} else {
				updateLocation(result);
			}
			handler.postDelayed(updateLocation, TIMEUPDATE_LOCATION);
		}
    	
    }

}
