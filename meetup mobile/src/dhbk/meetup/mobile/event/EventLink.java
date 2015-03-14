package dhbk.meetup.mobile.event;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.i;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.adapter.DynamicListView;
import dhbk.meetup.mobile.event.adapter.StableArrayAdapter;
import dhbk.meetup.mobile.event.object.EventObject;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class EventLink extends Activity implements OnClickListener{

	public static final String EVENT_POINT_TO_POINT = "pointtopoint";
	public static final String EVENT_UPDATE_LINK_POINT = "updatelinkpoint";
	
	public static final int REQUESTCODE_CREATEEVENT = 1;
	
	public String idevent;
	public ArrayList<EventObject> listEvent = new ArrayList<EventObject>();
	public ArrayList<String> listIdevent = new ArrayList<String>();
	
	public StableArrayAdapter adapter ;
	public DynamicListView listView ;
	
	private HttpConnect conn;
	private DialogWaiting dialog;
	public LocationManager locationManager;
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_link);
		
		Intent it = getIntent();
		idevent = it.getExtras().getString("idevent");
		
		ImageButton imgbtn_add = (ImageButton) findViewById(R.id.event_link_imgbtn_add);
		imgbtn_add.setOnClickListener(this);
		ImageButton imgbtn_export = (ImageButton) findViewById(R.id.event_link_imgbtn_export);
		imgbtn_export.setOnClickListener(this);
        listView = (DynamicListView) findViewById(R.id.event_link_lv);

        dialog = new DialogWaiting(this);
		conn = new HttpConnect();
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if(Utils.isConnectNetwork(EventLink.this)) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncLoadListLinkEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new asyncLoadListLinkEvent().execute();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Network not connected", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		
		boolean isChange = false;
		if(listEvent.size() == listIdevent.size()) {
			for(int i = 0; i < listEvent.size(); i++) {
				if(!listEvent.get(i).idevent.equals(listIdevent.get(i))) {
					isChange = true;
					break;
				}
			}
		} else {
			isChange = true;
		}
		
		if(isChange) {
			AlertDialog dialog = new AlertDialog.Builder(this)
			.setTitle("Link Point")
			.setMessage("Do you want save change ?")
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(Utils.isConnectNetwork(EventLink.this)) {
						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new asyncUpdateLinkPoint().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							new asyncUpdateLinkPoint().execute();
						}
					} else {
						Toast.makeText(getApplicationContext(), "Network not connected", Toast.LENGTH_SHORT).show();
					}
					dialog.dismiss();
				}
			})
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
					finish();
				}
			})
			.create();
			dialog.show();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.event_link_imgbtn_add :
			Intent it = new Intent(getApplicationContext(), CreateEvent.class);
			it.putExtra("fromLinkEvent", true);
			startActivityForResult(it,REQUESTCODE_CREATEEVENT);
			break;
		case R.id.event_link_imgbtn_export :
			
			break;
		default : break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUESTCODE_CREATEEVENT) {
			if(resultCode == RESULT_OK) {
				EventObject eo = new EventObject(data.getExtras().getString("title"), data.getExtras().getString("own"),
						data.getExtras().getString("place"), data.getExtras().getString("time"), data.getExtras().getString("content"),
						data.getExtras().getString("idevent"), data.getExtras().getString("idown"));
				listEvent.add(eo);
				adapter.putIdMap(eo);
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	public String loadListLinkEvent () {
		String url = Const.DOMAIN_NAME + EVENT_POINT_TO_POINT;
		
		HttpResponse response = null;
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"idevent", idevent});
			response = conn.sendRequestGet(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			return result; 
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE LINK LIST PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE LINK LIST IO";
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
	
	public String updateLinkPoint () {
		String url = Const.DOMAIN_NAME + EVENT_UPDATE_LINK_POINT;
		
		HttpResponse response = null;
		JSONArray jsa = new JSONArray();
		
		try {
			for(int i = 0; i < listEvent.size() - 1; i ++) {
				JSONObject jso_ = new JSONObject();
				jso_.put("idevent", listEvent.get(i).idevent);
				jso_.put("nextpoint", listEvent.get(i + 1).idevent);
				jsa.put(jso_);
			}
			JSONObject jso = new JSONObject();
			jso.put("idevent", listEvent.get(listEvent.size() - 1).idevent);
			jso.put("nextpoint", "0");
			jsa.put(jso);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"pointtopoint", jsa.toString()});
			response = conn.sendRequestPost(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			return result; 
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE UPDATE LINK LIST PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE UPDATE LINK LIST IO";
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
	
	private class asyncLoadListLinkEvent extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return loadListLinkEvent();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.out.println("RESULT LOAD LINK LIST EVENT : " + result);
			try {
				JSONObject jso = new JSONObject(result);System.out.println("10");
				JSONArray jsa_nextpoint = jso.getJSONArray("nextpoint");System.out.println("11");
				JSONArray jsa_prevpoint = jso.getJSONArray("prevpoint");System.out.println("12");
				for(int i = jsa_prevpoint.length() - 1; i >= 0; i--) {
					JSONObject jso1 = jsa_prevpoint.getJSONObject(i);
					listEvent.add(new EventObject(jso1.getString("title"), jso1.getString("name"), jso1.getString("place"),
							jso1.getString("time"), jso1.getString("description"), jso1.getString("idevent"), "0"));
				}
				for(int j = 0; j < jsa_nextpoint.length(); j++) {
					JSONObject jso2 = jsa_nextpoint.getJSONObject(j);
					listEvent.add(new EventObject(jso2.getString("title"), jso2.getString("name"), jso2.getString("place"),
							jso2.getString("time"), jso2.getString("description"), jso2.getString("idevent"), "0"));
				}
				
				adapter = new StableArrayAdapter(EventLink.this, listEvent);
				listView.setCheeseList(listEvent);
		        listView.setAdapter(adapter);
		        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				adapter.notifyDataSetChanged();
				for(EventObject eo : listEvent) {
					listIdevent.add(eo.idevent);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(), "Load Fail", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class asyncUpdateLinkPoint extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return updateLinkPoint();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.out.println("RESULT UPDATE POINT TO POINT : " + result);
			dialog.closeProgressDialog();
			if(result.equals("true")) {
				Toast.makeText(getApplicationContext(), "Save Success", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "Save Fail. Try again!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}
