package dhbk.meetup.mobile.event.member;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.TabMember;
import dhbk.meetup.mobile.event.adapter.ListNotMemberAdapter;

public class InviteMember {

	public AlertDialog dialog;
	public ListView lv_invite;
	
	public InviteMember(TabMember tabmember) {
		View v = LayoutInflater.from(tabmember.getActivity()).inflate(R.layout.invitemember, null);
		
		Button btn_inviteall = (Button) v.findViewById(R.id.invitemember_btn_inviteall);
		btn_inviteall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// invite all
			}
		});
		
		lv_invite = (ListView) v.findViewById(R.id.invitemember_lv_listnotmember);
//		ListNotMemberAdapter listnotmemberAdapter = new ListNotMemberAdapter(tabmember, tabmember.listnotmember);
		lv_invite.setAdapter(tabmember.listnotmemberAdapter);
		
		dialog = new AlertDialog.Builder(tabmember.getActivity()).create();
		dialog.setTitle("Invite Member");
		dialog.setView(v, 0, 0, 0, 0);
//		dialog.show();
	}
}
