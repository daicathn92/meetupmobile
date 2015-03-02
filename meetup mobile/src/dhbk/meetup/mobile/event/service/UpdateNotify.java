package dhbk.meetup.mobile.event.service;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dhbk.meetup.mobile.event.notify.Notifications;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import android.os.AsyncTask;
import android.text.style.ReplacementSpan;

public class UpdateNotify extends AsyncTask<String, Void, String>{

	public static final String EVENT_GETNOTIFY = "listnotify";
	private NewsService newsService;
	
	public UpdateNotify(NewsService newsService) {
		// TODO Auto-generated constructor stub
		this.newsService= newsService;
	}
	
	private String updateNotify () {
		String url = Const.DOMAIN_NAME + EVENT_GETNOTIFY;
		HttpResponse response = null;
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"iduser", newsService.iduser});
			System.out.println("IDUSER : " + newsService.iduser);
			response = newsService.connNotify.sendRequestGet(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("RESULT NOTIFY : " + result);
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE GET NOTIFY PROTOCOL ";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE GET NOTIFY IO";
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
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		return updateNotify();
	}
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		try {
			JSONObject jso_parent = new JSONObject(result);
			JSONArray jsa_listnotify = jso_parent.getJSONArray("listnotify");
			for(int i =0; i < jsa_listnotify.length(); i++) {
				JSONObject jso = jsa_listnotify.getJSONObject(i);
				new Notifications(jso.getString("idevent"), "Nhac nho", jso.getString("name") + "nhac ban den cuoc gap " + jso.getString("title"), newsService.getApplicationContext()).showNotify();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newsService.handlerNotify.postDelayed(newsService.getlistNotify, 10000);
	}

}
