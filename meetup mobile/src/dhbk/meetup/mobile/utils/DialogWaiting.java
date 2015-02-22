package dhbk.meetup.mobile.utils;

import dhbk.meetup.mobile.login.Register;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;

public class DialogWaiting {

	private ProgressDialog pg_dialog;
	private Activity context;
	
	public DialogWaiting (Activity context) {
		this.context = context;
	}
	
	public void showProgressDialog () {
		if(pg_dialog == null ) {
			pg_dialog = new ProgressDialog(context);
			pg_dialog.setCancelable(false) ;
			pg_dialog.setTitle("Processing");
			pg_dialog.setMessage("Please Wait !!!");
			pg_dialog.setIndeterminate(false);
			pg_dialog.show();
		} 
	}
	
	public void closeProgressDialog () {
		if(pg_dialog != null) {
			if(pg_dialog.isShowing())
				pg_dialog.dismiss();
			pg_dialog = null;
		}
	}
}
