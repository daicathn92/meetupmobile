package dhbk.meetup.mobile.event.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import dhbk.meetup.mobile.R;

public class ListMenuAdapter extends ArrayAdapter<String>{

	private Activity ct;
	private Integer[] arr_img;
	private String[] arr_text;
	
	public ListMenuAdapter(Activity context, final Integer[] arr_img, final String[] arr_text) {
		super(context, R.layout.listmenu_item, arr_text);
		// TODO Auto-generated constructor stub
		this.ct = context;
		this.arr_img = arr_img;
		this.arr_text = arr_text;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = ct.getLayoutInflater().inflate(R.layout.listmenu_item, null, true);
		TextView tv = (TextView) v.findViewById(R.id.listmenu_item_text);
		ImageView img = (ImageView) v.findViewById(R.id.listmenu_item_img);
		tv.setText(arr_text[position]);
		img.setImageResource(arr_img[position]);
		return v;
	}

}
