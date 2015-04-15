package dhbk.meetup.mobile.event;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.datatype.Duration;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.adapter.DynamicListView;
import dhbk.meetup.mobile.event.adapter.StableArrayAdapter;
import dhbk.meetup.mobile.event.googlemap.Directions;
import dhbk.meetup.mobile.event.object.EventObject;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.FormatFileApp;
import dhbk.meetup.mobile.utils.Storage;
import dhbk.meetup.mobile.utils.Utils;

public class EventLink extends Activity implements OnClickListener{

	public static final String EVENT_POINT_TO_POINT = "pointtopoint";
	public static final String EVENT_UPDATE_LINK_POINT = "updatelinkpoint";
	public static final String EVENT_LISTEVENTREGISTERED = "listeventregistered";
	
	public static final int REQUESTCODE_CREATEEVENT = 1;
	
	public String idevent;
	public String idusercreate;
	public ArrayList<EventObject> listEvent = new ArrayList<EventObject>();
	public ArrayList<String> listIdevent = new ArrayList<String>();
	private ArrayList<String> listeventregistred = new ArrayList<String>();
	private long timeDuration = 0;
	
	public StableArrayAdapter adapter ;
	public DynamicListView listView ;
	public ImageButton imgbtn_add;
	
	private HttpConnect conn;
	private DialogWaiting dialog;
	public LocationManager locationManager;
	
