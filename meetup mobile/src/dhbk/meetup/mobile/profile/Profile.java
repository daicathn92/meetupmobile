package dhbk.meetup.mobile.profile;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class Profile extends Activity implements OnClickListener{

	public static final String PROFILE_INFO = "infoprofile";
	public static final int REQUESTCODE_EDITPROFILE = 100;
	
	private HttpConnect conn;
	private DialogWaiting dialog;
	
	private TextView tv_name, tv_gender;
	private int gender = -1;
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		conn = new HttpConnect();
		dialog = new DialogWaiting(this);
		
		tv_name = (TextView)findViewById(R.id.profile_tv_username);
		tv_gender = (TextView)findViewById(R.id.profile_tv_gender);
		ImageButton imgbtn_edit = (ImageButton) findViewById(R.id.profile_imgbtn_edit);
		imgbtn_edit.setOnClickListener(this);
		
		if(Utils.isConnectNetwork(Profile.this)) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncGetInfoprofile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new asyncGetInfoprofile().execute();
			}
		} 
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUESTCODE_EDITPROFILE) {
			if(resultCode == RESULT_OK) {
				gender = data.getExtras().getInt("gender");
				if(gender == 0) tv_gender.setText("Male");
				else			tv_gender.setText("Female");
				tv_name.setText(Const.username);
				
				// save username, password
				SharedPreferences prefs = getSharedPreferences(Const.PREFERENCE_MEETUP_MOBILE, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("username", Const.username);
				editor.putString("password", Const.password);
				editor.commit();
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.profile_imgbtn_edit :
			Intent it = new Intent(getApplicationContext(), EditProfile.class);
			it.putExtra("gender", gender);
			startActivityForResult(it, REQUESTCODE_EDITPROFILE);
			break;
		default : break;
		}
	}
	
	private String getInfoprofile() {
		String url = Const.DOMAIN_NAME + PROFILE_INFO;
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return null;
	}
	
	private class asyncGetInfoprofile extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return getInfoprofile();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result != null) {
				try {
					JSONObject jso = new JSONObject(result);
					String name = jso.getString("name");
					String _gender = jso.getString("gender");
					gender = Integer.parseInt(_gender);
					tv_name.setText(name);
					if(gender == 0) tv_gender.setText("Male");
					else 			tv_gender.setText("Female");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Load information fail", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
