package musar;



import java.util.ArrayList;
import java.util.List;

public class refresh_data {
	String user_id;
	List<String>installedApps=new ArrayList<String>();
	ArrayList<Contact>contacts=new ArrayList<Contact>();
	public refresh_data(){}
	public refresh_data(String user_id,String my_phone,List<String> installed,ArrayList<Contact>contacts) 
	{
		this.user_id=user_id;
		this.installedApps=installed;
		this.contacts=contacts;
	}
	public void set_id(String user_id){this.user_id=user_id;}
	public void set_installedApps(ArrayList<String>installedApps){this.installedApps=installedApps;}
	public void set_contacts(ArrayList<Contact>contacts){this.contacts=contacts;}
	
	public String get_id(){return this.user_id;}
	public List<String>get_installed(){return this.installedApps;}
	public ArrayList<Contact>get_contact(){return this.contacts;}
}
