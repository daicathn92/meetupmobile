package dhbk.meetup.mobile.event.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.ShareTemplate;

public class ListTemplateShareAdapter extends ArrayAdapter<String> {

	private Activity context;
	private ArrayList<String> listFile;
	
	public ListTemplateShareAdapter(Activity context, ArrayList<String> listFile) {
		super(context, R.layout.sharetemplate_item, listFile);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.listFile = listFile;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = context.getLayoutInflater().inflate(R.layout.sharetemplate_item, null, true);
		TextView tv = (TextView) v.findViewById(R.id.sharetempitem_tv_fileName);
		CheckBox cb = (CheckBox) v.findViewById(R.id.sharetempitem_cb_chooseFile);
		tv.setText(listFile.get(position));
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				System.out.println("CHECK CHANGE : " + isChecked);
				((ShareTemplate)context).isChoose.get(position).set(isChecked);
			}
		});
		return v;
	}

}
