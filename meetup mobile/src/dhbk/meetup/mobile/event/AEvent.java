package dhbk.meetup.mobile.event;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.adapter.PageAdapter;
import dhbk.meetup.mobile.event.adapter.TabFactory;

public class AEvent extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener{

	public static final String TAB_HOME = "Home";
	public static final String TAB_MEMBER = "Member";
	public static final String TAB_CHAT = "Message";
	public static final String TAB_DOC = "Document";
	
	private TabHost tabhost;
	private ViewPager viewPage;
	
	private String idevent = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_tabhost);
		
		Intent it = getIntent();
		idevent = it.getExtras().getString("idevent");
		System.out.println("IDEVENT : " + idevent);
		
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		tabhost.setOnTabChangedListener(this);
		viewPage = (ViewPager) findViewById(R.id.tabhost_viewpager);
		
		TabSpec tsHome = (tabhost.newTabSpec(TAB_HOME)).setIndicator(TAB_HOME);
		TabSpec tsMember = (tabhost.newTabSpec(TAB_MEMBER)).setIndicator(TAB_MEMBER);
		TabSpec tsChat = (tabhost.newTabSpec(TAB_CHAT)).setIndicator(TAB_CHAT);
		TabSpec tsDoc = (tabhost.newTabSpec(TAB_DOC)).setIndicator(TAB_DOC);
		
		tsHome.setContent(new TabFactory(this));
		tsMember.setContent(new TabFactory(this));
		tsChat.setContent(new TabFactory(this));
		tsDoc.setContent(new TabFactory(this));
		
		tabhost.addTab(tsHome);
		tabhost.addTab(tsMember);
		tabhost.addTab(tsChat);
		tabhost.addTab(tsDoc);
		
		PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(), getListFragments());	
		viewPage.setAdapter(pageAdapter);
		viewPage.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		tabhost.setCurrentTab(viewPage.getCurrentItem());
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		viewPage.setCurrentItem(tabhost.getCurrentTab());
	}
	
	public ArrayList<Fragment> getListFragments () {
		ArrayList<Fragment> list = new ArrayList<Fragment>();
		
		TabHome th = new TabHome();
		TabMember tm =new TabMember();
		TabChat tc = new TabChat();
		TabDoc td = new TabDoc();
		
		th.setIdevent(idevent);
		tc.setIdevent(idevent);
		tm.setIdevent(idevent);
		
		list.add(th);
		list.add(tm);
		list.add(tc);
		list.add(td);
		
		return list;
	}
}
