package dhbk.meetup.mobile.event.object;

public class EventObject {

	public String title, own, place, time, content, idevent, idown;
	
	 public EventObject(String title, String own, String place, String time, String content, String idevent, String idown) {
		// TODO Auto-generated constructor stub
		this.title = title;
		this.own = own;
		this.place = place;
		this.time = time; 
		this.content = content;
		this.idevent = idevent;
		this.idown = idown;
	}
	 
	 public EventObject (EventObject eo) {
		 this.title = eo.title;
		 this.own = eo.own;
		 this.place = eo.place;
		 this.time = eo.time; 
		 this.content = eo.content;
		 this.idevent = eo.idevent;
		 this.idown = eo.idown;
	 }
}
