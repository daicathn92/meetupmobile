package dhbk.meetup.mobile.event.object;

public class InviteObject {

	public String idinvite, idevent, name, title;
	public boolean isVisible = true;
	
	public InviteObject(String idinvite, String idevent, String name, String title) {
		this.idinvite = idinvite;
		this.idevent = idevent;
		this.name = name;
		this.title = title;
	}
	
	public InviteObject (InviteObject io) {
		this.idinvite = io.idinvite;
		this.idevent = io.idevent;
		this.name = io.name;
		this.title = io.title;
	}
	
}
