package dhbk.meetup.mobile.event.adapter;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.TabDoc;
import dhbk.meetup.mobile.event.object.DocumentObject;

public class ListDocumentAdapter extends ArrayAdapter<DocumentObject>{

	private TabDoc tabdoc;
	private ArrayList<DocumentObject> listdoc;
	
	public ListDocumentAdapter(TabDoc tabdoc, ArrayList<DocumentObject> listdoc) {
		super(tabdoc.getActivity(), R.layout.listmember_item, listdoc);
		// TODO Auto-generated constructor stub
		this.tabdoc = tabdoc;
		this.listdoc = listdoc;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = tabdoc.getActivity().getLayoutInflater().inflate(R.layout.listmember_item, null, true);
		
		TextView tv_name = (TextView) v.findViewById(R.id.memberitem_tv_name);
		tv_name.setText(listdoc.get(position).name);
		Button btn_download = (Button) v.findViewById(R.id.memberitem_btn_invite_notify);
		btn_download.setText("Download");
		btn_download.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(listdoc.get(position).isVisible)
					tabdoc.downloadDocument(String.valueOf(position));
			}
		});
		if(!listdoc.get(position).isVisible) {
			btn_download.setBackgroundColor(tabdoc.getResources().getColor(R.color.dark_grey));
//			btn_sendnotify.setText("THOI");
			System.out.println("DA SET DOWNLOAD");
		}
		
		return v;
	}


}
