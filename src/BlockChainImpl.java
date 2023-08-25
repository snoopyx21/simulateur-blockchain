import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import javax.xml.bind.DatatypeConverter;
import java.lang.*;
import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject ;

public class BlockChainImpl 
  extends UnicastRemoteObject
    implements BlockChain
{
	public int difficulty = 5;
	public ArrayList<Block> chain;
	public ArrayList<PublicKey> clients;
	public ArrayList<Transaction> transactions;
	public int positionChain = 0;
	public int positionTransac = 0;
	public ArrayList<Server> neighbors;

	public BlockChainImpl() throws RemoteException
	{
		super();
		this.chain = new ArrayList<Block>();
		this.clients = new ArrayList<PublicKey>();
		this.transactions = new ArrayList<Transaction>();
		this. neighbors = new ArrayList<Server>();
	}

	public Boolean isBlockChainValid() throws RemoteException
	{
		Block currentBlock; 
		Block previousBlock;
		
		for(int i=1; i < this.chain.size(); i++) 
		{
			currentBlock = this.chain.get(i);
			previousBlock = this.chain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) )
			{
				System.out.println("Current Hashes not equal");			
				return false;
			}
			//compare previous hash and registered previous has
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) 
			{
				System.out.println("Previous Hashes not equal");
				return false;
			}
		}
		return true;
	}

	public void addTransaction(Transaction transaction) throws RemoteException
	{
		this.transactions.add(transaction);
	}

	public void addBlock() throws RemoteException
	{
		if (positionChain == 0)
		{
			// add first block 
			this.chain.add(new Block( transactions ,"empty"));
			positionChain++;
		}
		else 
		{
			// add block with previous hash 
			this.chain.add(new Block( transactions , this.chain.get(this.chain.size()-1).hash));
			positionChain++;
		}
	}
	
	public int getDifficulty() throws RemoteException
	{
		return difficulty;
	}

	public void addClient( PublicKey publicKey) throws RemoteException
	{
		this.clients.add(publicKey);
		System.out.println("Adding New Client \n");
		this.transactions.add( new Transaction (publicKey) );
		positionTransac++;

	}

	public PublicKey NewTransacReceiver(String receiver) throws RemoteException
	{
		if (clients.isEmpty() == true)
		{
			return null;
		}
		int i;
		for(i = 0 ; i < this.clients.size() ; i++)
		{
			if ( StringUtil.getStringFromKey( this.clients.get(i).getEncoded() ).equalsIgnoreCase( receiver) == true )
				return this.clients.get(i);
		}
		return null;
	}

	public ArrayList<Transaction> getTransactions() throws RemoteException
	{
		return this.transactions;
	}

	public void setTransactions(ArrayList<Transaction> transac) throws RemoteException
	{
		this.transactions = transac ;
	}

	public ArrayList<Block> getBlockChain() throws RemoteException
	{
		return this.chain;
	}

	public void setBlockChain(ArrayList<Block> blockchains) throws RemoteException
	{
		this.chain = blockchains ;
	}

	public ArrayList<PublicKey> getClientChain() throws RemoteException
	{
		return this.clients;
	}

	public void setClientChain(ArrayList<PublicKey> client) throws RemoteException
	{
		this.clients = client ;
	}
	
	public int newTransaction(PublicKey sender, PrivateKey sendersecret, PublicKey receiver, int value) throws RemoteException
	{
		if (clients.contains(sender) && clients.contains(receiver))
		{
			this.transactions.add( new Transaction (sender, receiver, value) );
			this.transactions.get(this.transactions.size()-1).generateSignature(sendersecret);
			if (this.transactions.get(this.transactions.size()-1).verifiySignature() == true)
				return 0;
			else return 1;
		}
		else return 1;
	}

	public int getPositionBlockChain() throws RemoteException
	{
		return this.positionChain;
	}

	public void StopClient(PublicKey publicKey) throws RemoteException
	{
		if (clients.contains(publicKey))
		{
			this.clients.remove(publicKey);
			System.out.println("Remove " + StringUtil.getStringFromKey(publicKey.getEncoded()) + " : OK \n");
		}
	}

	public int isReceiver(PublicKey publicKey) throws RemoteException
	{
		int i;
		int j = positionTransac;
		if (clients.contains(publicKey))
		{
			for(i = j ; i < this.transactions.size() ; i++, j++)
			{
				if (StringUtil.getStringFromKey( transactions.get(i).reciepient.getEncoded() ).equalsIgnoreCase(StringUtil.getStringFromKey(publicKey.getEncoded())) == true)
				{
					positionTransac++;
					return transactions.get(i).value;
				}			
			}
		}
		return 0;
	}


	public boolean addNeighbor(Server neigh) throws RemoteException
	{
		for (Server neighbor: neighbors)
		{
			if(neighbor.port()==neigh.port())
				return false;
		}
		neighbors.add(neigh);
		return true;
	}
	public void removeNeighbor(Server neigh) throws RemoteException
	{
		if (neighbors.contains((neigh)))
		{
			for (Server neighbor: neighbors)
			{
				if(neighbor.port()==neigh.port())
					neighbors.remove(neigh);
			}
		}
	}

	public void displayBlockChainServer() throws RemoteException
	{
		for(int i =0; i<this.chain.size() ; i++)
		{
			System.out.println( this.chain.get(i).toString() );
		}
	}
}
