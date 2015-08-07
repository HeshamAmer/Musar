package musar;



import java.util.ArrayList;
import java.util.List;

public class user{
	private String Type;
	private List<user> userFriends;
	private List<user> similarFriends;
	private String user_id;
	private String name;
	private String image;
	user(String user_id,String Type){
		this.user_id=user_id;
		this.Type=Type;
		
	}			
	user(String name,String type,String user_id,String image){
		Type=type;
		this.name=name;
		this.user_id=user_id;
		this.image=image;
	}
	public String get_id(){return user_id;}
	public String get_type(){return Type;}
	public String get_name(){return name;}
	public List<user> get_friend(){return userFriends;}
	public List<user>get_similar(){return similarFriends;}
	public String get_image(){return image;}
	
	
	public void set_id(String id){this.user_id=id;}
	public void set_type(String type){this.Type=type;}
	public void set_name(String name){this.name=name;}
	public void set_user_friend(List<user>userfriends){this.userFriends=userfriends;}
	public void set_similarfriend(List<user>similarfriends){this.similarFriends=similarfriends;}
	public void set_image(String image){this.image=image;}
}