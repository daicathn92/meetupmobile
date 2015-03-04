package dhbk.meetup.mobile.event.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.TabMember;
import dhbk.meetup.mobile.event.object.MemberObject;

public class ListNotMemberAdapter extends ArrayAdapter<MemberObject>{

	private TabMember tabmember;
	private ArrayList<MemberObject> listNotMember;
	
	public ListNotMemberAdapter(TabMember tabmember, ArrayList<MemberObject> listNotMember) {
		super(tabmember.getActivity(), R.layout.listmember_item, listNotMember);
		// TODO Auto-generated constructor stub
		this.tabmember = tabmember;
		this.listNotMember = listNotMember;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = tabmember.getActivity().getLayoutInflater().inflate(R.layout.listmember_item, null, true);
		
		TextView tv_name = (TextView) v.findViewById(R.id.memberitem_tv_name);
		tv_name.setText(listNotMember.get(position).name);
		Button btn_sendinvite = (Button) v.findViewById(R.id.memberitem_btn_invite_notify);
		btn_sendinvite.setText("Invite");
		btn_sendinvite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(listNotMember.get(position).isVisible)
					tabmember.inviteMember(listNotMember.get(position).iduser, String.valueOf(position));
				System.out.println("ADAPTER 2 : " + listNotMember.get(position).idmember);
			}
		});
		
		if(!listNotMember.get(position).isVisible) {
			btn_sendinvite.setBackgroundColor(tabmember.getResources().getColor(R.color.dark_grey));
//			btn_sendnotify.setText("THOI");
			System.out.println("DA SET");
		}
		
		return v;
	}
	
}
