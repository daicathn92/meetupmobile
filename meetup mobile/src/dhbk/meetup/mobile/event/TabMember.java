package dhbk.meetup.mobile.event;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.adapter.ListMemberAdapter;
import dhbk.meetup.mobile.event.adapter.ListNotMemberAdapter;
import dhbk.meetup.mobile.event.googlemap.PlaceEvent;
import dhbk.meetup.mobile.event.googlemap.TrackGPS;
import dhbk.meetup.mobile.event.member.InviteMember;
import dhbk.meetup.mobile.event.object.MemberObject;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class TabMember extends Fragment implements OnClickListener{

	public static final int REQUESTCODE_SETTINGS_LOCATION = 100;
	public static final String EVENT_SENDNOTIFY = "sendnotify";
	public static final String EVENT_LISTMEMBER = "listmember";
	public static final String EVENT_SENDINVITE = "sendinvite";
	
	private String idevent;
	private HttpConnect conn;
	private DialogWaiting dialog;
	
	private ListMemberAdapter listMemberAdapter;
	public	ListNotMemberAdapter listnotmemberAdapter;
	private ArrayList<MemberObject> listmember = new ArrayList<MemberObject>();
	public ArrayList<MemberObject> listnotmember = new ArrayList<MemberObject>();
	private InviteMember inviteMemberDialog;
	
	ListView lv_member;
	
	public LocationManager locationManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("ONCREATE TABMEMBER");
		conn = new HttpConnect();
		dialog = new DialogWaiting(getActivity());
		locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
		
		listMemberAdapter = new ListMemberAdapter(TabMember.this, listmember);
		listnotmemberAdapter = new ListNotMemberAdapter(this, listnotmember);
		inviteMemberDialog = new InviteMember(this);
	}
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.tab_member, container, false);
		
		lv_member = (ListView) v.findViewById(R.id.tabmember_lv_member);
		lv_member.setAdapter(listMemberAdapter);
		
		ImageButton imgbtn_invite = (ImageButton) v.findViewById(R.id.tabmember_imgbtn_invite);
		ImageButton imgbtn_track = (ImageButton) v.findViewById(R.id.tabmember_imgbtn_track);		
		imgbtn_invite.setOnClickListener(this);
		imgbtn_track.setOnClickListener(this);
		
		if(Utils.isConnectNetwork(getActivity())) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncListmember().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "member");
			} else {
				new asyncListmember().execute("member") ;
			}
		} 
		
		System.out.println("ONCREATEVIEW TABMEMBER");
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		// result from setting location
		if(requestCode == REQUESTCODE_SETTINGS_LOCATION) {
			if(resultCode == getActivity().RESULT_OK) {
				if(Utils.isGPSEnable(locationManager)) {
					Intent it = new Intent(getActivity().getApplicationContext(), TrackGPS.class);
					it.putExtra("lat", ((AEvent)getActivity()).th.lat);
					it.putExtra("lng", ((AEvent)getActivity()).th.lng);
					it.putExtra("place", ((AEvent)getActivity()).th.tv_place.getText().toString());
					it.putExtra("listid", listIduserWithJson());
					startActivity(it);
				}
			}
		}
	}
	
	@Override
	public void onPause() {
	// TODO Auto-generated method stub
		super.onPause();
		System.out.println("ONPAUSE TABMEMBER");
	}
	
	@Override
	public void onDestroy() {
	// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("ONDESTROY TABMEMBER");
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tabmember_imgbtn_invite :
			if(Utils.isConnectNetwork(getActivity())) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncListmember().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "notmember");
				} else {
					new asyncListmember().execute("notmember") ;
				}
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tabmember_imgbtn_track :
			if(Utils.isGPSEnable(locationManager)) {
				Intent it = new Intent(getActivity().getApplicationContext(), TrackGPS.class);
				it.putExtra("lat", ((AEvent)getActivity()).th.lat);
				it.putExtra("lng", ((AEvent)getActivity()).th.lng);
				it.putExtra("place", ((AEvent)getActivity()).th.tv_place.getText().toString());
				it.putExtra("listid", listIduserWithJson());
				startActivity(it);
			} else {
				new AlertDialog.Builder(getActivity())
    			.setTitle("GPS")
    			.setMessage("Enable GPS")
    			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent itGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				        startActivityForResult(itGPS, REQUESTCODE_SETTINGS_LOCATION);
					}
    			})
    			.show();
			}
			break;
		default : break;
		}
	}
	
	public void setIdevent(String idevent) {
		this.idevent = idevent;
	}
	
	@SuppressLint("NewApi")
	public void sendNotify (String idmember, String position) {
		if(Utils.isConnectNetwork(getActivity())) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncSendNotify().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, idmember, position);
			} else {
				new asyncSendNotify().execute(idmember, position) ;
			}
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
		}
	}

	public String sendNotifyInBackgroud (String idmember) {
		String url = Const.DOMAIN_NAME + EVENT_SENDNOTIFY;
		HttpResponse response = null;
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"idusernotify", Const.iduser});
			values.add(new String[] {"idmember", idmember});
			response = conn.sendRequestPost(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("SEND NOTIFY RESULT : " + result);
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE IO";
		} finally {
			dialog.closeProgressDialog();
			if(response != null)
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	@SuppressLint("NewApi")
	public void inviteMember(String iduser, String position) {
		if(Utils.isConnectNetwork(getActivity())) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncInviteMember().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, iduser, position);
			} else {
				new asyncInviteMember().execute(iduser, position) ;
			}
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
		}
	}
	
	public String inviteMemberInBackground (String iduser) {
		// iduser : duoc moi
		// iduserinvite : moi
		String url = Const.DOMAIN_NAME + EVENT_SENDINVITE;
		HttpResponse response = null;
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"idevent", idevent});
			values.add(new String[] {"iduser", iduser});
			values.add(new String[] {"iduserinvite", Const.iduser});
			response = conn.sendRequestPost(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("INVITE RESULT : " + result);
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE IO";
		} finally {
			dialog.closeProgressDialog();
			if(response != null)
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	public String listMember (String type) {
		String url = Const.DOMAIN_NAME + EVENT_LISTMEMBER;
		HttpResponse response = null;
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"idevent", idevent});
			values.add(new String[] {"type", type});
			response = conn.sendRequestGet(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("LISTMEMBER RESULT : " + result);
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE IO";
		} finally {
			dialog.closeProgressDialog();
			if(response != null)
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	// get list iduser format Json
	public String listIduserWithJson () {
		JSONObject jso = new JSONObject();
		JSONArray jsa = new JSONArray();
		for(MemberObject mem: listmember) {
			jsa.put(mem.iduser);
		}
		try {
			jso.put("listid", jsa);
		} catch (JSONException e) { }
		return jso.toString();
	}
	
	public void changeDataListMemberIfNeed (String result, boolean ismember) {
		try {
			JSONObject jso_parent = new JSONObject(result);
			JSONArray jsa_listmember = jso_parent.getJSONArray("listmember");
			if(ismember) {
				listmember.clear();
				for(int i = 0; i < jsa_listmember.length(); i++) {
					JSONObject jso = jsa_listmember.getJSONObject(i);
					listmember.add(new MemberObject(jso.getString("idmember"), jso.getString("iduser"), jso.getString("name")));
				}
				listMemberAdapter.notifyDataSetChanged();
			} else {
				listnotmember.clear();
				for(int i = 0; i < jsa_listmember.length(); i++) {
					JSONObject jso = jsa_listmember.getJSONObject(i);
					listnotmember.add(new MemberObject("0", jso.getString("iduser"), jso.getString("name")));
				}
				listnotmemberAdapter.notifyDataSetChanged();
				inviteMemberDialog.dialog.show();
//				new InviteMember(this);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class asyncSendNotify extends AsyncTask<String, Void, String> {

		String pos = "";
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			pos = params[1];
			return sendNotifyInBackgroud(params[0]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result.equals("true")) {
				Toast.makeText(getActivity().getApplicationContext(), "Send Success", Toast.LENGTH_SHORT).show();
				try {
					int position = Integer.parseInt(pos);
					listmember.get(position).isVisible = false;
//					listMemberAdapter.getView(position, null, null);
//					lv_member.setAdapter(listMemberAdapter);
					listMemberAdapter.notifyDataSetChanged();
//					lv_member.refreshDrawableState();
				} catch (Exception e){}
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Try Again !", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class asyncListmember extends AsyncTask<String, Void, String> {

		private boolean ismember = false;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(params[0].equals("member"))
				ismember = true;
			return listMember(params[0]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			changeDataListMemberIfNeed(result, ismember);
		}
	}

	private class asyncInviteMember extends AsyncTask<String, Void, String> {

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
			pos = params[1];
			return inviteMemberInBackground(params[0]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result.equals("true")) {
				Toast.makeText(getActivity().getApplicationContext(), "Send Success", Toast.LENGTH_SHORT).show();
				try {
					int position = Integer.parseInt(pos);
					listnotmember.get(position).isVisible = false;
//					listMemberAdapter.getView(position, null, null);
//					inviteMemberDialog.lv_invite.setAdapter(listMemberAdapter);
					listnotmemberAdapter.notifyDataSetChanged();
//					lv_member.refreshDrawableState();
				} catch (Exception e){}
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Try Again !", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	
}
