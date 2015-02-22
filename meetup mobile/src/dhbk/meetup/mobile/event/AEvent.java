package dhbk.meetup.mobile.event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AEvent extends Activity{

	private int idevent = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent it = getIntent();
		idevent = it.getExtras().getInt("idevent");
		System.out.println("IDEVENT : " + idevent);
	}
}
