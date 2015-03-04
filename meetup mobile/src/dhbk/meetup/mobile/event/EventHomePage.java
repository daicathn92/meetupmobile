package dhbk.meetup.mobile.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.adapter.ListEventAdapter;
import dhbk.meetup.mobile.event.adapter.ListInviteAdapter;
import dhbk.meetup.mobile.event.member.BeInvite;
import dhbk.meetup.mobile.event.object.EventObject;
import dhbk.meetup.mobile.event.object.InviteObject;
import dhbk.meetup.mobile.event.service.NewsService;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.login.Login;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class EventHomePage extends Activity implements OnClickListener, OnMenuItemClickListener{

	public static final int REQUESTCODE_TABHOME = 100;
	public static final int TIME_UPDATE = 5000;
	public static final String EVENT_LISTEVENT = "listevent";
	public static final String EVENT_LISTEVENTREGISTERED = "listeventregistered";
	public static final String EVENT_LISTINVITE = "listinvite";
	public static final String EVENT_ADDMEMBER = "addmember";
	
	public static final int MENU_SIGNOUT = Menu.FIRST; 
	
	private HttpConnect conn;
	private ListView lv_event;
	private ListEventAdapter listeventAdapter;
	private ArrayList<EventObject> listevent = new ArrayList<EventObject>();
	private ArrayList<EventObject> listeventupdate;
	private ArrayList<String> listteventregistred = new ArrayList<String>();
	
	private Handler handler_updateEvent;
	private Runnable update = new Runnable() {
		@SuppressLint("NewApi")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Utils.isConnectNetwork(EventHomePage.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncUpdateEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new asyncUpdateEvent().execute() ;
				}
			} else {
				handler_updateEvent.postDelayed(update, TIME_UPDATE);
			}
		}
	};
	
	private DialogWaiting dialog;
	private PopupMenu popupMenu;
	private AtomicBoolean isFilter = new AtomicBoolean(false);
	
	public ListInviteAdapter listinviteAdapter;
	public ArrayList<InviteObject> listinvite = new ArrayList<InviteObject>();
	public BeInvite beInvite;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventhomepage);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
//		handler_updateEvent = new Handler();
//		handler_updateEvent.post(update);
		
