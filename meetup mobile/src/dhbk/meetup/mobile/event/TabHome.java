package dhbk.meetup.mobile.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class TabHome extends Fragment implements OnClickListener{

	public static final String EVENT_INFO = "infoevent";
	public static final int REQUESTCODE_EDITEVENT = 10;
	
	private HttpConnect conn;
	private DialogWaiting dialog;
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	
	TextView tv_title, tv_time, tv_place, tv_own, tv_content;
	boolean ispublic;
	private String idevent = "0", nextpoint, iduser;
	boolean ismember = false;
	private String idusercreate;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		conn = new HttpConnect();
		dialog = new DialogWaiting(getActivity());
		System.out.println("ONCREATE TABHOME");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.tab_home, container, false);
		System.out.println("ONCREATEVIEW TABHOME");
		tv_title = (TextView) v.findViewById(R.id.tabhome_tv_title);
		tv_own = (TextView) v.findViewById(R.id.tabhome_tv_own);
		tv_time = (TextView) v.findViewById(R.id.tabhome_tv_time);
		tv_place = (TextView) v.findViewById(R.id.tabhome_tv_place);
		tv_content = (TextView) v.findViewById(R.id.tabhome_tv_content);
		
		ImageButton imgbtn_placeevent = (ImageButton) v.findViewById(R.id.tabhome_imgbtn_place);
		imgbtn_placeevent.setOnClickListener(this);
		
		ImageButton imgbtn_nextpoint = (ImageButton) v.findViewById(R.id.tabhome_imgbtn_nextpoint);
		ImageButton imgbtn_edit = (ImageButton) v.findViewById(R.id.tabhome_imgbtn_edit);
		ImageButton imgbtn_join = (ImageButton) v.findViewById(R.id.tabhome_imgbtn_join);
		
		if(idusercreate.equals(Const.iduser)) {
			imgbtn_nextpoint.setOnClickListener(this);
			imgbtn_edit.setOnClickListener(this);
			imgbtn_join.setVisibility(View.GONE);
		} else if(ismember) {
			imgbtn_nextpoint.setVisibility(View.GONE);
			imgbtn_edit.setVisibility(View.GONE);
			imgbtn_join.setVisibility(View.GONE);
		} else {
			imgbtn_nextpoint.setVisibility(View.GONE);
			imgbtn_edit.setVisibility(View.GONE);
			imgbtn_join.setOnClickListener(this);
		}
		
		if(Utils.isConnectNetwork(getActivity())) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncLoadEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new asyncLoadEvent().execute() ;
			}
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
		}
		return v;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("ONPAUSE TABHOME");
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("ONDESTROY TABHOME");
	}	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUESTCODE_EDITEVENT) {
			if(resultCode == getActivity().RESULT_OK) {
				if(Utils.isConnectNetwork(getActivity())) {
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new asyncLoadEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						new asyncLoadEvent().execute() ;
					}
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tabhome_imgbtn_join :
			if(Utils.isConnectNetwork(getActivity())) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncJoinNow().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new asyncJoinNow().execute();
				}
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Network not connected", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tabhome_imgbtn_place :
			
			break;
		case R.id.tabhome_imgbtn_nextpoint :
			
			break;
		case R.id.tabhome_imgbtn_edit :
			Intent it = new Intent(getActivity().getApplicationContext(), EditEvent.class);
			it.putExtra("title", tv_title.getText());
//			it.putExtra("own", tv_own.getText());
			it.putExtra("time", tv_time.getText());
			it.putExtra("content", tv_content.getText());
			it.putExtra("place", tv_place.getText());
			it.putExtra("idevent", idevent);
			it.putExtra("ispublic", ispublic);
			it.putExtra("nextpoint", nextpoint);
			it.putExtra("iduser", iduser);
			startActivityForResult(it, REQUESTCODE_EDITEVENT);
			break;
		default : break;
		}
	}

	public void setIdevent(String idevent) {
		this.idevent = idevent;
		System.out.println("SET IDEVENT");
	}
	
	public void setIsmember(boolean ismember) {
		this.ismember = ismember;
	}
	
	public void setIdusercreate(String idusercreate) {
		this.idusercreate = idusercreate;
	}
	
	public String loadInfoEvent () {
		String url = Const.DOMAIN_NAME + EVENT_INFO;
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"idevent", idevent});
			HttpResponse response = conn.sendRequestGet(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("RESULT : " + result);
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "false 2";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "false 3";
		} finally {
			dialog.closeProgressDialog();
			isFirst.set(false);
		}
	}
	
	public String joinNow () {
		String url = Const.DOMAIN_NAME + EventHomePage.EVENT_ADDMEMBER;
		HttpResponse response = null;
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"iduser", Const.iduser});
			values.add(new String[] {"idevent", idevent});
//			values.add(new String[] {"idinvite", listinvite.get(position).idinvite});
			response = conn.sendRequestPost(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("RESULT JOIN NOW INVITE : " + result);
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE JOIN NOW PROTOCOL";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FALSE JOIN NOW IO";
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
	
	private class asyncLoadEvent extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(isFirst.get())
				dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return loadInfoEvent();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(isFirst.get()){
				dialog.closeProgressDialog();
				isFirst.set(false);
			}
			try {
				JSONObject jso_parent = new JSONObject(result);
				JSONArray jsa_info = jso_parent.getJSONArray("infoevent");
				for(int i = 0; i < jsa_info.length(); i++) {
					JSONObject jso = jsa_info.getJSONObject(i);
					tv_title.setText(jso.getString("title").toString());
					tv_content.setText(jso.getString("description").toString());
					tv_own.setText(jso.getString("name").toString());
					tv_place.setText(jso.getString("place").toString());
					tv_time.setText(jso.getString("time").toString());
					String ispublic_str = jso.getString("ispublic");
					if(ispublic_str.equals("1"))
						ispublic = true;
					else ispublic = false;
					nextpoint = jso.getString("nextpoint").toString();
					iduser = jso.getString("iduser").toString();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getActivity().getApplicationContext(), "Load fail", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
private class asyncJoinNow extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return joinNow();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result.equals("true")) {
				Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
				Intent it = new Intent();
				it.putExtra("idevent", idevent);
				it.putExtra("idusercreate", idusercreate);
				getActivity().setResult(EventHomePage.RESULT_OK, it);
				getActivity().finish();
//				Intent it = new Intent(getActivity().getApplicationContext(), AEvent.class);
//				it.putExtra("idevent", idevent);
//				it.putExtra("ismember", true);
//				it.putExtra("iduser", "none");
//				it.putExtra("idusercreate", idusercreate);
//				startActivity(it);
//				getActivity().finish();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Try Again !", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}
