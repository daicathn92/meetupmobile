package dhbk.meetup.mobile.profile;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import com.google.android.gms.internal.cu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Utils;

public class EditProfile extends Activity implements OnClickListener{

	public static final String PROFILE_EDIT = "editprofile";
	
	private HttpConnect conn;
	private DialogWaiting dialog;
	
	private EditText ed_name, ed_currpass, ed_newpass, ed_confirmpass;
	private Spinner sp_gender;
	private int gender = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editprofile);
		conn = new HttpConnect();
		dialog = new DialogWaiting(this);
		
		ed_name = (EditText) findViewById(R.id.editPro_ed_name);
		ed_currpass = (EditText) findViewById(R.id.editPro_ed_currpass);
		ed_newpass = (EditText) findViewById(R.id.editPro_ed_newpass);
		ed_confirmpass = (EditText) findViewById(R.id.editPro_ed_confirmpass);
		sp_gender = (Spinner) findViewById(R.id.editPro_sp_gender);
		Button btn_save = (Button) findViewById(R.id.editpro_btn_save);
		btn_save.setOnClickListener(this);
		
		Intent it = getIntent();
		ed_name.setText(Const.username);
		gender = it.getExtras().getInt("gender");
		if(gender >= 0)
			sp_gender.setSelection(gender);
		
	}

	@SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.editpro_btn_save :
			if(Utils.isConnectNetwork(EditProfile.this)) {
				String pass = getPassword();
				String name = ed_name.getText().toString();
				if(name.equals("")) name = Const.username;
				if(pass == null) {
					Toast.makeText(getApplicationContext(), "Check your password", Toast.LENGTH_SHORT).show();
					return;
				}
				if(pass.equals(Const.password) && name.equals(Const.username) && gender == sp_gender.getSelectedItemPosition()) {
					Toast.makeText(getApplicationContext(), "Not field change", Toast.LENGTH_SHORT).show();
					return;
				}
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncEditProfile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name, pass, String.valueOf(sp_gender.getSelectedItemPosition()));
				} else {
					new asyncEditProfile().execute(name, pass, String.valueOf(sp_gender.getSelectedItemPosition())) ;
				}
			} else {
				Toast.makeText(getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
			}
			break;
		default : break;
		}
	}
	
	public String getPassword () {
		String newpass = ed_newpass.getText().toString();
		String currpass = ed_currpass.getText().toString();
		String confirmpass = ed_confirmpass.getText().toString();
		
		if(newpass.equals(""))
			return Const.password;
		else if(!newpass.equals(confirmpass)){
			return null;
		} else if (!currpass.equals(Const.password)) {
			return null;
		}
		return newpass;
	}
	
	public boolean changeProfile (String name, String pass, String gender) {
		String url = Const.DOMAIN_NAME + PROFILE_EDIT;
		HttpResponse response = null;
		
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"iduser", Const.iduser});
			values.add(new String[] {"name", name});
			values.add(new String[] {"gender", gender});
			values.add(new String[] {"password", pass});
			response = conn.sendRequestPost(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			if(result.equals("true"))
				return true;
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
		return false;
	}
	
	private class asyncEditProfile extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return changeProfile(arg0[0], arg0[1], arg0[2]);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result) {
				Toast.makeText(getApplicationContext(), "Change Complete", Toast.LENGTH_SHORT).show();
				Intent it = new Intent();
				Const.username = ed_name.getText().toString();
				if(!ed_newpass.getText().toString().equals("")) {
					Const.password = ed_newpass.getText().toString();
				}
				it.putExtra("gender", sp_gender.getSelectedItemPosition());
				setResult(RESULT_OK, it);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "Change Fail. Try Again", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}
