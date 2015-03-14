/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dhbk.meetup.mobile.event.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.EventLink;
import dhbk.meetup.mobile.event.googlemap.PlaceEvent;
import dhbk.meetup.mobile.event.object.EventObject;
import dhbk.meetup.mobile.utils.Utils;

public class StableArrayAdapter extends ArrayAdapter<EventObject> {

    final int INVALID_ID = -1;
    
    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
    ArrayList<EventObject> arr;
    Activity ct;

    public StableArrayAdapter(Activity context, ArrayList<EventObject> objects) {
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
    	View v = ct.getLayoutInflater().inflate(R.layout.event_item_link, null, true);
    	TextView tv_title = (TextView) v.findViewById(R.id.event_item_link_tv_title);
    	TextView tv_own = (TextView) v.findViewById(R.id.event_item_link_tv_own);
    	TextView tv_place = (TextView) v.findViewById(R.id.event_item_link_tv_place);
    	TextView tv_time = (TextView) v.findViewById(R.id.event_item_link_tv_time);
    	final String []placefull = arr.get(position).place.split(";");
    	tv_own.setText(arr.get(position).own);
    	tv_title.setText(arr.get(position).title);
    	tv_place.setText(placefull[0]);
    	tv_time.setText(arr.get(position).time);
    	
    	ImageView img = (ImageView) v.findViewById(R.id.event_item_link_imgbtn_place);
    	img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Utils.isGPSEnable(((EventLink)ct).locationManager)) {
					Intent it = new Intent(ct.getApplicationContext(), PlaceEvent.class);
					it.putExtra("onlyview", true);
					it.putExtra("lat", Double.parseDouble(placefull[1]));
					it.putExtra("lng", Double.parseDouble(placefull[2]));
					it.putExtra("place", placefull[0]);
					ct.startActivity(it);
				} else {
					new AlertDialog.Builder(ct)
	    			.setTitle("GPS")
	    			.setMessage("Enable GPS")
	    			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent itGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					        ct.startActivity(itGPS);
						}
	    			})
	    			.show();
				}
			}
		});
    	if(getItemId(position) == ((EventLink)ct).listView.mMobileItemId)
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
