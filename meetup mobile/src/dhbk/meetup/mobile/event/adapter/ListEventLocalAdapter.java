package dhbk.meetup.mobile.event.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.ImportTemplate;
import dhbk.meetup.mobile.event.object.EventObject;

public class ListEventLocalAdapter extends ArrayAdapter<EventObject>{

	final int INVALID_ID = -1;
    
    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    ArrayList<EventObject> arr;
    Activity ct;

    public ListEventLocalAdapter(Activity context, ArrayList<EventObject> objects) {
        super(context, R.layout.event_item_link, objects);
        ct = context;
        arr = objects;
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i).idevent, i);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	// TODO Auto-generated method stub
    	View v = ct.getLayoutInflater().inflate(R.layout.event_item, null, true);
		TextView tv_title = (TextView) v.findViewById(R.id.eventitem_title);
		TextView tv_own = (TextView) v.findViewById(R.id.eventitem_own);
		TextView tv_place = (TextView) v.findViewById(R.id.eventitem_place);
		TextView tv_time = (TextView) v.findViewById(R.id.eventitem_time);
		TextView tv_content = (TextView) v.findViewById(R.id.eventitem_content);
		
		tv_title.setText(arr.get(position).title);
		tv_own.setText("Tạo bởi : " + arr.get(position).own);
		tv_place.setText("Tại : " + arr.get(position).place.split(";")[0]);
		tv_time.setText("Vào lúc : " + arr.get(position).time);
		tv_content.setText(arr.get(position).content);
		
		
    	if(getItemId(position) == ((ImportTemplate)ct).listView.mMobileItemId)
    		v.setVisibility(View.INVISIBLE);
    	return v;
    }
    
    @Override
    public long getItemId(int position) {
//    	System.out.println("GET ITEM ID : " + position);
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        String item = getItem(position).idevent;
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    
    public void putIdMap (EventObject eo) {
    	mIdMap.put(eo.idevent, mIdMap.size());
    }
	
}
