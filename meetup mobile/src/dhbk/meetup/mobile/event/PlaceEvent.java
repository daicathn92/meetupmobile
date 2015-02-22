package dhbk.meetup.mobile.event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import dhbk.meetup.mobile.R;

public class PlaceEvent extends Activity implements OnClickListener{

	private EditText ed_place;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooseplace);
		
		ed_place = (EditText) findViewById(R.id.chooseplace_ed_place);
		Button btn_ok = (Button) findViewById(R.id.chooseplace_btn_ok);
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.chooseplace_btn_ok :
			String place = ed_place.getText().toString();
			if(place.equals("")) {
				Toast.makeText(getApplicationContext(), "fill all", Toast.LENGTH_SHORT).show();
			} else {
				Intent it = new Intent();
				it.putExtra("place", place);
				setResult(RESULT_OK, it);
				finish();
			}
			break;
		}
	}
	
	
}
