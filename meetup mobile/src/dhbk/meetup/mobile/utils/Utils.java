package dhbk.meetup.mobile.utils;

import android.content.Context;
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
}
