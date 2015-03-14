package dhbk.meetup.mobile.event;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import dhbk.meetup.mobile.utils.Const;

public class AEvent extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener{

	public static final String TAB_HOME = "Home";
	public static final String TAB_MEMBER = "Member";
	public static final String TAB_CHAT = "Message";
	public static final String TAB_DOC = "Document";
	
	private TabHost tabhost;
	private ViewPager viewPage;
	
	private String idevent = "";
	private boolean ismember = false;
	private String idusercreate = "-1", iduser;
	
	public TabHome th ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_tabhost);
		
		Intent it = getIntent();
		idevent = it.getExtras().getString("idevent");
		ismember = it.getExtras().getBoolean("ismember");
		idusercreate = it.getExtras().getString("idusercreate");
		iduser = it.getExtras().getString("iduser");
		
		// set username, password khi goi tu notify den
		if(!iduser.equals("none")) {
			Const.iduser = iduser;
			SharedPreferences prefs = getSharedPreferences(Const.PREFERENCE_MEETUP_MOBILE, Context.MODE_PRIVATE);
			Const.username = prefs.getString("username", "");
			Const.password = prefs.getString("password", "");
		}
		
		System.out.println("IDEVENT : " + idevent);
		
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		tabhost.setOnTabChangedListener(this);
		viewPage = (ViewPager) findViewById(R.id.tabhost_viewpager);
		
		TabSpec tsHome = (tabhost.newTabSpec(TAB_HOME)).setIndicator(TAB_HOME);
		tsHome.setContent(new TabFactory(this));
		tabhost.addTab(tsHome);
		
		if(ismember) {
			TabSpec tsMember = (tabhost.newTabSpec(TAB_MEMBER)).setIndicator(TAB_MEMBER);
			TabSpec tsChat = (tabhost.newTabSpec(TAB_CHAT)).setIndicator(TAB_CHAT);
			TabSpec tsDoc = (tabhost.newTabSpec(TAB_DOC)).setIndicator(TAB_DOC);
			
			
			tsMember.setContent(new TabFactory(this));
			tsChat.setContent(new TabFactory(this));
			tsDoc.setContent(new TabFactory(this));
			
			tabhost.addTab(tsMember);
			tabhost.addTab(tsChat);
			tabhost.addTab(tsDoc);
		}
		
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
		
		th = new TabHome();
		th.setIdevent(idevent);
		th.setIsmember(ismember);
		th.setIdusercreate(idusercreate);
		list.add(th);
		
		if(ismember) {
			TabMember tm =new TabMember();
			TabChat tc = new TabChat();
			TabDoc td = new TabDoc();
			
			tc.setIdevent(idevent);
			tm.setIdevent(idevent);
			
			list.add(tm);
			list.add(tc);
			list.add(td);
		}
		
		return list;
	}
}
