package dhbk.meetup.mobile.login;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.EventHomePage;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class Register extends Activity implements OnClickListener{

	public static final String USER_REGISTER = "createuser";
	
	private HttpConnect conn;
	
	private EditText ed_name, ed_pass, ed_confirmpass;
	private TextView tv_notify;
	private Spinner sp_gender;
	
	private DialogWaiting dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		conn = new HttpConnect();
		dialog = new DialogWaiting(this);
		
		ed_name = (EditText) findViewById(R.id.register_ed_username);
		ed_pass = (EditText) findViewById(R.id.register_ed_pass);
		ed_confirmpass = (EditText) findViewById(R.id.register_ed_passconfirm);
		tv_notify = (TextView) findViewById(R.id.register_tv_notify);
		sp_gender = (Spinner) findViewById(R.id.register_sp_gender);
		Button btn_register = (Button) findViewById(R.id.register_btn_register);
		btn_register.setOnClickListener(this);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.register_btn_register :
			if(Utils.isConnectNetwork(Register.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncRegister().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new asyncRegister().execute() ;
				}
			} else {
				Toast.makeText(getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
			}
			break;
		default : break;
		}
	}
	
	public String registering () {
		String url = Const.DOMAIN_NAME + USER_REGISTER;
		
		String name = ed_name.getText().toString();
		String pass = ed_pass.getText().toString();
		String confirmpass = ed_confirmpass.getText().toString();
		String gender = String.valueOf(sp_gender.getSelectedItemPosition());
		
		if(name.equals("") || pass.equals("") || confirmpass.equals("")) {
			return "false3";
		} else if (!pass.equals(confirmpass)){
			return  "checkpass";
		} else {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"name", name});
			values.add(new String[] {"password", pass});
			values.add(new String[] {"gender", gender});
			try {
				System.out.println("SEND");
				HttpResponse response = conn.sendRequestPost(url, null, values);
				System.out.println("RECEIVE");
				String result = EntityUtils.toString(response.getEntity());
				System.out.println("RES : " + result);
				return result;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false1";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false2";
			} finally {
				dialog.closeProgressDialog();
			}
			
		}
	}
	
	private class asyncRegister extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return registering();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			System.out.println(result);
			if(result.equals("exist")) {
				Toast.makeText(getApplicationContext(), "User is exist", Toast.LENGTH_SHORT).show();
			} else if(result.equals("checkpass")) {
				tv_notify.setText("check password again");
			} else {
				try {
					int res = Integer.parseInt(result);
					Const.iduser = result;
					Const.username = ed_name.getText().toString();
					Const.password = ed_pass.getText().toString();
					
					Intent it = new Intent(getApplicationContext(), EventHomePage.class);
					startActivity(it);
					finish();
				} catch (Exception e) {
					tv_notify.setText("try again");
				}
			}
			dialog.closeProgressDialog();
		}
	}

	
}
