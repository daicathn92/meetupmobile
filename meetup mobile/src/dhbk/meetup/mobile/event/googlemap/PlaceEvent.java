package dhbk.meetup.mobile.event.googlemap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import dhbk.meetup.mobile.utils.Utils;

public class PlaceEvent extends Activity implements 
		OnMapReadyCallback, LocationListener, OnMarkerDragListener, OnMarkerClickListener, OnClickListener{

	private GoogleMap map;
	private MapFragment mapFragment;
	
	public Location myLocation;
	public LatLng desLocation;
	public LocationManager locationManager;
	public MarkerOptions markerPlaceMeeting;
	public boolean isMarkerShow = false;
	public double latDes, lngDes;
	
	private EditText ed_place;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooseplace);
		
		Intent it = getIntent();
		boolean onlyView = it.getExtras().getBoolean("onlyview");
		String place = it.getExtras().getString("place");
		latDes = it.getExtras().getDouble("lat");
		lngDes = it.getExtras().getDouble("lng");
			
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.chooseplace_map);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        
        mapFragment.getMapAsync(this);
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
		
		// load my location
		getMyLocation();
		
		
		if(myLocation != null) {
			if(latDes == 1000 || lngDes == 1000) desLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			else desLocation = new LatLng(latDes, lngDes);
			markerPlaceMeeting.position(desLocation);
			map.addMarker(markerPlaceMeeting).showInfoWindow();
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(desLocation, 13));
//			map.moveCamera(CameraUpdateFactory.newLatLngZoom(desLocation, 13));
		} else {
			Toast.makeText(getApplicationContext(), "Location not available", Toast.LENGTH_SHORT).show();
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
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public void getMyLocation () {
    	myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(myLocation == null) {
    		myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    		System.out.println("NULLLLLLLLLLLLLLLLLLLLLLLLLL 11111111111111");
    	}
    	if(myLocation == null)
    		System.out.println("NULLLLLLLLLLLLLLLLLLLLLLLLLL 22222222222222222");
    	
//    	System.out.println("AAAAAAAAAAAAAAAAAAA : " + myLocation.getLatitude() + " : " + myLocation.getLongitude());
    }

}
