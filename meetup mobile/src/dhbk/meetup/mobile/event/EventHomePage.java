package dhbk.meetup.mobile.event;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.object.EventObject;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class EventHomePage extends Activity implements OnClickListener{

	public static final int TIME_UPDATE = 5000;
	
	private HttpConnect conn;
	private ListView lv_event;
	private ListEventAdapter listeventAdapter;
//	private ArrayList<String> title, own, place, time, content;
	private ArrayList<EventObject> listevent = new ArrayList<EventObject>();
	
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
	
//	private DialogWaiting dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventhomepage);
		
		handler_updateEvent = new Handler();
		handler_updateEvent.post(update);
		
//		title = new ArrayList<String>();
//		own = new ArrayList<String>();
//		place = new ArrayList<String>();
//		time = new ArrayList<String>();
//		content = new ArrayList<String>();
		
		listeventAdapter = new ListEventAdapter(this, listevent);
		
		conn = new HttpConnect();
//		dialog = new DialogWaiting(this);
		
		lv_event = (ListView) findViewById(R.id.homepage_lv_event);
		lv_event.setAdapter(listeventAdapter);
		ImageButton imgbtn_createevent = (ImageButton) findViewById(R.id.homepage_imgbtn_createevent);
		imgbtn_createevent.setOnClickListener(this);
		ImageButton imgbtn_filter = (ImageButton) findViewById(R.id.homepage_imgbtn_filter);
		imgbtn_filter.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.homepage_imgbtn_createevent :
			Intent it = new Intent(getApplicationContext(), CreateEvent.class);
			startActivity(it);
			break;
		case R.id.homepage_imgbtn_filter :
			
			break;
		default : break; 
		}
	}
	
	public void updateEvent() {
		
	}
	
	public void changeDataIfNeed () {
		
	}
	
	private class asyncUpdateEvent extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			updateEvent();
			return null;
		}
		
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			changeDataIfNeed();
			handler_updateEvent.postDelayed(update, TIME_UPDATE);
		};
	}
}
