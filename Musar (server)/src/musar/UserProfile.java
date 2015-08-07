package musar;



public class UserProfile {
	private String name;
	private int index;
	private char sex;
	private String country;
	private int age;
	
	public UserProfile(String name, int index, int age, char sex, String country) {
		super();
		this.name = name;
		this.index = index;
		this.age = age;
		this.country = country;
		this.sex = sex;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
	
	public char getSex() {
		return sex;
	}
	public void setSex(char sex) {
		this.sex = sex;
	}
	
	
	
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

}
