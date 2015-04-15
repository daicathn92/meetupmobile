package dhbk.meetup.mobile.event;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.adapter.DynamicListView;
import dhbk.meetup.mobile.event.adapter.ListEventLocalAdapter;
import dhbk.meetup.mobile.event.object.DocumentObject;
import dhbk.meetup.mobile.event.object.EventObject;
import dhbk.meetup.mobile.event.storage.SwiftApi;
import dhbk.meetup.mobile.httpconnect.HttpConnect;
import dhbk.meetup.mobile.utils.Const;
import dhbk.meetup.mobile.utils.DialogWaiting;
import dhbk.meetup.mobile.utils.FormatFileApp;
import dhbk.meetup.mobile.utils.Storage;
import dhbk.meetup.mobile.utils.Utils;

@SuppressLint("NewApi")
public class ImportTemplate extends Activity implements 
		OnClickListener, OnDateSetListener, OnMenuItemClickListener{

	public static final int REQUESTCODE_EDITEVENT = 10;
	public static final int REQUESTCODE_ADDEVENT = 100;
	
	public static final String EVENT_CREATEMULTI = "createmultievent";
	
	private HttpConnect conn;
	private DialogWaiting dialog;
	private SwiftApi swiftApi;
	@SuppressLint("SimpleDateFormat")
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private ArrayList<EventObject> listEvent_local = new ArrayList<EventObject>();
	private ArrayList<String[]> listTemplateOnline = new ArrayList<String[]>();
	
	private TextView tv_date, tv_template;
	private ListEventLocalAdapter adapter;
	public DynamicListView listView ;
	
	private DatePickerDialog datePicker;
	private Calendar calendar = null;
	
	private PopupMenu popupMenu;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.importtemplate);
		
		conn = new HttpConnect();
		swiftApi = new SwiftApi();
		dialog = new DialogWaiting(ImportTemplate.this);
		
		calendar = Calendar.getInstance();
		datePicker = new DatePickerDialog(ImportTemplate.this, ImportTemplate.this, calendar.get(Calendar.YEAR)
				, calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
		
		tv_date = (TextView) findViewById(R.id.import_tv_date);
		tv_template = (TextView) findViewById(R.id.import_tv_template);
		ImageButton imgbtn_choosetemplate = (ImageButton) findViewById(R.id.import_imgbtn_choosetemplate);
		ImageButton imgbtn_date = (ImageButton) findViewById(R.id.import_imgbtn_date);
		ImageButton imgbtn_addevent = (ImageButton) findViewById(R.id.import_imgbtn_addevent);
		Button btn_create = (Button) findViewById(R.id.import_create);
		imgbtn_choosetemplate.setOnClickListener(this);
		imgbtn_date.setOnClickListener(this);
		imgbtn_addevent.setOnClickListener(this);
		btn_create.setOnClickListener(this);
//		Date date = calendar.getTime();
		
		listView = (DynamicListView) findViewById(R.id.import_lv);
        listView.setOnItemClickListener(mOnItemClickListener);
		
		popupMenu = new PopupMenu(this, imgbtn_choosetemplate);
		popupMenu.setOnMenuItemClickListener(this);
		popupMenu.getMenuInflater().inflate(R.menu.menu_choose_template, popupMenu.getMenu());
		
		tv_date.setText(sdf.format(calendar.getTime()));
		
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		// result from activity edit event
		if(requestCode == REQUESTCODE_EDITEVENT) {
			if(resultCode == RESULT_OK) {
				// set lai gia tri cua event duoc sua
				String idevent_local = data.getExtras().getString("ideventlocal");
				String title = data.getExtras().getString("title");
				String content = data.getExtras().getString("content");
				String place = data.getExtras().getString("place");
				String time = data.getExtras().getString("time");
				boolean isPublic = data.getExtras().getBoolean("ispublic");
				
				for(EventObject eo : listEvent_local) {
					if(eo.idevent.equals(idevent_local)) {
						eo.title = title;
						eo.content = content;
						eo.place = place;
						eo.time = time;
						eo.isPublic = isPublic;
						eo.__time = time.split(" ")[1];
						break;
					}
				}
				
				adapter.notifyDataSetChanged();
			}
		}
		
		// result from activity addeventlocal
		if(requestCode == REQUESTCODE_ADDEVENT) {
			if(resultCode == RESULT_OK) {
				// 
				String time = data.getExtras().getString("time");
				try {
					EventObject eo = new EventObject(data.getExtras().getString("title"), "me", data.getExtras().getString("place"),
							Utils.deltaDay(sdf.parse(tv_date.getText().toString()).getTime(), sdf.parse(time.split(" ")[0]).getTime())+"", 
							time.split(" ")[1], data.getExtras().getString("content"), listEvent_local.size()+"", Const.iduser);
					eo.time = time;
					eo.isPublic = data.getExtras().getBoolean("ispublic");
					listEvent_local.add(eo);
					adapter.putIdMap(eo);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
						
				adapter.notifyDataSetChanged();
			}
		}
	}

	@SuppressLint({ "InlinedApi", "NewApi" })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.import_imgbtn_choosetemplate : 			popupMenu.show(); 							break;
		case R.id.import_imgbtn_date : 						showDatePickerDialog(); 					break;
		case R.id.import_imgbtn_addevent : 
			Intent it = new Intent(ImportTemplate.this, AddEventLocal.class);
			startActivityForResult(it, REQUESTCODE_ADDEVENT);
			break;
		case R.id.import_create :
			if(Utils.isConnectNetwork(ImportTemplate.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncCreateListEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new asyncCreateListEvent().execute() ;
				}
			} else {
				Toast.makeText(getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
			}
			break;
		default : break;
		}
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case R.id.menu_template_local :
			final ArrayList<String> listTemplate = Storage.getListFile(ImportTemplate.this);
			if(listTemplate.size() == 0)
				Toast.makeText(ImportTemplate.this, "No template available", Toast.LENGTH_SHORT).show();
			else {
				final ListView lv = new ListView(ImportTemplate.this);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(ImportTemplate.this, android.R.layout.simple_list_item_1, listTemplate);
				lv.setAdapter(adapter);
				final AlertDialog alertdialog = new AlertDialog.Builder(ImportTemplate.this)
				.setTitle("Choose Temple")
				.setView(lv)
				.show();
				
				lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						System.out.println("CLICK ");
						tv_template.setText(listTemplate.get(arg2));
//						setListEventFromTemplate();
						ArrayList<EventObject> list = FormatFileApp.formatFileToEvent(Storage.readFile(tv_template.getText().toString(), ImportTemplate.this));
						if(list.size() > 0) {
							listEvent_local.clear();
							for(EventObject eo : list) {
								listEvent_local.add(eo);
							}
						}
						
						ImportTemplate.this.adapter = new ListEventLocalAdapter(ImportTemplate.this, listEvent_local);
						listView.setCheeseList(listEvent_local);
				        listView.setAdapter(ImportTemplate.this.adapter);
				        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			        	listView.isDraggable = true;
			        	listView.fromLocal = true;
//			        	ImportTemplate.this.adapter.notifyDataSetChanged();
						
						System.out.println("SIZE : " +listEvent_local.size());
						setTimeDateEvent();
						
						
						alertdialog.dismiss();
						
					}
				});
				
						
			}
			return true;
		case R.id.menu_template_online :
			if(Utils.isConnectNetwork(ImportTemplate.this)) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new asyncListTemplateOnline().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new asyncListTemplateOnline().execute() ;
				}
			} else {
				Toast.makeText(getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
			}
			return true;
		default : return false;
		}
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Intent it = new Intent(getApplicationContext(), EditEventLocal.class);
			it.putExtra("title", listEvent_local.get(arg2).title);
			it.putExtra("time", listEvent_local.get(arg2).time);
			it.putExtra("content", listEvent_local.get(arg2).content);
			String[] places = listEvent_local.get(arg2).place.split(";");
			it.putExtra("place", places[0]);
			it.putExtra("lat", Double.parseDouble(places[1]));
			it.putExtra("lng", Double.parseDouble(places[2]));
			it.putExtra("ideventlocal", listEvent_local.get(arg2).idevent);
			it.putExtra("ispublic", listEvent_local.get(arg2).isPublic);
			
			startActivityForResult(it, REQUESTCODE_EDITEVENT);
		}
	};
	
	@Override
	public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		String newDate = arg1+ "-" +(arg2+1)+ "-" +arg3;
		if(tv_date.getText().toString().equals(newDate))
			return;
		tv_date.setText(newDate);
		setTimeDateEvent();
	}
	
	public void showDatePickerDialog () {
		datePicker.updateDate(calendar.get(Calendar.YEAR)
				, calendar.get(Calendar.MONTH) , calendar.get(Calendar.DAY_OF_MONTH));
		datePicker.show();
	}
	
	public void setTimeDateEvent () {
		// cap nhat lai ngay thang o moi event
		for(EventObject eo : listEvent_local) {
			System.out.println("SET TIMEDATE");
			String date = "";
			try {
				calendar.setTime(sdf.parse(tv_date.getText().toString()));
				calendar.add(Calendar.DATE, Integer.parseInt(eo.deltaDay));
				date = sdf.format(calendar.getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(date.equals(""))
				date = tv_date.getText().toString();
			eo.time = (date + " " + eo.__time);
		}
		adapter.notifyDataSetChanged();
	}
	
//	public void setListEventFromTemplate () {
//		try {
//			JSONObject jso_parent = new JSONObject();
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	public String getListeventWithJson () {
		JSONArray jsa = new JSONArray();
		
		for(EventObject eo : listEvent_local) {
			try {
				JSONObject jso = new JSONObject();
				jso.put("title", eo.title);
				jso.put("time", eo.time);
				jso.put("place", eo.place);
				jso.put("description", eo.content);
				jso.put("iduser", eo.idown);
				jso.put("ispublic", eo.isPublic?"true":"false");
				jsa.put(jso);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("JSONARRAY : " + jsa.toString());
		return jsa.toString();
	}
	
	public String createListEvent () {
		String url = Const.DOMAIN_NAME + EVENT_CREATEMULTI;
		
		HttpResponse response = null;
		try {
			ArrayList<String[]> values = new ArrayList<String[]>();
			values.add(new String[] {"listevent", getListeventWithJson()});
			values.add(new String[] {"num", listEvent_local.size()+""});
			values.add(new String[] {"iduser", Const.iduser});
			response = conn.sendRequestPost(url, null, values);
			String result = EntityUtils.toString(response.getEntity());
			return result;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "111";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "222";
		} finally {
			if(response != null ) {
				try {
					response.getEntity().consumeContent();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getListTemplateOnline () {
		listTemplateOnline = swiftApi.getListFile(SwiftApi.CONTAINER_TEMPLATES);
		for(int i = 0; i < listTemplateOnline.size(); i++) {
			if(listTemplateOnline.get(i)[0].equals("")) {
				listTemplateOnline.remove(i);
				i--;
			}
		}
	}
	
	//----------------action asyncTask----------------------------------------
	private class asyncCreateListEvent extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return createListEvent();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			System.out.println("KET QUA : " + result);
			if(result.equals("true")) {
				Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_SHORT).show();
				Intent it = new Intent();
				setResult(RESULT_OK, it);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "Create fail. Try again", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	private class asyncListTemplateOnline extends AsyncTask<String, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			getListTemplateOnline();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(listTemplateOnline.size() > 0) {
				final ArrayList<String> listTemplate = new ArrayList<String>();
				for(String[] ss : listTemplateOnline)
					listTemplate.add(ss[1]);
				final ListView lv = new ListView(ImportTemplate.this);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(ImportTemplate.this, android.R.layout.simple_list_item_1, listTemplate);
				lv.setAdapter(adapter);
				final AlertDialog alertdialog = new AlertDialog.Builder(ImportTemplate.this)
				.setTitle("Choose Temple")
				.setView(lv)
				.show();
				
				lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						System.out.println("CLICK ");
						tv_template.setText(listTemplate.get(arg2));
//						setListEventFromTemplate();
						if(Utils.isConnectNetwork(ImportTemplate.this)) {
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								new asyncReadTemplateOnline().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(arg2));
							} else {
								new asyncReadTemplateOnline().execute(String.valueOf(arg2)) ;
							}
						} else {
							Toast.makeText(getApplicationContext(), "Not connected network", Toast.LENGTH_SHORT).show();
						}
						alertdialog.dismiss();
					}
				});
			} else {
				Toast.makeText(getApplicationContext(), "No item template", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class asyncReadTemplateOnline extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.showProgressDialog();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			int position = Integer.parseInt(params[0]);
			return swiftApi.readObject(SwiftApi.CONTAINER_TEMPLATES, listTemplateOnline.get(position)[0], listTemplateOnline.get(position)[1]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.closeProgressDialog();
			if(result == null) {
				Toast.makeText(getApplicationContext(), "Import fail. Try again!", Toast.LENGTH_SHORT).show();
			} else {
				ArrayList<EventObject> list = FormatFileApp.formatFileToEvent(result);
				if(list.size() > 0) {
					listEvent_local.clear();
					for(EventObject eo : list) {
						listEvent_local.add(eo);
					}
				}
				
				ImportTemplate.this.adapter = new ListEventLocalAdapter(ImportTemplate.this, listEvent_local);
				listView.setCheeseList(listEvent_local);
		        listView.setAdapter(ImportTemplate.this.adapter);
		        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        	listView.isDraggable = true;
	        	listView.fromLocal = true;
	//        	ImportTemplate.this.adapter.notifyDataSetChanged();
				System.out.println("SIZE : " +listEvent_local.size());
				setTimeDateEvent();
			}
		}
		
	}
}
