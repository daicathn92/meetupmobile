package dhbk.meetup.mobile.event;

import dhbk.meetup.mobile.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class TabDoc extends Fragment implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("ONCREATE TABDOC");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.tab_doc, container, false);
		System.out.println("ONCREATEVIEW TABDOC");
		return v;
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
		
	}

}