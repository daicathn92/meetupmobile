package dhbk.meetup.mobile.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateUtils;

import dhbk.meetup.mobile.event.object.EventObject;

public class FormatFileApp {

	public static String formatEventToFile (ArrayList<EventObject> listevent ) {
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
		JSONArray jsa = new JSONArray();
		try {
			long dayStart = dayFormat.parse(listevent.get(0).time.split(" ")[0]).getTime();
			
			for(EventObject eo : listevent)  {
				JSONObject jso = new JSONObject();
				jso.put("title", eo.title);
				jso.put("place", eo.place);
				jso.put("description", eo.content);
				String timeOrigin[] = eo.time.split(" ");
				jso.put("time", Utils.deltaDay(dayStart, dayFormat.parse(timeOrigin[0]).getTime()) + ";" + timeOrigin[1]);
				jsa.put(jso);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsa.toString();
	}
	
	public static ArrayList<EventObject> formatFileToEvent (String template) {
		ArrayList<EventObject> listEvent = new ArrayList<EventObject>();
		try {
			JSONArray jsa = new JSONArray(template);
			for(int i = 0; i < jsa.length(); i++) {
				JSONObject jso = jsa.getJSONObject(i);
				String[] time = jso.getString("time").split(";");
				EventObject eo = new EventObject(jso.getString("title"), "me", jso.getString("place"), time[0], time[1], 
											jso.getString("description"), i+"", Const.iduser);
				listEvent.add(eo);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listEvent;
	}
}
