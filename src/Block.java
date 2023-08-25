import java.util.ArrayList;
import java.io.*;
import java.security.*;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;


public class Block implements Serializable
{
	public String hash;
	public String previousHash;
	public ArrayList<Transaction> transactions;
	private long time; // ms since 01/01/1970
	private int nonce;
	
	public Block(ArrayList<Transaction> transactions , String previousHash)
	{
		this.previousHash = previousHash;
		this.transactions = transactions;
		this.time = new Date().getTime();
		this.hash = calculateHash();
	}

	public String calculateHash()
	{
		String total = StringUtil.applySha256(
			previousHash 
			+ Long.toString(time) 
			+ Integer.toString(nonce));
		return total;
	}

	public String toString()
	{
		String str = "hash : "+ this.hash 
					+ "\nprevious hash : " + this.previousHash
					+ "\ntime : " + Long.toString(this.time)
					+ "\n";
		return str;
	}
	public void mineBlock(int difficulty) 
	{
		//Create a string with difficulty * "0" 
		String target = new String(new char[difficulty]).replace('\0', '0'); 
		while(!hash.substring( 0, difficulty).equals(target)) 
		{
			nonce++;
			hash = calculateHash();
		}
		//System.out.println("Mining block ... \n" + hash);
	}
	
}
