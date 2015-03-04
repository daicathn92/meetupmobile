package dhbk.meetup.mobile.event.member;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.EventHomePage;
import dhbk.meetup.mobile.event.TabMember;

public class BeInvite {

	public AlertDialog dialog;
	public ListView lv_invite;
	
	public BeInvite(EventHomePage evhp) {
		View v = LayoutInflater.from(evhp).inflate(R.layout.invitemember, null);
		
		Button btn_inviteall = (Button) v.findViewById(R.id.invitemember_btn_inviteall);
		btn_inviteall.setVisibility(View.GONE);
		
		lv_invite = (ListView) v.findViewById(R.id.invitemember_lv_listnotmember);
		lv_invite.setAdapter(evhp.listinviteAdapter);
		
		dialog = new AlertDialog.Builder(evhp).create();
		dialog.setTitle("Be Invite");
		dialog.setView(v, 0, 0, 0, 0);
//		dialog.show();
	}
}
