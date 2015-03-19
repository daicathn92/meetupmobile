package dhbk.meetup.mobile.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	public static boolean isConnectNetwork (Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE) ;
			NetworkInfo netinfo = cm.getActiveNetworkInfo() ;
			if(netinfo != null && netinfo.isConnected())
				return true;
			else 
				return false ;
		} catch (Exception e){
			return false;
		}
	}
	
	public static boolean isGPSEnable (LocationManager locationManager) {
    	return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    
    public static boolean isNetworkEnable (LocationManager locationManager) {
    	return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    
    public static String formatTimeRelatively (String input) {
    	if(input.equals("Not Update"))
    		return input;
    	else {
    		long inputTime = Long.parseLong(input);
    		long deltaTime = (System.currentTimeMillis() - inputTime) / 1000;
    		if(deltaTime < 60)
    			return deltaTime + " giay truoc";
    		else if(deltaTime < 60 * 60) 
    			return (deltaTime /60) + " phut truoc";
			else if (deltaTime < 60 * 60 * 24)
				return (deltaTime / (60 * 24)) + " gio truoc";
			else
				return "hon 1 ngay truoc";
    	}
    }
    
    public static long deltaDay (long startDay, long endDay) {
    	long delta = endDay - startDay + 1000*60*60*12;
//    	System.out.println("START DAY : " + startDay);
//    	System.out.println("END DAY : " + endDay);
    	return (delta) / (1000*60*60*24);
    }
}
