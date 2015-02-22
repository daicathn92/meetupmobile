package dhbk.meetup.mobile.event;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.object.EventObject;

public class ListEventAdapter extends ArrayAdapter<String>{

	private ArrayList<EventObject> eventObject;
	private Activity context;
	
	public ListEventAdapter(Activity context, ArrayList<EventObject> eventObject) {
		super(context, R.layout.eventitem);
		// TODO Auto-generated constructor stub
		this.eventObject = eventObject;
		this.context = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View item = context.getLayoutInflater().inflate(R.layout.eventitem, null, true);
		TextView tv_title = (TextView) item.findViewById(R.id.eventitem_title);
		TextView tv_own = (TextView) item.findViewById(R.id.eventitem_own);
		TextView tv_place = (TextView) item.findViewById(R.id.eventitem_place);
		TextView tv_time = (TextView) item.findViewById(R.id.eventitem_time);
		TextView tv_content = (TextView) item.findViewById(R.id.eventitem_content);
		
		tv_title.setText(eventObject.get(position).title);
		tv_own.setText(eventObject.get(position).own);
		tv_place.setText(eventObject.get(position).place);
		tv_time.setText(eventObject.get(position).time);
		tv_content.setText(eventObject.get(position).content);
		return item;
	}

}
