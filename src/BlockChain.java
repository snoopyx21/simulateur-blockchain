
import java.rmi.*;
import java.security.*;
import java.util.*;

public interface BlockChain extends Remote
{
	public Boolean isBlockChainValid() 
		throws RemoteException;
	public void addTransaction(Transaction transaction) 
		throws RemoteException;
	public void addBlock () 
		throws RemoteException;
	public int getDifficulty() 
		throws RemoteException;
	public void addClient( PublicKey publicKey) 
		throws RemoteException;
	public PublicKey NewTransacReceiver(String receiver) 
		throws RemoteException;
	public ArrayList<Transaction> getTransactions() 
		throws RemoteException;
	public void setTransactions(ArrayList<Transaction> transac) 
		throws RemoteException;
	public ArrayList<Block> getBlockChain()
		throws RemoteException;
	public void setBlockChain(ArrayList<Block> blockchains) 
		throws RemoteException;
	public ArrayList<PublicKey> getClientChain() 
		throws RemoteException;
	public void setClientChain(ArrayList<PublicKey> client) 
		throws RemoteException;
	public int newTransaction(PublicKey sender, PrivateKey sendersecret, PublicKey receiver, int value) 
		throws RemoteException;
	public int getPositionBlockChain() 
		throws RemoteException;
	public void StopClient(PublicKey publicKey)
		throws RemoteException;
	public int isReceiver(PublicKey publicKey) 
		throws RemoteException;
	public boolean addNeighbor(Server neigh) 
		throws RemoteException;
	public void removeNeighbor(Server neigh) 
		throws RemoteException;
	public void displayBlockChainServer() 
		throws RemoteException;
}

