package dhbk.meetup.mobile.event.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

public class TabFactory implements TabContentFactory{

	private final Context ct;
	
	public TabFactory(Context ct) {
		// TODO Auto-generated constructor stub
		this.ct = ct;
	}
	
	@Override
	public View createTabContent(String tag) {
		// TODO Auto-generated method stub
		View v = new View(ct);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
	}

}
