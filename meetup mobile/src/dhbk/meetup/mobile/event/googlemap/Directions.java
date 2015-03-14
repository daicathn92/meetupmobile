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
import android.os.AsyncTask;
import android.os.Build;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import dhbk.meetup.mobile.httpconnect.HttpConnect;

public class Directions {

	public static final String DOMAIN_DIRECTION = "http://maps.googleapis.com/maps/api/directions/json";
	public static final String MODE_WAY_WALKING = "walking";
	public static final String MODE_WAY_BICYCLING = "bicycling";
	public static final String MODE_WAY_DRIVING = "driving";
	
	private GoogleMap map;
	private HttpConnect conn;
	public String modeWay = MODE_WAY_WALKING;
	private LatLng from, to;
	
	public ArrayList<LatLng> listPolyline;
	
	public Directions(GoogleMap map) {
		this.map = map;
		this.conn = new HttpConnect();
	}
	
	@SuppressLint("NewApi")
	public void findDirection (LatLng from, LatLng to, String modeWay) {
		this.from = from;
		this.to = to;
		this.modeWay = modeWay;
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new LoadDirection().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new LoadDirection().execute() ;
		}
	}
	
//	Iterable<LatLng> iterableLatLng;
	
	public void drawDirection () {
//		map.moveCamera(CameraUpdateFactory.newLatLngZoom(from, 16));
//		iterableLatLng = (Iterable<LatLng>) listLatLng.iterator();
//		PolylineOptions polylineOptions = new PolylineOptions();
		if(listPolyline != null)
			map.addPolyline(new PolylineOptions().geodesic(true)
								.addAll(listPolyline));
	}
	
	public String loadDirection () {
		HttpResponse response = null;
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[]{"origin", from.latitude+ "," + from.longitude});
			values.add(new String[]{"destination", to.latitude+ "," + to.longitude});
			values.add(new String[]{"sensor", "false"});
			values.add(new String[]{"mode", modeWay});
			
			response = conn.sendRequestGet(DOMAIN_DIRECTION, null, values);
			String result = EntityUtils.toString(response.getEntity());
//			System.out.println("RESULT DIRECTION : " + result);
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE DIRECTION PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE DIRECTION IO";
		} finally {
			if (response != null) {
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public ArrayList<LatLng> parseDirection (String result) {
		ArrayList<LatLng> listLatLng = new ArrayList<LatLng>();
		try {
			JSONObject jso_parent = new JSONObject(result);
			JSONArray jsa_routes = jso_parent.getJSONArray("routes");
			for(int i =0; i < jsa_routes.length(); i++) {
				JSONObject jso_route = jsa_routes.getJSONObject(i);
				JSONArray jsa_legs = jso_route.getJSONArray("legs");
				for(int j = 0; j < jsa_legs.length(); j++) {
					JSONObject jso_leg = jsa_legs.getJSONObject(j);
					JSONArray jsa_steps = jso_leg.getJSONArray("steps");
					for(int k = 0; k < jsa_steps.length(); k++) {
						JSONObject jso_step = jsa_steps.getJSONObject(k);
						JSONObject jso_polyline = jso_step.getJSONObject("polyline");
						String points = jso_polyline.getString("points");
						// decode points
						ArrayList<LatLng> list = decodePolylineString(points);
						for(LatLng latlng : list) {
							listLatLng.add(latlng);
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listLatLng;
	}
	
	public ArrayList<LatLng> decodePolylineString (String points) {
		ArrayList<LatLng> list = new ArrayList<LatLng>();
		// decode
		int index = 0, len = points.length();
	        int lat = 0, lng = 0;
	        while (index < len) {
	            int b, shift = 0, result = 0;
	            do {
	                b = points.charAt(index++) - 63;
	                result |= (b & 0x1f) << shift;
	                shift += 5;
	            } while (b >= 0x20);
	            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	            lat += dlat;
	            shift = 0;
	            result = 0;
	            do {
	                b = points.charAt(index++) - 63;
	                result |= (b & 0x1f) << shift;
	                shift += 5;
	            } while (b >= 0x20);
	            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	            lng += dlng;
	 
	            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
	            list.add(position);
	        }
		return list;
	}
	
	private class LoadDirection extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return loadDirection();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result != null) {
				listPolyline = parseDirection(result);
				drawDirection();
			} else {
				System.out.println("RESULT DIRECTION NULL");
			}
		}
		
	}
	
}
