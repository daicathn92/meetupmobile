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
import dhbk.meetup.mobile.utils.Const;
import android.os.AsyncTask;

public class UpdateInvite extends AsyncTask<String, Void, String> {

	private NewsService newsService;
	
	public UpdateInvite (NewsService newsService) {
		this.newsService = newsService;
	}
	
	public String updateInvite () {
		String url = Const.DOMAIN_NAME + NewsService.EVENT_GETINVITE;
		
		HttpResponse response = null;
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"iduser", newsService.iduser});
			values.add(new String[] {"type", "notwatch"});
			response = newsService.connNotify.sendRequestGet(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
//			System.out.println("RESULT INVITE : " + result);
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE INVITE PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE INVITE IO";
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
		return updateInvite();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		try {
			JSONObject jso_parent = new JSONObject(result);
			JSONArray jsa_listinvite = jso_parent.getJSONArray("listinvite");
			for(int i =0; i < jsa_listinvite.length(); i++) {
				JSONObject jso = jsa_listinvite.getJSONObject(i);
				new Notifications(newsService.iduser, jso.getString("iduser"), jso.getString("idevent"), "Meetup Invite", jso.getString("name") + " moi ban tham gia cuoc gap " + jso.getString("title"), false, newsService.getApplicationContext()).showNotify();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		newsService.handlerInvite.postDelayed(newsService.getlistInvite, NewsService.TIME_REPOST_CONNECT);
	}
}
