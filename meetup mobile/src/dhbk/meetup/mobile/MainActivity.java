package dhbk.meetup.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import dhbk.meetup.mobile.login.Login;

public class MainActivity extends Activity implements OnTouchListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LinearLayout layout_main = (LinearLayout) findViewById(R.id.layout_main);
		layout_main.setOnTouchListener(this);
//		Goo
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		Intent it = new Intent(getApplicationContext(), Login.class);
		startActivity(it);
		return true;
	}

	
}