//		listevent.add(new EventObject("a", "", "", "", "", "", ""));
		listeventAdapter = new ListEventAdapter(EventHomePage.this, listevent);
		
		conn = new HttpConnect();
		dialog = new DialogWaiting(this);
		
		lv_event = (ListView) findViewById(R.id.homepage_lv_event);
		lv_event.setAdapter(listeventAdapter);
		lv_event.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						Intent it = new Intent(EventHomePage.this, AEvent.class);
						String idevent_ = listevent.get(arg2).idevent;
						it.putExtra("idevent", idevent_);
						boolean b = false;
						for(String str : listteventregistred) {
							System.out.println("IDEVENT REGISTRED : " + str);
							if(idevent_.equals(str)) {
								b = true;
								break;
							}
						}
						it.putExtra("ismember", b);
						it.putExtra("iduser", "none");
						it.putExtra("idusercreate", listevent.get(arg2).idown);
						startActivityForResult(it, REQUESTCODE_TABHOME);
					}
		});
		
		ImageButton imgbtn_createevent = (ImageButton) findViewById(R.id.homepage_imgbtn_createevent);
		imgbtn_createevent.setOnClickListener(this);
		final ImageButton imgbtn_filter = (ImageButton) findViewById(R.id.homepage_imgbtn_filter);
		imgbtn_filter.setOnClickListener(this);
		ImageButton imgbtn_listinvite = (ImageButton) findViewById(R.id.homepage_imgbtn_listinvite);
		imgbtn_listinvite.setOnClickListener(this);
		popupMenu = new PopupMenu(this, imgbtn_filter);
		popupMenu.setOnMenuItemClickListener(this);
		popupMenu.getMenuInflater().inflate(R.menu.menufilter, popupMenu.getMenu());
		
		// dialog list invite
		listinviteAdapter = new ListInviteAdapter(this, listinvite);
		beInvite = new BeInvite(this);
		
		// load new news
		if(Utils.isConnectNetwork(EventHomePage.this)) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncListEventRegistered().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new asyncListEventRegistered().execute();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Network not connected", Toast.LENGTH_SHORT).show();
		}
		
		// start newsservice
		System.out.println("START SERVICE");
		Intent it = new Intent(getApplicationContext(), NewsService.class);
		it.putExtra("iduser", Const.iduser);
		startService(it);
		
		// save username, password
		SharedPreferences prefs = getSharedPreferences(Const.PREFERENCE_MEETUP_MOBILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("username", Const.username);
		editor.putString("password", Const.password);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_SIGNOUT, 0, "Sign out");
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("RESULT FOR ACTIVITY OK 2");
		if(requestCode == REQUESTCODE_TABHOME) {
			System.out.println("RESULT FOR ACTIVITY OK 1");
			if(resultCode == RESULT_OK) {
				try {
					System.out.println("RESULT FOR ACTIVITY OK");
					String idevent_ = data.getExtras().getString("idevent");
					String idusercreate_ = data.getExtras().getString("idusercreate");
					listteventregistred.add(idevent_);
					
					Intent it = new Intent(EventHomePage.this, AEvent.class);
					it.putExtra("idevent", idevent_);
					it.putExtra("ismember", true);
					it.putExtra("iduser", "none");
					it.putExtra("idusercreate", idusercreate_);
					startActivityForResult(it, REQUESTCODE_TABHOME);
					System.out.println("START ACTIVITY OK");
				} catch (Exception e) {}
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case MENU_SIGNOUT :
			SharedPreferences prefs = getSharedPreferences(Const.PREFERENCE_MEETUP_MOBILE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("username", "");
			editor.putString("password", "");
			editor.commit();
			Intent itS = new Intent(getApplicationContext(), NewsService.class);
			itS.putExtra("iduser", "0");
			startService(itS);
			Intent it = new Intent(getApplicationContext(), Login.class);
			startActivity(it);
			finish();
			return true;
		default : return false;
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.homepage_imgbtn_listinvite :
			if(Utils.isConnectNetwork(EventHomePage.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncListInvite().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new asyncListInvite().execute() ;
				}
			} else {
				Toast.makeText(getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.homepage_imgbtn_createevent :
			Intent it = new Intent(getApplicationContext(), CreateEvent.class);
			startActivity(it);
			break;
		case R.id.homepage_imgbtn_filter :
			popupMenu.show();
			break;
		default : break; 
		}
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		String filter = "";
		switch(item.getItemId()) {
		case R.id.filter_all :
			filter = "all";
			break;
		case R.id.filter_own :
			filter = "own";
			break;
		case R.id.filter_registered :
			filter = "registered";
			break;
		case R.id.filter_occurred :
			filter = "occurred";
			break;
		default : return false;
		}
		
		if(filter.equals("")){
			return false;
		} else {
			if(Utils.isConnectNetwork(EventHomePage.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncUpdateEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, filter);
				} else {
					new asyncUpdateEvent().execute(filter);
				}
			} else {
				Toast.makeText(getApplicationContext(), "Network not connected", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
	}
	
	public boolean updateEvent(String filter) {
		String url = Const.DOMAIN_NAME + EVENT_LISTEVENT;
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"filter", filter});
			values.add(new String[] {"iduser", Const.iduser});
			HttpResponse response = conn.sendRequestGet(url, null, values);
			listeventupdate = listeventFromJson(new JSONObject(EntityUtils.toString(response.getEntity())));
//			if(listeventupdate.size() > 0)
				return true;
//			else return false;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			dialog.closeProgressDialog();
		}
		
	}
	
	public String listeventregistered () {
		String url = Const.DOMAIN_NAME + EVENT_LISTEVENTREGISTERED;
		HttpResponse response = null;
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"iduser", Const.iduser});
			response = conn.sendRequestGet(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE IO";
		}  finally {
			dialog.closeProgressDialog();
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
	
	public String listInvite () {
		String url = Const.DOMAIN_NAME + EVENT_LISTINVITE;
		HttpResponse response = null;
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"iduser", Const.iduser});
			values.add(new String[] {"type", "all"});
			response = conn.sendRequestGet(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("RESULT BE INVITE : " + result);
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE BE INVITE PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE BE INVITE IO";
		} finally {
			dialog.closeProgressDialog(); 
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
	
	public void acceptInvite (String position) {
		if(Utils.isConnectNetwork(EventHomePage.this)) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncAccept().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, position);
			} else {
				new asyncAccept().execute(position);
			}
		} else {
			Toast.makeText(getApplicationContext(), "Network not connected", Toast.LENGTH_SHORT).show();
		}
	}
	
	public String acceptInviteInBackgroud (String pos) {
		String url = Const.DOMAIN_NAME + EVENT_ADDMEMBER;
		HttpResponse response = null;
		
		try {
			int position = Integer.parseInt(pos);
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"iduser", Const.iduser});
			values.add(new String[] {"idevent", listinvite.get(position).idevent});
			values.add(new String[] {"idinvite", listinvite.get(position).idinvite});
			response = conn.sendRequestPost(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("RESULT ACCEPT INVITE : " + result);
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE ACCEPT INVITE PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE ACCEPT INVITE IO";
		} finally {
			dialog.closeProgressDialog(); 
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
	
	public void changeDataIfNeed (boolean isChange) {
		if(isChange) {
			listevent.clear();
			for(EventObject eo : listeventupdate) {
				listevent.add(new EventObject(eo));
			}
			System.out.println("SIZE : " + listevent.size());
			listeventAdapter.notifyDataSetChanged();
		} else {
			
		}
	}
	
	public ArrayList<EventObject> listeventFromJson (JSONObject jso_parent) throws JSONException {
		System.out.println("LISTEVENT JSON : " + jso_parent.toString());
		ArrayList<EventObject> arr = new ArrayList<EventObject>();
		JSONArray jsa_listevent = jso_parent.getJSONArray("listevent");
		for(int i = 0; i < jsa_listevent.length(); i++) {
			JSONObject jso = jsa_listevent.getJSONObject(i);
			arr.add(new EventObject(jso.getString("title"), jso.getString("name"), jso.getString("place"), jso.getString("time"),
							jso.getString("description"), jso.getString("idevent"), jso.getString("iduser")));
		}
		return arr;
	}
	
	private class asyncUpdateEvent extends AsyncTask<String, Void, Boolean> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return updateEvent(params[0]);
		}
		
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result)
				changeDataIfNeed(true);
			dialog.closeProgressDialog();
