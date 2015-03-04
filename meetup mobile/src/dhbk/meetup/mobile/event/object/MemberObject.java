package dhbk.meetup.mobile.event.object;

public class MemberObject {

	public String idmember, iduser, name;
	public boolean isVisible = true;
	
	public MemberObject(String idmember, String iduser, String name) {
		this.idmember = idmember;
		this.iduser = iduser;
		this.name = name;
	}
	
	public MemberObject (MemberObject mo) {
		this.idmember = mo.idmember;
		this.iduser = mo.iduser;
		this.name = mo.name;
	}
}
