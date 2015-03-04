package dhbk.meetup.mobile.event.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.EventHomePage;
import dhbk.meetup.mobile.event.object.InviteObject;

public class ListInviteAdapter extends ArrayAdapter<InviteObject>{

	EventHomePage evhp;
	ArrayList<InviteObject> listinvite ;
	
	public ListInviteAdapter(EventHomePage evhp, ArrayList<InviteObject> listinvite) {
		super(evhp, R.layout.listmember_item, listinvite);
		// TODO Auto-generated constructor stub
		this.listinvite = listinvite;
		this.evhp = evhp;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = evhp.getLayoutInflater().inflate(R.layout.listmember_item, null, true);
		
		TextView tv_name = (TextView) v.findViewById(R.id.memberitem_tv_name);
		tv_name.setText(listinvite.get(position).name + "moi ban tham gia su kien " + listinvite.get(position).title);
		Button btn_accept = (Button) v.findViewById(R.id.memberitem_btn_invite_notify);
		btn_accept.setText("Accept");
		btn_accept.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(listinvite.get(position).isVisible){
					evhp.acceptInvite(String.valueOf(position));
				}
//					evhp.sendNotify(listmember.get(position).idmember, String.valueOf(position));
				System.out.println("ADAPTER 3 : " + listinvite.get(position).idinvite);
			}
		});
		if(!listinvite.get(position).isVisible) {
			btn_accept.setBackgroundColor(evhp.getResources().getColor(R.color.dark_grey));
//			btn_sendnotify.setText("THOI");
			System.out.println("DA SET");
		}
		
		return v;
	}
	
}
