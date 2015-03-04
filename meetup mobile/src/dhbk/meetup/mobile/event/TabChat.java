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

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class TabChat extends Fragment implements OnClickListener{

	public static final String EVENT_POSTCHAT = "postchat";
	public static final String EVENT_GETCHAT = "getchat";
	
	public static final long TIME_UPDATE_MESSAGE = 5000;
	public static final long TIME_RECONNECT_UPDATE_MESSAGE = 60000;
	
	private String idevent;
	private String lastId = "-1";
	private TextView tv_message;
	private EditText ed_message;
	
	private String status = "start";
	private ArrayList<String[]> listMessage = new ArrayList<String[]>();
	
	private HttpConnect connGet, connPost;
	private DialogWaiting dialog;
	private AtomicBoolean isFirst = new AtomicBoolean(true);
	
	public Handler handler;
	public Runnable updateMessage = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Utils.isConnectNetwork(getActivity())) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncGetChat().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, status);
				} else {
					new asyncGetChat().execute(status) ;
				}
			} else {
				if(isFirst.get())
					handler.postDelayed(updateMessage, TIME_UPDATE_MESSAGE);
				else
					handler.postDelayed(updateMessage, TIME_RECONNECT_UPDATE_MESSAGE);
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		connGet = new HttpConnect();
		connPost = new HttpConnect();
		dialog = new DialogWaiting(getActivity());
		handler = new Handler();
//		// load message
//		if(Utils.isConnectNetwork(getActivity())) {
//			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				new asyncGetChat().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "start");
//			} else {
//				new asyncGetChat().execute("start") ;
//			}
//		}
		
		
		System.out.println("ONCREATE TABCHAT");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.tab_chat, container, false);
		ImageButton imgbtn_send = (ImageButton) v.findViewById(R.id.tabchat_imgbtn_send);
		tv_message = (TextView) v.findViewById(R.id.tabchat_tv_message);
		ed_message = (EditText) v.findViewById(R.id.tabchat_ed_message);
		imgbtn_send.setOnClickListener(this);
		
		// load message
		for(String[] str : listMessage) {
			String message = str[0] + " : " + str[1] + "\n";
			SpannableString span = new SpannableString(message);
			span.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, str[0].length(), 0);
			tv_message.append(span);
		}

		handler.post(updateMessage);
		System.out.println("ONCREATEVIEW TABCHAT");
		return v;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("ONPAUSE TABCHAT");
		handler.removeCallbacks(updateMessage);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		System.out.println("ONDESTROY TABCHAT");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tabchat_imgbtn_send :
			if(ed_message.getText().toString().equals("")) {
				
			} else {
				if(Utils.isConnectNetwork(getActivity())) {
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new asyncPostChat().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						new asyncPostChat().execute() ;
					}
				} else {
					Toast.makeText(getActivity().getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		default : break;
		}
	}

	public void setIdevent(String idevent) {
		this.idevent = idevent;
		System.out.println("SETID TABCHAT");
	}
	
	public String postChat () {
		String url = Const.DOMAIN_NAME + EVENT_POSTCHAT;
		HttpResponse response = null;
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"iduser", Const.iduser});
			values.add(new String[] {"idevent", idevent});
			values.add(new String[] {"content", ed_message.getText().toString()});
			response = connPost.sendRequestPost(url, null, values);
			
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("RES POST CHAT : " + result);
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
	
	public String getChat (String type) {
		String url = Const.DOMAIN_NAME + EVENT_GETCHAT;
		HttpResponse response = null;
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"iduser", Const.iduser});
			values.add(new String[] {"idevent", idevent});
			values.add(new String[] {"lastid", lastId});
			values.add(new String[] {"type", type});
			response = connGet.sendRequestGet(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			System.out.println("RESULT CHAT : " + result);
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
			if(isFirst.get())
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
	
	private class asyncPostChat extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
//			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return postChat();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
//			dialog.closeProgressDialog();
			System.out.println("POST CHAT : " + result);
			if(result.equals("true")) {
				// set textview
				String message = Const.username + " : " + ed_message.getText().toString() + "\n";
				SpannableString span = new SpannableString(message);
				span.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, Const.username.length(), 0);
				tv_message.append(span);
				listMessage.add(new String[] {Const.username,ed_message.getText().toString()});
				ed_message.setText("");
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Try Again", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class asyncGetChat extends AsyncTask<String, Void, String> {

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
			return getChat(params[0]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(isFirst.get())
				dialog.closeProgressDialog();
			try {
				JSONObject jso_parent = new JSONObject(result);
				JSONArray jsa_listchat = jso_parent.getJSONArray("listchat");
				for(int i = 0; i < jsa_listchat.length(); i++) {
					JSONObject jso = jsa_listchat.getJSONObject(i);
					String user = jso.getString("name").toString();
					String content = jso.getString("content").toString();
					lastId = jso.getString("idgroupchat").toString();
				
					String message = user + " : " + content + "\n";
					SpannableString span = new SpannableString(message);
					span.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, user.length(), 0);
					tv_message.append(span);
					listMessage.add(new String[] {user, content});
					
				}
				if(isFirst.get()) {
					isFirst.set(false);
					status = "live";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handler.postDelayed(updateMessage, TIME_UPDATE_MESSAGE);
		}
		
	}
}
