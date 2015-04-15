package dhbk.meetup.mobile.event.service;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import dhbk.meetup.mobile.utils.Const;

public class UpdateLocation extends AsyncTask<String, Void, String>{

	private NewsService newsService;
	
	public UpdateLocation (NewsService newsService) {
		this.newsService = newsService;
	}
	
	public void updateMyLocation () {
		System.out.println("START UPDATE  LOCATION");
//		Location myLocation = newsService.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		if(myLocation == null) {
//			myLocation = newsService.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//			if(myLocation == null){
//				System.out.println("LOCATION NOT AVAIABLE");
//				return;
//			}
//		}
		
		String url = Const.DOMAIN_NAME + NewsService.UPDATE_MYLOCATION;
		HttpResponse response = null;
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[]{"iduser", newsService.iduser});
			values.add(new String[]{"location", newsService.lat + ";" + newsService.lng
													+ ";" + System.currentTimeMillis()});
			response = newsService.connLocation.sendRequestPost(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("RESULT LOCATION : " + result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		updateMyLocation();
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		newsService.handlerLocation.postDelayed(newsService.updateMyLocation, NewsService.TIME_REPOST_CONNECT * 2);
	}

}
