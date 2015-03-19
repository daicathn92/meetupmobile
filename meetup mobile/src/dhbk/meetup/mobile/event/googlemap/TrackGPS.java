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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.Toast;

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

public class TrackGPS extends Activity implements OnMapReadyCallback, LocationListener, OnMarkerClickListener{

	public static final String TRACKGPS_MEMBER = "location";
	public static final long TIMEUPDATE_LOCATION = 15000;
	
	private HttpConnect conn;
	private GoogleMap map;
	private MapFragment mapFragment;
	private Directions directions;
	
	public Location myLocation;
	public LatLng desLocation;
	public LocationManager locationManager;
	public MarkerOptions markerPlaceMeeting;
	public boolean isMarkerShow = false;
	public ArrayList<Marker> listMarkers = new ArrayList<Marker>();
	
	public String listId = "";
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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        markerPlaceMeeting = new MarkerOptions()
									.title("Place meeting")
									.snippet("Tai : " + place)
									.anchor(0.5f, 1);
        
        mapFragment.getMapAsync(this);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	if(Utils.isGPSEnable(locationManager)) {
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);
    		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
    	} 
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	locationManager.removeUpdates(this);
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
		getMyLocation();
		if(myLocation != null) { 
			LatLng from = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
//			System.out.println(from.latitude + " : " + from.longitude);
//			System.out.println(desLocation.latitude + " : " + desLocation.longitude);
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(from, 13));
			
			if(myLocation == null) {
				Toast.makeText(getApplicationContext(), "Location not available", Toast.LENGTH_SHORT).show();
				return;
			} else {
				// find path
				directions.findDirection(from, desLocation, Directions.MODE_WAY_DRIVING);
				markerPlaceMeeting.position(desLocation);
				map.addMarker(markerPlaceMeeting).showInfoWindow();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Location not available", Toast.LENGTH_SHORT).show();
		}
		handler.post(updateLocation);
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		isMarkerShow = !isMarkerShow;
		return isMarkerShow;
	}
	
    public void getMyLocation () {
    	myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(myLocation == null)
    		myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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
    	if(myLocation != null) { 
//			LatLng from = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			
			// find path
//			directions.drawDirection();
//			map.addMarker(markerPlaceMeeting).showInfoWindow();
		}
    	
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