//			handler_updateEvent.postDelayed(update, TIME_UPDATE);
		};
	}
	
	// lay danh sach su kien da dang ki tham gia
	private class asyncListEventRegistered extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return listeventregistered();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			
			try {
				JSONObject jso_parent = new JSONObject(result);
				JSONArray jsa_listevent = jso_parent.getJSONArray("listevent");
				for(int i = 0; i < jsa_listevent.length(); i++) {
					JSONObject jso = jsa_listevent.getJSONObject(i);
					listteventregistred.add(jso.getString("idevent"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// load new news event when finish load listeventregistered
			if(Utils.isConnectNetwork(EventHomePage.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncUpdateEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "all");
				} else {
					new asyncUpdateEvent().execute("all");
				}
			} 
		}
		
	}
	
	private class asyncListInvite extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return listInvite();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			try {
				JSONObject jso_parent = new JSONObject(result);
				JSONArray jsa_listinvite = jso_parent.getJSONArray("listinvite");
				listinvite.clear();
				for(int i = 0; i < jsa_listinvite.length(); i++) {
					JSONObject jso = jsa_listinvite.getJSONObject(i);
					listinvite.add(new InviteObject(jso.getString("idinvite"), jso.getString("idevent"),
							jso.getString("name"), jso.getString("title")));
				}
				listinviteAdapter.notifyDataSetChanged();
				beInvite.dialog.show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class asyncAccept extends AsyncTask<String, Void, String> {

		String pos;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			pos = params[0];
			return acceptInviteInBackgroud(pos);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result.equals("true")) {
				Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
				try {
					int position = Integer.parseInt(pos);
					listteventregistred.add(listinvite.get(position).idevent);
					listinvite.get(position).isVisible = false;
					listinviteAdapter.notifyDataSetChanged();
				} catch (Exception e){}
			} else {
				Toast.makeText(getApplicationContext(), "Try Again !", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}
