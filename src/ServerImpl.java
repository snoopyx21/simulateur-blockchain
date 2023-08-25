import java.net.* ;
import java.rmi.* ;
import java.util.*;
import java.rmi.server.UnicastRemoteObject ;

public class ServerImpl 
    extends UnicastRemoteObject 
        implements Server
{
    public BlockChainImpl blockchain = new BlockChainImpl();
	private static String port="";
		
	// remote object if neighbor
	public static BlockChain chain;

    public String port() throws RemoteException
    {
        return port;
    }

	public ServerImpl(String port) throws RemoteException
	{
		super();
        try
		{
			Naming.rebind("rmi://localhost:" + port + "/BlockChain" ,blockchain) ;
		}
        catch (MalformedURLException e) 
        { 
            System.out.println(e);
        }
	}
		
    // add new neighbor - use lookup like client RMI 
	private Server addNeighbor(String port) throws InterruptedException 
	{
		Server new_neighbor = null;
		try
		{
			new_neighbor = (Server) Naming.lookup("rmi://localhost:" + port + "/Server") ;
		}
        catch (NotBoundException re) 
        { 
            System.out.println(re) ; 
        }
        catch (RemoteException re) 
        { 
            System.out.println(re); 
        }
        catch (MalformedURLException e) 
        { 
            System.out.println(e) ; 
        }

		return new_neighbor;
	}
		
	private void help_println()
	{
		System.out.println("\n---------------------------HELP---------------------------");
		System.out.println("\t- help");
		System.out.println("\t- exit");
		System.out.println("\t- neighbor <port rmiregistry>");
        System.out.println("----------------------------------------------------------\n");
	}

	public void server_actions()
	{
        // display help 
        help_println();
        
        // delims to catch different args
		String delims = "[ ]+";
        String[] args;
        
        Scanner sc = new Scanner(System.in);
        String str = null;

		while(true)
		{
			str= sc.nextLine();
            args =str.split(delims);
            
			if(args[0].equals("exit")) System.exit(0);
			else if(args[0].equals("help")) help_println();
			else if(args[0].equals("neighbor"))
			{
				if(args.length == 2)
				{
					try
					{
						Server new_neighbor=addNeighbor(args[1]);
						if(new_neighbor!=null)
						{
							blockchain.addNeighbor(new_neighbor);
							// our blockchain change - get server chain 
							chain = (BlockChain) Naming.lookup("rmi://localhost:" + args[1] + "/BlockChain") ;
                            System.out.println("New Neighbor on port "+ args[1]+". \n");
						}
						else System.out.println("Usage : can't access to Blockchain");
					}
					catch (NotBoundException re) 
					{ 
						System.out.println(re) ;
					}
					catch (MalformedURLException e) 
					{ 
						System.out.println(e) ;
					}
                    catch (InterruptedException re) 
                    { 
                        System.out.println(re) ; 
                    }
                    catch (RemoteException re) 
                    { 
                        System.out.println(re); 
                    }
				}
				else	System.out.println("\tUsage : neighbor <port du rmiregistry>");
			}
			else	System.out.println("Try again please.\n");
		}
	}


    public static void main(String [] args)
	{
		if (args.length != 1)
		{
			System.out.println("Usage : java Serveur <port rmiregistry>") ;
			System.exit(0) ;
		}

		try
		{
            port = args[0];
			ServerImpl server;
			server = new ServerImpl(port) ;
			Naming.rebind("rmi://localhost:" + port + "/Server" ,server) ;
			System.out.println("WELCOME \n") ;
            server.server_actions();
		}
        catch (RemoteException re) 
        { 
            System.out.println(re);
        }
        catch (MalformedURLException e) 
        { 
            System.out.println(e);
        }


	}
}