package dhbk.meetup.mobile.event;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.adapter.ListDocumentAdapter;
import dhbk.meetup.mobile.event.object.DocumentObject;
import dhbk.meetup.mobile.event.storage.SwiftApi;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.ReadWriteFile;
import dhbk.meetup.mobile.utils.Utils;

public class TabDoc extends Fragment implements OnClickListener{

	public static final int REQUESTCODE_CHOOSEFILE = 10;
	
	private HttpConnect conn;
	private DialogWaiting dialog;
	private SwiftApi swiftApi = new SwiftApi();
	
	private String idevent;
	private ListDocumentAdapter adapter;
	private ArrayList<DocumentObject> listdoc = new ArrayList<DocumentObject>();
	
	long maxBytes;
	
	@SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("ONCREATE TABDOC");
		conn = new HttpConnect();
		dialog = new DialogWaiting(getActivity());
		adapter = new ListDocumentAdapter(this, listdoc);
		
		ActivityManager am = (ActivityManager) getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
		int memoryClass = am.getMemoryClass();
		maxBytes = (memoryClass * 1024 * 1024) / 30; // ???/16
		
		// load list document 
		if(Utils.isConnectNetwork(getActivity())) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncListDocument().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new asyncListDocument().execute() ;
			}
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.tab_doc, container, false);
		System.out.println("ONCREATEVIEW TABDOC");
		ListView lv = (ListView) v.findViewById(R.id.tabdoc_lv);
		lv.setAdapter(adapter);
		Button btn_upload = (Button) v.findViewById(R.id.tabdoc_btn_upload);
		btn_upload.setOnClickListener(this);
		
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUESTCODE_CHOOSEFILE) {
			if(resultCode == Activity.RESULT_OK) {
				
				final String path[] = ReadWriteFile.parsePathFile(getActivity(), data.getData()); // path[0] = path; path[1] = name
				if(path == null) {
					Toast.makeText(getActivity().getApplicationContext(), "File invalid! Try again with file image or text", Toast.LENGTH_SHORT).show();
					return;
				}
				
				new AlertDialog.Builder(getActivity())
    			.setTitle("Upload")
    			.setMessage("Do you want upload " + path[1])
    			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String content = ReadWriteFile.readFile(path[0], path[1], maxBytes);
//						System.out.println("content : " + content);
						if(content == null) {
							Toast.makeText(getActivity().getApplicationContext(), "Read file error. Try again!", Toast.LENGTH_SHORT).show();
							return;
						}
						
						if(Utils.isConnectNetwork(getActivity())) {
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								new asyncUpload().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path[1], path[0], content);
							} else {
								new asyncUpload().execute(path[1], path[0], content) ;
							}
						} else {
							Toast.makeText(getActivity().getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
						}
					}
    			})
    			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				})
    			.show();
			}
		}
	}
	
	@Override
	public void onPause() {
	// TODO Auto-generated method stub
		super.onPause();
		System.out.println("ONPAUSE TABDOC");
	}
	
	@Override
	public void onDestroy() {
	// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("ONDESTROY TABDOC");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tabdoc_btn_upload :
			Intent it = new Intent();
			it.setAction(Intent.ACTION_GET_CONTENT);
			it.addCategory(Intent.CATEGORY_OPENABLE);
			it.setType("*/*");
			Intent __it = Intent.createChooser(it, "Choose File");
			startActivityForResult(__it, REQUESTCODE_CHOOSEFILE);
			break;
		default : break;
		}
	}
	
	public void setIdevent(String idevent) {
		this.idevent = idevent;
	}
	
	public String getListDocument () {
		ArrayList<String[]> listDoc = swiftApi.getListFile(SwiftApi.CONTAINER_DOCUMENTS);
		for(String[] ss : listDoc) {
			if(ss[0].equals(idevent)) {
				listdoc.add(new DocumentObject(ss[1], ss[0]));
			}
		}
		return null;
	}
	
	@SuppressLint("NewApi")
	public void downloadDocument (String position) {
		if(Utils.isConnectNetwork(getActivity())) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				new asyncDownload().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, position);
			} else {
				new asyncDownload().execute(position) ;
			}
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
		}
	}
	
//	public String downloading () {
//		
//		return null;
//	}
	
//	public boolean uploading (String container, String id, String name, String path) {
//		
//		return swiftApi;
//	}
	
	/*-----------------async action-------------------*/
	private class asyncListDocument extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return getListDocument();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			adapter.notifyDataSetChanged();
		}
	}
	
	private class asyncDownload extends AsyncTask<String, Void, Boolean> {
		
		int position = -1;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
			System.out.println("ONPREEEEE");
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			position = Integer.parseInt(params[0]);
			System.out.println("DOINBG");
			return swiftApi.downloadObject(SwiftApi.CONTAINER_DOCUMENTS, listdoc.get(position).id, listdoc.get(position).name);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result) {
				Toast.makeText(getActivity().getApplicationContext(), "Download Complete", Toast.LENGTH_SHORT).show();
				listdoc.get(position).isVisible = false;
				adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Download Fail. Try again!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class asyncUpload extends AsyncTask<String, Void, Boolean> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			return swiftApi.uploadObject(SwiftApi.CONTAINER_DOCUMENTS, idevent, params[0], null, params[2]);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result) {
				Toast.makeText(getActivity().getApplicationContext(), "Upload Complete", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Upload Fail. Try again!", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
