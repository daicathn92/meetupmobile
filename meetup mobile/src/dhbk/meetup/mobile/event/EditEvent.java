package dhbk.meetup.mobile.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class EditEvent extends Activity implements OnClickListener, OnDateSetListener, OnTimeSetListener{

	public static final int REQUESTCODE_PLACE = 1;
	public static final String EVENT_EDITEVENT = "editevent";
	
	private HttpConnect conn;
	private DialogWaiting dialog;
	
	private EditText ed_title, ed_content;
	private TextView tv_place, tv_date, tv_time;
	private CheckBox cb_public;
	private String idevent, iduser, nextpoint;
	private boolean ispublic;
	
	private DatePickerDialog datePicker;
	private TimePickerDialog timePicker;
	private Calendar calendar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createevent);
		
		conn = new HttpConnect();
		dialog = new DialogWaiting(EditEvent.this);
		
		calendar = Calendar.getInstance();
		
		datePicker = new DatePickerDialog(EditEvent.this, EditEvent.this, calendar.get(Calendar.YEAR)
				, calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
		timePicker = new TimePickerDialog(EditEvent.this, EditEvent.this, calendar.get(Calendar.HOUR_OF_DAY), 
				calendar.get(Calendar.MINUTE), true);
		
		Button btn_create = (Button) findViewById(R.id.create_btn_ok);
		ImageButton imgbtn_place = (ImageButton) findViewById(R.id.create_imgbtn_place);
		ImageButton imgbtn_date = (ImageButton) findViewById(R.id.create_imgbtn_date);
		ImageButton imgbtn_time = (ImageButton) findViewById(R.id.create_imgbtn_time);
		btn_create.setOnClickListener(this);
		imgbtn_place.setOnClickListener(this);
		imgbtn_date.setOnClickListener(this);
		imgbtn_time.setOnClickListener(this);
		btn_create.setText("Apply");
		
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
		idevent = it.getExtras().getString("idevent");
		iduser = it.getExtras().getString("iduser");
		nextpoint = it.getExtras().getString("nextpoint");
		ispublic = it.getExtras().getBoolean("ispublic");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUESTCODE_PLACE) {
			if(resultCode == RESULT_OK) {
				String place = data.getExtras().getString("place");
				tv_place.setText(place);
			}
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.create_btn_ok : 
			if(Utils.isConnectNetwork(EditEvent.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncEditEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new asyncEditEvent().execute() ;
				}
			} else {
				Toast.makeText(getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.create_imgbtn_place :
			Intent it = new Intent(getApplicationContext(), PlaceEvent.class);
			startActivityForResult(it, REQUESTCODE_PLACE);
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
	
	public String editEvent () {
		String title = ed_title.getText().toString();
		String content = ed_content.getText().toString();
		String place = tv_place.getText().toString();
		String date = tv_date.getText().toString();
		String time = tv_time.getText().toString();
		String isPublic = String.valueOf(cb_public.isChecked()?1:0);
		
		if(title.equals("") || content.equals("") || place.equals("") || date.equals("") || time.equals("")) {
			return "not fill";
		} else {
			String url = Const.DOMAIN_NAME + EVENT_EDITEVENT;
			
			try {
				ArrayList<String[]> values = new ArrayList<String[]>();
				values.add(new String[] {"title", title});
				values.add(new String[] {"place", place});
				values.add(new String[] {"description", content});
				values.add(new String[] {"ispublic", isPublic});
				values.add(new String[] {"iduser", Const.iduser});
				values.add(new String[] {"time", date + " " + time});
				values.add(new String[] {"idevent", idevent});
				values.add(new String[] {"nextpoint", nextpoint});
				HttpResponse response = conn.sendRequestPost(url, null, values);
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
			}
		}
	}
	
	private class asyncEditEvent extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return editEvent();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result.equals("not fill")){
				Toast.makeText(getApplicationContext(), "Fill All", Toast.LENGTH_SHORT).show();
			} else if(result.equals("true")){
				Intent it = new Intent();
				setResult(RESULT_OK, it);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "Try again !", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
