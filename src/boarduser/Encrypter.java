package boarduser;

/*Encryption Class
	encrypt() encode a string msg
	decrypt() decode an encoded string*/

import java.util.Base64;

public class Encrypter {
	public static String encrypt(String msg) {
		byte[] encodedMsg = Base64.getEncoder().encode(msg.getBytes());
		return new String(encodedMsg);
	}

	public static String decrypt(String encodedMsg) {
		byte[] decodedMsg = Base64.getDecoder().decode(encodedMsg);
		return new String(decodedMsg);
	}

}