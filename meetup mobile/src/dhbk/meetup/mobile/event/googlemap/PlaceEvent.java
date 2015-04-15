package dhbk.meetup.mobile.event.googlemap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import dhbk.meetup.mobile.R;

public class PlaceEvent extends Activity implements 
		OnMapReadyCallback, LocationListener, OnMarkerDragListener, OnMarkerClickListener, OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener{

	private GoogleMap map;
	private MapFragment mapFragment;
	private GoogleApiClient googleApiClient;
	private LocationRequest locationRequest;
	
	public Location myLocation;
	public LatLng desLocation;
//	public LocationManager locationManager;
	public MarkerOptions markerPlaceMeeting;
	public boolean isMarkerShow = false;
	public double latDes, lngDes;
	
	private EditText ed_place;
	public boolean locationAvaiable = false;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooseplace);
		
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			System.out.println("YESSSSS");
		} else {
			System.out.println("NOOOO");
			Toast.makeText(getApplicationContext(), "Google Play Service not Avaiable", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		locationRequest = new LocationRequest();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
		Intent it = getIntent();
		boolean onlyView = it.getExtras().getBoolean("onlyview");
		String place = it.getExtras().getString("place");
		latDes = it.getExtras().getDouble("lat");
		lngDes = it.getExtras().getDouble("lng");
			
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.chooseplace_map);
		mapFragment.getMapAsync(this);
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        markerPlaceMeeting = new MarkerOptions()
									.title("Place meeting")
									.snippet("Tai : ...")
									.draggable(!onlyView)
									.anchor(0.5f, 1);
        if(!place.equals("")) {
        	markerPlaceMeeting.snippet("Tai : " + place);
        }
        
        Button btn_ok = (Button) findViewById(R.id.chooseplace_btn_ok);
        ed_place = (EditText) findViewById(R.id.chooseplace_ed_place);
        ed_place.setText(place);
        btn_ok.setOnClickListener(this);
        
        if(onlyView) {
        	ed_place.setVisibility(View.GONE);
        	btn_ok.setVisibility(View.GONE);
        }
        
        googleApiClient = new GoogleApiClient.Builder(this)
        						.addApi(LocationServices.API)
        						.addConnectionCallbacks(this)
        						.addOnConnectionFailedListener(this)
        						.build();
        
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.chooseplace_btn_ok :
			String place = ed_place.getText().toString();
			if(place.equals("")) {
				Toast.makeText(getApplicationContext(), "fill all", Toast.LENGTH_SHORT).show();
			} else {
				Intent it = new Intent();
				it.putExtra("place", place);
				it.putExtra("lat", desLocation.latitude);
				it.putExtra("lng", desLocation.longitude);
				setResult(RESULT_OK, it);
				finish();
			}
			break;
		default : break;
		}
		
	}
	
	@Override
	public void onMapReady(GoogleMap arg0) {
		// TODO Auto-generated method stub
		map = arg0;
		map.setOnMarkerDragListener(this);
		map.setOnMarkerClickListener(this);
		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		System.out.println("MAP READY");
		
		// load my location
		if(latDes == 1000 || lngDes == 1000) {
			if(getMyLocation()) {
				desLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
				markerPlaceMeeting.position(desLocation);
				map.addMarker(markerPlaceMeeting).showInfoWindow();
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(desLocation, 13));
			} else {
				System.out.println("LAST LOCATION NULL");
			}
		} else {
			desLocation = new LatLng(latDes, lngDes);
			markerPlaceMeeting.position(desLocation);
			map.addMarker(markerPlaceMeeting).showInfoWindow();
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(desLocation, 13));
		}
		
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		isMarkerShow = !isMarkerShow;
		return isMarkerShow;
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
		desLocation = arg0.getPosition();
		
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		System.out.println("LOCATION CHANGE");
		if(!locationAvaiable && map != null) {
			myLocation = new Location(location);
			locationAvaiable = true;
			
			System.out.println("LOCATION AVAIABLE");
			
			if(latDes == 1000 || lngDes == 1000) {
				desLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
				markerPlaceMeeting.position(desLocation);
				map.clear();
				map.addMarker(markerPlaceMeeting).showInfoWindow();
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(desLocation, 13));
				
			}
		}
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
	
}
