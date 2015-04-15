package dhbk.meetup.mobile.event.object;

public class DocumentObject {

	public String name, id, type; // path = id/name
	public boolean isVisible = true;
	
	public DocumentObject(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	public DocumentObject(String name, String id, String type) {
		this.name = name;
		this.type = type;
		this.id = id;
	}
}
