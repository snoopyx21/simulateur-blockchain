import java.rmi.* ; 
import java.security.*;
import java.lang.*;	
import java.util.*;
import java.net.MalformedURLException ; 

public class Client
{
	public static PublicKey publicKey;
	public static PrivateKey privateKey;
	private static int account = 0;
	public static int connect =0;
	
	// thread minor 
	public static Thread Minor = null ;

	// remote object 
	public static BlockChain chain;
	
	public Client()
	{
		generateKeyPair();	
	}
	
	public void generateKeyPair() 
	{
		try
        {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.generateKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
            byte[] hashByte = publicKey.getEncoded();          
            StringBuffer hashHex = new StringBuffer();
            for (int i = 0; i < hashByte.length; i++) 
            {
                String hex = Integer.toHexString(0xff & hashByte[i]);
                if(hex.length() == 1) 
                    hashHex.append('0');
                hashHex.append(hex);
            }
            //System.out.println(hashHex.toString());
        }
        catch(NoSuchAlgorithmException re)
        {
            System.out.println(re) ;
        }
        
	}

	public static PrivateKey getPrivateKey() throws RemoteException
	{
		return privateKey;
	}
	public static PublicKey getPublicKey() throws RemoteException
	{
		return publicKey;
	}

	public static int getAccount() throws RemoteException
	{
		return account;
	}

	// use a thread to mine - can do something else with terminal 
	public static class Mining implements Runnable 
	{
		public void run()
		{
			try
			{	
				System.out.println("Mining START");
				while(!Thread.currentThread().isInterrupted())
				{
					chain.addBlock();
					chain.getBlockChain().get(chain.getPositionBlockChain()-1).mineBlock(chain.getDifficulty());
					// receive for mine
					account += 100;
				}
			}
			catch (RemoteException e) 
			{ 
				System.out.println(e);
			}	
			catch (Exception e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}

			System.out.println("Mining STOP");
		}

	}

	// change server 
	private static boolean change_server(String port) throws InterruptedException 
	{
		boolean error_input = true;

		try
		{
			if (connect == 1) chain.StopClient(getPublicKey());
			chain = (BlockChain) Naming.lookup("rmi://localhost:" + port + "/BlockChain") ; 
			System.out.println("Welcome ! Inscription to the Server " + port +"  \n" );
			chain.addClient( getPublicKey());
			System.out.println("Inscription : OK ! \n");
			connect = 1;

			Mining newMinor = new Mining();
			Minor = new Thread(newMinor);
		}
		catch (NotBoundException re) 
		{ 
			System.out.println(re) ; 
			error_input = false;
		}
		catch (RemoteException re) 
		{ 
			System.out.println(re); 
			error_input = false;
		}
		catch (MalformedURLException e) 
		{ 
			System.out.println(e) ; 
			error_input = false;
		}
		return error_input;
	}

	public static void listClient() throws RemoteException
	{
		if (chain.getClientChain().isEmpty() == true)
		{
			System.out.println("No other PublicKey.\n\n");
			return;
		}
		int i;
		for(i = 0 ; i < chain.getClientChain().size() ; i++)
		{
			System.out.println("\t- "+StringUtil.getStringFromKey( chain.getClientChain().get(i).getEncoded() ) + " \n\n");
		}	
	}


	public static void help_println() throws RemoteException
	{
		System.out.println("\n---------------------------HELP---------------------------");
		System.out.println("\t- help");
		System.out.println("\t- account");
		System.out.println("\t- displayBlockchain");
		System.out.println("\t- my_key");
		System.out.println("\t- verifyBlockchain");
		System.out.println("\t- change_server <port du rmiregistry>");
		System.out.println("\t- transaction");
		System.out.println("\t- mining <start/stop>");
		System.out.println("\t- exit");
		System.out.println("----------------------------------------------------------\n");
	}

	public static void client_actions() throws InterruptedException 
	{
		// try for remote exception 
		try 
		{
			// display help 
			help_println();

			String delims = "[ ]+";
			String[] args;

			String str = null;
			Scanner sc = new Scanner(System.in);

			while(true)
			{
				account += chain.isReceiver(publicKey);

				str = sc.nextLine();
				args = str.split(delims);

				if(args[0].equals("help"))  help_println();
				else if(args[0].equals("my_key"))	System.out.println("\nPublic key  : " + StringUtil.getStringFromKey(getPublicKey().getEncoded()));
				else if(args[0].equals("verifyBlockchain")) 
				{
					if(chain.isBlockChainValid() == true) System.out.println("Blockchain isn't defective : OK\n");
					else System.out.println("BlockChain is defective.\n");
				}
				else if(args[0].equals("account"))  System.out.println("\nAccount  : " + getAccount());
				else if (args[0].equals("displayBlockchain")) chain.displayBlockChainServer();
				else if(args[0].equals("change_server"))
				{
					if(args.length == 2)
					{
						if(change_server(args[1])) Minor.start();
						else 	System.out.println("Usage : can't access to Blockchain");
					}
					else	System.out.println("\tUsage : change_server <port rmiregistry>");
				}

				else if(args[0].equals("mining"))
				{
					if(args[1].equals("start"))	Minor.start();
					else if(args[1].equals("stop"))	Minor.interrupt();
					else	System.out.println("\tUsage : mining <start/stop>");
				}
				else if(args[0].equals("transaction"))
				{
					Scanner sc2 = new Scanner(System.in);
					Scanner sc3 = new Scanner(System.in);

					System.out.println("Receiver ?");
					// display all client 
					listClient();

					String str1 = sc2.nextLine();
					PublicKey receiver = chain.NewTransacReceiver(str1);
					if (receiver == null)
					{
						System.out.println("Usage : Can't recognize this key. \n");
						continue;
					}
					// value transaction
					System.out.println("Value of the transaction ?");						
					String str2 = sc3.nextLine();
					int temp = 0;
					try
					{
						temp = Integer.parseInt(str2);
						if ( temp <= 0 || temp > account)
						{
							System.out.println("Usage : Impossible Value, verify your account or your input.\n");
							continue;
						}
					} 
					catch (NumberFormatException ex) 
					{
						// Not a float 
						System.out.println("Usage : Can't recognize this value. \n");
						continue;
					} 

					if ( chain.newTransaction(getPublicKey(), getPrivateKey(), receiver, temp) == 1)
					{
						System.out.println("Usage : Invalid transaction or Transaction is defective.\n");
					}	
					else
					{
						System.out.println("Transaction OK\n");
						if (StringUtil.getStringFromKey(receiver.getEncoded()).equalsIgnoreCase(StringUtil.getStringFromKey(getPublicKey().getEncoded())) == true)
						{
							account += temp;
						}
						else account -= temp;
					}
					System.out.println("Account : " + account+"\n");
				}
				else if(args[0].equals("exit"))
				{
					chain.StopClient(getPublicKey());
					break;
				}
				else System.out.println("Try again please.\n");
			}
		}
		catch (RemoteException e)
		{
			System.out.println(e);
		}
	}



	public static void main(String [] args)
	{
		if (args.length != 1)
		{
			System.out.println("Usage : java Client <port rmiregistry>") ;
			System.exit(0) ;
		}

		Client MyKey = new Client(); 
		try
		{
			// use change_server to initialize first server 
			if (change_server(args[0]) == false)
			{
				System.out.println("Usage : Invalid Input\n");
				return;
			}
			client_actions();
		}
		catch (InterruptedException re) 
		{ 
			System.out.println(re) ; 
		}
	}
}
