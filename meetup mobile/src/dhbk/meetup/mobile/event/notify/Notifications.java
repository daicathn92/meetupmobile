package dhbk.meetup.mobile.event.notify;

import dhbk.meetup.mobile.R;
import dhbk.meetup.mobile.event.AEvent;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Notifications {

	public String idevent;
	private String content, title;
	private Context ct;
	
	public Notifications (String idevent, String title, String content, Context ct) {
		this.idevent = idevent;
		this.content = content;
		this.title = title;
		this.ct = ct;
	}
	
	public void showNotify () {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(ct)
		.setSmallIcon(R.drawable.add)
		.setTicker(title)
		.setContentTitle(title)
		.setContentText(content) ;
		
		Intent notifyIntent = new Intent(ct, AEvent.class);
		notifyIntent.putExtra("idevent", idevent);
		
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent notifyPendingIntent = PendingIntent.getActivity(ct, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		builder.setContentIntent(notifyPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) ct.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, builder.build());
	}
}
