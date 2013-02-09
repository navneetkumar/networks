package edu.uw.cs.cse461.net.ddns;

import edu.uw.cs.cse461.net.ddns.DDNSRRecord.ARecord;

/**
 * This Interface documents the public methods implemented by the DDNS resolver.
 * The major methods are resolve(), register(), and unregister().  There is more 
 * than one implementation of each - most of the time, this class should handle
 * "details" like providing the correct password on a register call.  But, for grading
 * and other class related reasons, we sometimes want to be able to control those details.
 * (The more natural implementations simply fill in the default values for the details
 * and call the detailed interface.)
 * @author zahorjan
 *
 */
public interface DDNSResolverServiceInterface {

	/**
	 * A DDNS password is normally given in the config file.  This method knows what the
	 * field name is for it.
	 * @return The DDNS password for names controlled by this node.
	 */
	public String getDDNSPassword();  

	/**
	 * Attempts to resolve a name to an address.  Invokes the more detailed resolve with
	 * ddnsServiceName set to "ddns".
	 * @param nameStr The name to be resolved.
	 * @return The resolved address.
	 * @throws Exception Throws DDNSNoSuchName if the name doesn't exist.  Throws DDNSNoAddress
	 * if the name exists but there is no address currently registered for it.
	 */
	public ARecord resolve(String nameStr) throws Exception;
	
	/**
	 * Attempts to resolve a DDNS name to an address.  First consults the resolver's
	 * cache, and returns a result if one is found.  Otherwise, begins name resolution
	 * at the DDNS root, whose location must be given in the configuration file.
	 * @param nameStr  DDNS name to be resolved to an IP/port.
	 * @param ddnsServiceName The service name to use in the underlying RPC calls (e.g., "ddns").
	 * @return The resolved address
	 * @throws Exception 
	 */
	public ARecord resolve(String nameStr, String ddnsServiceName) throws Exception;

	//----------------------------------------------------------------------------------------
	// register methods
	//----------------------------------------------------------------------------------------
	
	/**
	 * The usual case is that you want to register a name you own.  The IP is that of the local
	 * node.  The password is known to the DDNSResolver instance, so just supply the name and port.
	 * @param name The DDNS name to be registered.
	 * @param port The port to register
	 * @throws Exception
	 */
	public void register(DDNSFullNameInterface name, int port) throws Exception;  // uses password
	
	/**
	 * When operating as a proxy, we need to register our address for a name that doesn't
	 * belong to us.  The owner of the name must supply the password.
	 * <p>
	 * The test code needs to contact a shim server inserted in front of the remote DNS service.
	 * To do that, we allo a specific RPC service name to be supplied.
	 * @param name The DDNS name to be registered
	 * @param port The port to register. (The IP is the IP of this node.)
	 * @param ddnsPassword The DDNS password to use
	 * @param ddnsServiceName Usually "ddns" but may be set to a distinct name for test code purposes.
	 * @throws Exception
	 */
	public void register(DDNSFullNameInterface name, int port, String ddnsPassword, String ddnsServiceName) throws Exception;

	
	//----------------------------------------------------------------------------------------
	// unregister methods
	//----------------------------------------------------------------------------------------
	
	public void unregister(DDNSFullNameInterface name) throws Exception;
	
	public void unregister(DDNSFullNameInterface name, String ddnsPassword, String ddnsServiceName) throws Exception;
	
}
