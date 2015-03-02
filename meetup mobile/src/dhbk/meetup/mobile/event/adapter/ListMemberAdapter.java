package dhbk.meetup.mobile.event.adapter;

import java.lang.reflect.Member;
import java.util.ArrayList;

import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.TabMember;
import dhbk.meetup.mobile.event.object.MemberObject;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ListMemberAdapter extends ArrayAdapter<MemberObject>{

	private TabMember tabmember;
	private ArrayList<MemberObject> listmember;
	
	public ListMemberAdapter(TabMember tabmember, ArrayList<MemberObject> listmember) {
		super(tabmember.getActivity(), R.layout.listmember_item, listmember);
		// TODO Auto-generated constructor stub
		this.tabmember = tabmember;
		this.listmember = listmember;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = tabmember.getActivity().getLayoutInflater().inflate(R.layout.listmember_item, null, true);
		
		TextView tv_name = (TextView) v.findViewById(R.id.memberitem_tv_name);
		tv_name.setText(listmember.get(position).name);
		Button btn_sendnotify = (Button) v.findViewById(R.id.memberitem_btn_invite_notify);
		btn_sendnotify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tabmember.sendNotify(listmember.get(position).idmember);
				System.out.println("ADAPTER : " + listmember.get(position).idmember);
			}
		});
		
		return v;
	}


}
