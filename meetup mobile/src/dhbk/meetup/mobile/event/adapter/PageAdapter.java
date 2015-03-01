package dhbk.meetup.mobile.event.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter{

	private ArrayList<Fragment> arr;
	
	public PageAdapter(FragmentManager fm, ArrayList<Fragment> arr) {
		super(fm);
		this.arr = arr;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return arr.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arr.size();
	}

	
}
