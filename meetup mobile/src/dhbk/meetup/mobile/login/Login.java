package dhbk.meetup.mobile.login;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.EventHomePage;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class Login extends Activity implements OnClickListener{

	public static final String USER_LOGIN = "login"; 
	
	private HttpConnect conn;
	private TextView tv_notify;
	private EditText ed_username, ed_password;
	private DialogWaiting dialog;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		conn = new HttpConnect();
		dialog = new DialogWaiting(this);
		
		tv_notify = (TextView) findViewById(R.id.login_tv_notify);
		ed_username = (EditText) findViewById(R.id.login_ed_username);
		ed_password = (EditText) findViewById(R.id.login_ed_pass);
		
		Button btn_login = (Button) findViewById(R.id.login_btn_login);
		Button btn_signup = (Button) findViewById(R.id.login_btn_signup);
		btn_login.setOnClickListener(this);
		btn_signup.setOnClickListener(this);
		
		SharedPreferences prefs = getSharedPreferences(Const.PREFERENCE_MEETUP_MOBILE, Context.MODE_PRIVATE);
		String username = prefs.getString("username", "");
		String password = prefs.getString("password", "");
		if(!(username.equals("") || password.equals(""))) {
			if(Utils.isConnectNetwork(Login.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncLogin().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username, password);
				} else {
					new asyncLogin().execute(username, password) ;
				}
			}
		} else {
			System.out.println("EMPTY * EMPTY");
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.login_btn_login :
			if(Utils.isConnectNetwork(Login.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncLogin().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ed_username.getText().toString(), ed_password.getText().toString());
				} else {
					new asyncLogin().execute(ed_username.getText().toString(), ed_password.getText().toString()) ;
				}
			} else {
				Toast.makeText(getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.login_btn_signup :
			Intent it = new Intent(getApplicationContext(), Register.class);
			startActivity(it);
			finish();
			break;
		default : break;
		}
	}
	
	private boolean authenticLogin (String username, String password) {
		String url = Const.DOMAIN_NAME + USER_LOGIN;
		try {
			if(username.equals("") || password.equals(""))
				return false;
			else {
				ArrayList<String[]> values = new ArrayList<String[]>();
				values.add(new String[] {"name", username});
				values.add(new String[] {"password", password});
				HttpResponse response = conn.sendRequestPost(url, null, values);
				String result = EntityUtils.toString(response.getEntity());
				System.out.println("RESULT LOGIN : " + result);
				try {
					int res = Integer.parseInt(result);
					Const.iduser = result;
					Const.username = username;
					Const.password = password;
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			dialog.closeProgressDialog();
		}
	}
	
	private class asyncLogin extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return authenticLogin(params[0], params[1]);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result) {
				Intent it = new Intent(getApplicationContext(), EventHomePage.class);
				startActivity(it);
				finish();
			} else {
				tv_notify.setText("Try Again !");
			}
		}
	}
}
