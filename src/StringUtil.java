import java.lang.*;
import java.security.*;
import java.util.*;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
public class StringUtil 
{
	//Applies SHA256 to a string and returns the result. 
	public static String applySha256(String input){		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			//Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}	

	

	public static String getStringFromKey(byte[] hashByte) 
	{
		StringBuffer hashHex = new StringBuffer();
		for (int i = 0; i < hashByte.length; i++) 
		{
			String hex = Integer.toHexString(0xff & hashByte[i]);
			if(hex.length() == 1) 
				hashHex.append('0');
			hashHex.append(hex);
		}
		return hashHex.toString();
	}
		//return Base64.getEncoder().encodeToString(key.getEncoded());
		/*try
		{
			str = new String( key.getEncoded() , "UTF-8");
			return str; 
		}
		catch(UnsupportedEncodingException e)
		{
			System.out.println(e);
		}
		return str;
	}*/

	//The method that signs the data using the private key that is stored in keyFile path
	public static byte[] signRSA(String data, PrivateKey privateKey) throws InvalidKeyException
	{
		try
		{
			Signature rsa = Signature.getInstance("SHA1withRSA"); 
			rsa.initSign(privateKey);
			rsa.update(data.getBytes());
			return rsa.sign();
		}
		catch (NoSuchAlgorithmException e)
		{
			System.out.println(e);
		}
		catch(SignatureException s)
		{
			System.out.println(s);
		}
		return null;
	}

				
	public static boolean verifyRSA(PublicKey publicKey, String data, byte[] signature) throws InvalidKeyException
	{
		try{
			Signature signature1 = Signature.getInstance("SHA1withRSA");
			signature1.initVerify(publicKey);
			signature1.update(data.getBytes());
			boolean result = signature1.verify(signature);
			return result;
		}		
		catch (NoSuchAlgorithmException e)
		{
			System.out.println(e);
		}
		catch(SignatureException s)
		{
			System.out.println(s);
		}
		return false;
		}

}
