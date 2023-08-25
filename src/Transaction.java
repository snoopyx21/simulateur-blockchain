import java.io.Serializable;
import java.security.*;

public class Transaction implements Serializable
{
	public String transactionId; // hash of the transaction.
	public PublicKey sender; // senders address/public key.
	public PublicKey reciepient; // Recipients address/public key.
	public int value;	// value transaction
	public byte[] signature;

	public Transaction(PublicKey from, PublicKey to, int value) 
	{
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.transactionId = calculateHashTransaction();
	}
	// inscription to the server
	public Transaction(PublicKey from)
	{
		this.sender = from;
		this.transactionId = calculateHashInscription();
	}


	// This Calculates the transaction hash (which will be used as its Id)
	private String calculateHashTransaction() 
	{
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender.getEncoded()) 
				+ StringUtil.getStringFromKey(reciepient.getEncoded()) 
				+ Float.toString(value));
	}
	// This Calculates the inscription hash (which will be used as its Id)
	private String calculateHashInscription() 
	{
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender.getEncoded()));
	}

	//Signs all the data we dont wish to be tampered with.
	public void generateSignature(PrivateKey privateKey) 
	{
		try
		{
			String data = StringUtil.getStringFromKey(sender.getEncoded()) + StringUtil.getStringFromKey(reciepient.getEncoded()) + Float.toString(value)	;
			signature = StringUtil.signRSA(data, privateKey);
		}
		catch(InvalidKeyException e)
		{
			System.out.println(e);
		}		
	}
	//Verifies the data we signed hasnt been tampered with
	public boolean verifiySignature() {
		try{
			String data = StringUtil.getStringFromKey(sender.getEncoded()) + StringUtil.getStringFromKey(reciepient.getEncoded()) + Float.toString(value)	;
			return StringUtil.verifyRSA(sender, data, signature);
		}
		catch(InvalidKeyException e)
		{
			System.out.println(e);
		}
		return false;
	}
	//Check if coin belongs to you
	public boolean isMine(PublicKey publicKey) 
	{
		return (publicKey == reciepient);
	}
}
