package dhbk.meetup.mobile.event;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.adapter.ListTemplateShareAdapter;
import dhbk.meetup.mobile.event.storage.SwiftApi;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.Storage;
import dhbk.meetup.mobile.utils.Utils;

public class ShareTemplate extends Activity implements OnClickListener{

	private HttpConnect conn;
	private DialogWaiting dialog;
	private ArrayList<String> listTemplate;
	public ArrayList<AtomicBoolean> isChoose = new ArrayList<AtomicBoolean>();
	private ListTemplateShareAdapter adapter;
	
	private SwiftApi swiftApi = new SwiftApi();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharetemplate);
		
		conn = new HttpConnect();
		dialog = new DialogWaiting(this);
		
		listTemplate = Storage.getListFile(this);
		for(int i = 0; i < listTemplate.size(); i++) {
			isChoose.add(new AtomicBoolean(false));
		}	
			
		ListView lv = (ListView) findViewById(R.id.sharetemp_lv);
		adapter = new ListTemplateShareAdapter(this, listTemplate);
		lv.setAdapter(adapter);
		
		Button btn_cancel = (Button) findViewById(R.id.sharetemp_btn_cancel);
		Button btn_share = (Button) findViewById(R.id.sharetemp_btn_share);
		btn_cancel.setOnClickListener(this);
		btn_share.setOnClickListener(this);
		
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.sharetemp_btn_cancel :
			super.onBackPressed();
			break;
		case R.id.sharetemp_btn_share : 
			boolean ischoose = false;
			for(AtomicBoolean b : isChoose) {
				if(b.get()) {
					ischoose = true;
					break;
				}
			}
			if(ischoose) {
				if(Utils.isConnectNetwork(ShareTemplate.this)) {
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new AsyncShareTemplate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					} else {
						new AsyncShareTemplate().execute();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Network not connected", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Choose Template", Toast.LENGTH_SHORT).show();
			}
			break;
		default : break;
		}
	}
	
	private boolean shareTemplate () {
		/*cai dat upload file*/
		System.out.println("SHAREEEEEEEEEEEEE");
		boolean result = true;
		for(int i = 0; i < isChoose.size(); i++) {
			AtomicBoolean ab = isChoose.get(i);
			if(ab.get()) {
				String content = Storage.readFile(listTemplate.get(i), getApplicationContext());
				result = swiftApi.uploadObject(SwiftApi.CONTAINER_TEMPLATES, Const.iduser, listTemplate.get(i), null, content);
				if(!result) return result;
			}
		}
		return true;
	}
	
	private class AsyncShareTemplate extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return shareTemplate();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result) {
				Toast.makeText(getApplicationContext(), "Share Success", Toast.LENGTH_SHORT).show();
				for(int i = 0; i< listTemplate.size(); i++) {
					boolean ischoose = isChoose.get(i).get();
					if(ischoose) {
						isChoose.remove(i);
						listTemplate.remove(i);
						i--;
					}
				adapter.notifyDataSetChanged();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Share Fail! Try Again!", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}
