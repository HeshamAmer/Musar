package musar;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.sun.jersey.core.util.Base64;

public class hash {
	@SuppressWarnings("static-access")  
	  public static String byteToBase64(byte[] data) {
		  String encodedText="";
	   return encodedText = new String(Base64.encode(data));
	  }  
	  @SuppressWarnings("unused")  
	  public static byte[] base64ToByte(String data) throws Exception {  
	   Base64 base64 = new Base64();  
	   return Base64.decode(data);
	  }  



public byte[] getHash(int iterationNb, String phone_number, byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchProviderException {
	  MessageDigest digest = MessageDigest.getInstance("KECCAK224","IAIK");
    digest.reset();
    digest.update(salt);
    byte[] input = digest.digest(phone_number.getBytes("UTF-8"));
    for (int i = 0; i < iterationNb; i++) {
        digest.reset();
        input = digest.digest(input);
    }
    return input;
}

}