	public ArrayList<String> listFiles;
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_link);
		
		Intent it = getIntent();
		idevent = it.getExtras().getString("idevent");
		idusercreate = it.getExtras().getString("idusercreate");
		
		imgbtn_add = (ImageButton) findViewById(R.id.event_link_imgbtn_add);
		imgbtn_add.setOnClickListener(this);
		ImageButton imgbtn_export = (ImageButton) findViewById(R.id.event_link_imgbtn_export);
		imgbtn_export.setOnClickListener(this);
        listView = (DynamicListView) findViewById(R.id.event_link_lv);
        listView.setOnItemClickListener(mOnItemClickListener);

        dialog = new DialogWaiting(this);
		conn = new HttpConnect();
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if(Utils.isConnectNetwork(EventLink.this)) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncListEventRegistered().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new asyncListEventRegistered().execute();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Network not connected", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		for(EventObject eo : listEvent) {
			System.out.println("EOO : " + eo.place);
		}
		
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
					if(listEvent.size() >= 2) {
						if(Utils.isConnectNetwork(EventLink.this)) {
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								new asyncCheckOverTime().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");
							} else {
								new asyncCheckOverTime().execute("0");
							}
						} else {
							Toast.makeText(getApplicationContext(), "Network not connected", Toast.LENGTH_SHORT).show();
						}
					} else {
						if(Utils.isConnectNetwork(EventLink.this)) {
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								new asyncUpdateLinkPoint().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							} else {
								new asyncUpdateLinkPoint().execute();
							}
						} else {
							Toast.makeText(getApplicationContext(), "Network not connected", Toast.LENGTH_SHORT).show();
						}
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
	
	private OnItemClickListener mOnItemClickListener = 
   		 new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					Intent it = new Intent(EventLink.this, AEventOfTour.class);
					String idevent_ = listEvent.get(arg2).idevent;
					it.putExtra("idevent", idevent_);
					it.putExtra("ismember", true);
					it.putExtra("iduser", "none");
					if(listView.isDraggable)
							it.putExtra("idusercreate", Const.iduser);
					else 	it.putExtra("idusercreate", listEvent.get(arg2).idown);
					EventLink.this.startActivity(it);
				}
	};

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
			listFiles = Storage.getListFile(EventLink.this);
			showDialogExportFile();
			
			// show result
			ArrayList<String> arr = Storage.getListFile(EventLink.this);
			int count = 0;
			for(String s : arr) {
				
				System.out.println(count + " : " + s);
				String result = Storage.readFile(s, EventLink.this);
				System.out.println(result);
				count++;
			}
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
	
	public void showDialogExportFile () {
		final EditText input = new EditText(EventLink.this);
		AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(EventLink.this)
				.setTitle("Export file")
				.setMessage("File name")
				.setView(input)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(input.getText().toString().equals("")) {
							Toast.makeText(EventLink.this, "Fill file name", Toast.LENGTH_SHORT).show();
							showDialogExportFile();
							dialog.dismiss();
						} else {
							boolean isDuppliacte = false;
							for(String s : listFiles) {
								if(s.equals(input.getText().toString()+".meetup")) {
									isDuppliacte = true;
									break;
								}
							}
							if(isDuppliacte) {
//								Toast.makeText(EventLink.this, "", Toast.LENGTH_SHORT).show();
								
								new AlertDialog.Builder(EventLink.this)
								.setTitle("Dupplicate")
								.setMessage("File is exist. \nDo you want replace")
								.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										if(Storage.writerFile(input.getText().toString(), FormatFileApp.formatEventToFile(listEvent), EventLink.this)) {
											Toast.makeText(EventLink.this, "File saved", Toast.LENGTH_SHORT).show();
											dialog.dismiss();
										} else {
											Toast.makeText(EventLink.this, "Save Fail", Toast.LENGTH_SHORT).show();
											dialog.dismiss();
										}
									}
									
								})
								.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}
								})
								.show();
							} else {
								if(Storage.writerFile(input.getText().toString(), FormatFileApp.formatEventToFile(listEvent), EventLink.this)) {
									Toast.makeText(EventLink.this, "File saved", Toast.LENGTH_SHORT).show();
									dialog.dismiss();
								} else {
									Toast.makeText(EventLink.this, "Save Fail. Try again", Toast.LENGTH_SHORT).show();
//									dialog.dismiss();
								}
							}
						}
					}
					
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				})
				.show();
	}
	
	//--------------------------AsyncTask------------------------------------
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
	
	public boolean getTimeDuration (int index) {
		String url = Directions.DOMAIN_DIRECTION;
		
		HttpResponse response = null;
		String from[] = listEvent.get(index).place.split(";");
		String to[]   = listEvent.get(index+1).place.split(";");
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[]{"origin", from[1]+ "," + from[2]});
			values.add(new String[]{"destination", to[1] + "," + to[2]});
			values.add(new String[]{"sensor", "false"});
			values.add(new String[]{"mode", Directions.MODE_WAY_DRIVING});
			
			response = conn.sendRequestGet(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			long duration = 0;
			JSONObject jso_parent = new JSONObject(result);
			String status = jso_parent.getString("status");
			if(status.equals("OK")) {
				JSONArray jsa_routes = jso_parent.getJSONArray("routes");
				for(int i =0; i < jsa_routes.length(); i++) {
					JSONObject jso_route = jsa_routes.getJSONObject(i);
					JSONArray jsa_legs = jso_route.getJSONArray("legs");
					for(int j = 0; j < jsa_legs.length(); j++) {
						JSONObject jso_leg = jsa_legs.getJSONObject(j);
						JSONObject jso_duration = jso_leg.getJSONObject("duration");
						duration += jso_duration.getLong("value");
					}
				}
			} 
//			System.out.println("RESULT DIRECTION : " + result);
			timeDuration = duration;
			return true;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return false;
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
		
		@SuppressLint("NewApi")
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
					listeventregistred.add(jso.getString("idevent"));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// load listevent link when finish load listeventregister
			if(Utils.isConnectNetwork(EventLink.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncLoadListLinkEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new asyncLoadListLinkEvent().execute();
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
					boolean ismember = false;
					for(String idevent : listeventregistred) {
						String _idevent = jso1.getString("idevent");
						if(_idevent.equals(idevent)) {
							ismember = true;
							break;
						}
					}
					if(ismember)
						listEvent.add(new EventObject(jso1.getString("title"), jso1.getString("name"), jso1.getString("place"),
								jso1.getString("time"), jso1.getString("description"), jso1.getString("idevent"), "0"));
				}
				for(int j = 0; j < jsa_nextpoint.length(); j++) {
					JSONObject jso2 = jsa_nextpoint.getJSONObject(j);
					boolean ismember2 = false;
					for(String idevent : listeventregistred) {
						String __idevent = jso2.getString("idevent");
						if(__idevent.equals(idevent)) {
							ismember2 = true;
							break;
						}
					}
					if(ismember2)
						listEvent.add(new EventObject(jso2.getString("title"), jso2.getString("name"), jso2.getString("place"),
								jso2.getString("time"), jso2.getString("description"), jso2.getString("idevent"), "0"));
				}
				
				adapter = new StableArrayAdapter(EventLink.this, listEvent);
				listView.setCheeseList(listEvent);
		        listView.setAdapter(adapter);
		        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		        if(Const.iduser.equals(idusercreate)) {
		        	listView.isDraggable = true;
		        } else {
		        	imgbtn_add.setVisibility(View.INVISIBLE);
		        }
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
	
	private class asyncCheckOverTime extends AsyncTask<String, Void, Boolean> {

		int index = -1;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			index = Integer.parseInt(arg0[0]);
			return getTimeDuration(index);
		}
		
		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result) {
				long deltaTime = Utils.deltaTime(listEvent.get(index).time, listEvent.get(index + 1).time);
				if(timeDuration > deltaTime) {
					AlertDialog dialog = new AlertDialog.Builder(EventLink.this)
					.setTitle("Over Time")
					.setMessage("Duration between point " +listEvent.get(index).title+ " and point " +listEvent.get(index + 1).title+ " is overtime?\n" +
							"You sure want save it?")
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
						}
					})
					.create();
					dialog.show();
					return;
				}
				
				if(index >= (listEvent.size()) - 2) {
					if(Utils.isConnectNetwork(EventLink.this)) {
						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new asyncUpdateLinkPoint().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							new asyncUpdateLinkPoint().execute();
						}
					}
				} else {
					String __index = String.valueOf(index + 1);
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new asyncCheckOverTime().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, __index);
					} else {
						new asyncCheckOverTime().execute(__index);
					}
				}
			} else {
				Toast.makeText(getApplicationContext(), "Save fail. Try again", Toast.LENGTH_SHORT)	.show();
			}
		}
		
	}
}
