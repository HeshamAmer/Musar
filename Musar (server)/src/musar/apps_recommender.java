package musar;




public class apps_recommender {
	private String name;
	private String Type;
	private String package_name;
	apps_recommender(String package_name)
	{
		this.package_name=package_name;
	}
	apps_recommender(String package_name,String name,String type) {
		this.name=name;
		this.Type=type;
		this.package_name=package_name;
	}
	public String get_name(){return name;}
	public String get_type(){return Type;}
	public String get_package(){return package_name;}

	
	
	public void set_name(String name){this.name=name;}
	public void set_type(String type){this.Type=type;}
	public void set_package(String package_name){this.package_name=package_name;}
	
}

