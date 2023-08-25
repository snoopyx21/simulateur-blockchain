import java.rmi.*;
import java.security.*;

public interface Server extends Remote
{
	public String port()
		throws RemoteException ;
}
