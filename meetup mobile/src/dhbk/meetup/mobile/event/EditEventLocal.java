package dhbk.meetup.mobile.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.googlemap.PlaceEvent;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class EditEventLocal extends Activity implements OnClickListener, OnDateSetListener, OnTimeSetListener{

	public static final int REQUESTCODE_PLACEEVENT = 1;
	public static final int REQUESTCODE_SETTINGS_LOCATION = 10;
	
	private EditText ed_title, ed_content;
	private TextView tv_place, tv_date, tv_time;
	private CheckBox cb_public;
	private String idevent_local;
//	private boolean ispublic;
	
	private DatePickerDialog datePicker;
	private TimePickerDialog timePicker;
	private Calendar calendar = null;
	
	public LocationManager locationManager;
	public double lat, lng;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createevent);
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		calendar = Calendar.getInstance();
		
		datePicker = new DatePickerDialog(EditEventLocal.this, EditEventLocal.this, calendar.get(Calendar.YEAR)
				, calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
		timePicker = new TimePickerDialog(EditEventLocal.this, EditEventLocal.this, calendar.get(Calendar.HOUR_OF_DAY), 
				calendar.get(Calendar.MINUTE), true);
		
		Button btn_create = (Button) findViewById(R.id.create_btn_ok);
		ImageButton imgbtn_place = (ImageButton) findViewById(R.id.create_imgbtn_place);
		ImageButton imgbtn_date = (ImageButton) findViewById(R.id.create_imgbtn_date);
		ImageButton imgbtn_time = (ImageButton) findViewById(R.id.create_imgbtn_time);
		btn_create.setOnClickListener(this);
		imgbtn_place.setOnClickListener(this);
		imgbtn_date.setOnClickListener(this);
		imgbtn_time.setOnClickListener(this);
		btn_create.setText("Save");
		
		ed_title = (EditText) findViewById(R.id.create_ed_title);
		ed_content = (EditText) findViewById(R.id.create_ed_content);
		tv_place = (TextView) findViewById(R.id.create_tv_place);
		tv_date = (TextView) findViewById(R.id.create_tv_date);
		tv_time = (TextView) findViewById(R.id.create_tv_time);
		cb_public = (CheckBox) findViewById(R.id.create_cb_public);
		
		//
		Intent it = getIntent();
		ed_title.setText(it.getExtras().getString("title"));
		ed_content.setText(it.getExtras().getString("content"));
		String[] time = it.getExtras().getString("time").split(" ");
		tv_date.setText(time[0]);
		tv_time.setText(time[1]);
		tv_place.setText(it.getExtras().getString("place"));
		lat = it.getExtras().getDouble("lat");
		lng = it.getExtras().getDouble("lng");
		idevent_local = it.getExtras().getString("ideventlocal");
		cb_public.setChecked(it.getExtras().getBoolean("ispublic"));
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		// result from setting location
		if(requestCode == REQUESTCODE_SETTINGS_LOCATION) {
			if(resultCode == RESULT_OK) {
				if(Utils.isGPSEnable(locationManager)) {
					Intent it = new Intent(getApplicationContext(), PlaceEvent.class);
					it.putExtra("onlyview", false);
					it.putExtra("lat", lat);
					it.putExtra("lng", lng);
					it.putExtra("place", tv_place.getText().toString());
					startActivityForResult(it, REQUESTCODE_PLACEEVENT);
				}
			}
		}
		
		// result from activity place event
		if(requestCode == REQUESTCODE_PLACEEVENT) {
			if(resultCode == RESULT_OK) {
				try {
					
					lat = data.getExtras().getDouble("lat");
					lng = data.getExtras().getDouble("lng");
					tv_place.setText(data.getExtras().getString("place"));
				} catch (Exception e) {
					System.out.println("RESULT ERROR : " + e.getMessage());
				}
			}
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.create_btn_ok : // save 
			String title = ed_title.getText().toString();
			String content = ed_content.getText().toString();
			String place = tv_place.getText().toString();
			String date = tv_date.getText().toString();
			String time = tv_time.getText().toString();
			String isPublic = String.valueOf(cb_public.isChecked()?1:0);
			if(title.equals("") || content.equals("") || place.equals("") || date.equals("") || time.equals("")) {
				Toast.makeText(getApplicationContext(), "Not fill", Toast.LENGTH_SHORT).show();
			} else {
				Intent it = new Intent();
				it.putExtra("title", title);
				it.putExtra("content", content);
				it.putExtra("place", place + ";" + lat + ";" + lng);
				it.putExtra("time", date + " " + time);
				it.putExtra("ispublic", cb_public.isChecked());
				it.putExtra("ideventlocal", idevent_local);
				setResult(RESULT_OK, it);
				finish();
			}
			break;
		case R.id.create_imgbtn_place :
			if(Utils.isGPSEnable(locationManager)) {
				Intent it = new Intent(getApplicationContext(), PlaceEvent.class);
				it.putExtra("onlyview", false);
				it.putExtra("lat", lat);
				it.putExtra("lng", lng);
				it.putExtra("place", tv_place.getText().toString());
				startActivityForResult(it, REQUESTCODE_PLACEEVENT);
			} else {
				new AlertDialog.Builder(this)
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
		case R.id.create_imgbtn_date :
			showDatePickerDialog();
			break;
		case R.id.create_imgbtn_time :
			showTimePickerDialog();
			break;
		default : break;
		}
	}
	
	@Override
	public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		tv_time.setText(arg1+ ":" +arg2+ ":00");
	}


	@Override
	public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		tv_date.setText(arg1+ "-" +(arg2+1)+ "-" +arg3);
	}
	
	public void showDatePickerDialog () {
		datePicker.updateDate(calendar.get(Calendar.YEAR)
				, calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
		datePicker.show();
	}
	
	public void showTimePickerDialog () {
		timePicker.updateTime(Calendar.HOUR_OF_DAY, calendar.get(Calendar.MINUTE));
		timePicker.show();
	}
	
//	public String editEvent () {
//		String title = ed_title.getText().toString();
//		String content = ed_content.getText().toString();
//		String place = tv_place.getText().toString();
//		String date = tv_date.getText().toString();
//		String time = tv_time.getText().toString();
//		String isPublic = String.valueOf(cb_public.isChecked()?1:0);
//		
//		if(title.equals("") || content.equals("") || place.equals("") || date.equals("") || time.equals("")) {
//			return "not fill";
//		} else {
//			String url = Const.DOMAIN_NAME + EVENT_EDITEVENT;
//			
//			try {
//				ArrayList<String[]> values = new ArrayList<String[]>();
//				values.add(new String[] {"title", title});
//				values.add(new String[] {"place", place + ";" + lat + ";" + lng});
//				values.add(new String[] {"description", content});
//				values.add(new String[] {"ispublic", isPublic});
//				values.add(new String[] {"iduser", Const.iduser});
//				values.add(new String[] {"time", date + " " + time});
//				values.add(new String[] {"idevent", idevent});
//				values.add(new String[] {"nextpoint", nextpoint});
//				HttpResponse response = conn.sendRequestPost(url, null, values);
//				String result = EntityUtils.toString(response.getEntity());
//				System.out.println("RESULT : " + result);
//				return result;
//			} catch (ClientProtocolException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return "false 2";
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return "false 3";
//			} finally {
//				dialog.closeProgressDialog();
//			}
//		}
//	}
//	
//	private class asyncEditEvent extends AsyncTask<String, Void, String> {
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//			dialog.showProgressDialog();
//		}
//		
//		@Override
//		protected String doInBackground(String... params) {
//			// TODO Auto-generated method stub
//			return editEvent();
//		}
//		
//		@Override
//		protected void onPostExecute(String result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//			dialog.closeProgressDialog();
//			if(result.equals("not fill")){
//				Toast.makeText(getApplicationContext(), "Fill All", Toast.LENGTH_SHORT).show();
//			} else if(result.equals("true")){
//				Intent it = new Intent();
//				setResult(RESULT_OK, it);
//				finish();
//			} else {
//				Toast.makeText(getApplicationContext(), "Try again !", Toast.LENGTH_SHORT).show();
//			}
//		}
//	}

}
