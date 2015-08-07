package musar;



public class Contact {
    
    //private variables
    int _id;
    String _name;
    String _phone_number;
    String _image;
    // Empty constructor
    public Contact(){
         
    }
    // constructor
    public Contact(int id, String name, String _phone_number,String _image){
        this._id = id;
        this._name = name;
        this._phone_number = _phone_number;
        this._image=_image;
    }
     
    // constructor
    public Contact(String name, String _phone_number,String _image){
        this._name = name;
        this._phone_number = _phone_number;
        this._image=_image;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
     
    // setting id
    public void setID(int id){
        this._id = id;
    }
     
    // getting name
    public String getName(){
        return this._name;
    }
     
    // setting name
    public void setName(String name){
        this._name = name;
    }
     
    // getting phone number
    public String getPhoneNumber(){
        return this._phone_number;
    }
     
    // setting phone number
    public void setPhoneNumber(String phone_number){
        this._phone_number = phone_number;
    }
    
    // getting image
    public String getImage(){
        return this._image;
    }
     
    // setting phone number
    public void setImage(String Image){
        this._image = Image;
    }
}