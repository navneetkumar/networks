package edu.uw.cs.cse461.net.ddns;

import org.json.JSONException;

import edu.uw.cs.cse461.httpd.HTTPProviderInterface;
import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableService;
import edu.uw.cs.cse461.net.ddns.DDNSRRecord.ARecord;
import edu.uw.cs.cse461.util.Log;


public class DDNSResolverService extends NetLoadableService implements HTTPProviderInterface, DDNSResolverServiceInterface {
	private static String TAG="DDNSResolverService";
	
	private static final int DEFAULTCACHETIMEOUT = 60000; // 1 minute
	
	private String mDDNSServicePassword;
	private int mMaxServerTTL;   // maximum number of DDNS servers to visit for any one resolve request (or the like)
  private long mCacheTimeout;  // how long a cached node is kept in the cache, in seconds
	
	/**
	 * Called to end execution.  Specifically, need to terminate any threads we've created.
	 */
	@Override
	public void shutdown() {
		super.shutdown();
		// Your shutdown code here
	}
	
	/**
	 * Serves web pages.  The 0th element of uriArray is always null.
	 * The next element names this service ("ddnsresolver").  The optional third
	 * component is a name to be resolved before dumping the cache.
	 * You're free to change what is produced for the web page.
	 */
	@Override
	public String httpServe(String[] uriArray) {
		StringBuilder sb = new StringBuilder();
		sb.append("Host:  ").append(NetBase.theNetBase().hostname()).append("\n");
		if ( uriArray.length > 2 ) {
			sb.append("Resolving: ").append(uriArray[2]).append("\n");
			// third component
			ARecord result = null;
			try {
				result = resolve(uriArray[2]);
				sb.append("Got: [").append(result).append("]\n");
			} catch (Exception e) {
				sb.append("Got exception: ").append(e.getMessage()).append("\n");
			}
		}
		sb.append("\nCache contents:\n");
		sb.append(toString());  
		return sb.toString();
	}
	
	/**
	 * DDNSResolver constructor.
	 * @throws DDNSException
	 */
	public DDNSResolverService() throws DDNSException {
		super("ddnsresolver");
		
		// Your initialization code goes somewhere in here
		
		//--------------------------------------------------------------
		// See if I have a password
		//--------------------------------------------------------------

		mDDNSServicePassword = NetBase.theNetBase().config().getProperty("ddns.resolver.password");
		if ( mDDNSServicePassword == null ) {
			Log.w(TAG, "No valid ddnsresolver.password entry in config file.  Using null string password.");
			mDDNSServicePassword = "";  // try using no password
		}
		
		//--------------------------------------------------------------
		// establish resolution TTL
		//--------------------------------------------------------------
		
		mMaxServerTTL = NetBase.theNetBase().config().getAsInt("ddns.resolver.serverttl", 5, 1, 100);
		
		//--------------------------------------------------------------
		// fetch cachettl info
		//		ddnsresolver.cachettl == 0 means don't cache.
		//--------------------------------------------------------------

		mCacheTimeout = NetBase.theNetBase().config().getAsInt("ddns.resolver.cache.timeout", DEFAULTCACHETIMEOUT, 0);
		
	}
	
	/**
	 * Provides access to the DDNS password needed to update DDNS registrations.
	 */
	@Override
	public String getDDNSPassword() {
		return mDDNSServicePassword; 
	}
		
	/**
	 * Unregisters a name, using the default password 
	 * @param name
	 * @throws DDNSException
	 */
	@Override
	public void unregister(DDNSFullNameInterface name) throws DDNSException, JSONException {
		// Your code here
	  return;
	}
	
	/**
	 * Another unregister interface, this one allowing the caller to specify a password
	 * and to give the internal name of the service to contact.  (The normal DDNS service
	 * has name "ddns", for example.)  Used by the DDNS proxy service.
	 */
	@Override
	public void unregister(DDNSFullNameInterface name, String ddnsPassword, String ddnsServiceName) throws DDNSException, JSONException {
	  // Your code here
	  return;
	}
	
	/**
	 * Registers a name as being on this host (IP) at the given port.
	 * If the name already exists, update its address mapping.  If it doesn't exist, create it (as an ARecord).
	 * @param name
	 * @param port
	 * @throws DDNSException
	 */
	@Override
	public void register(DDNSFullNameInterface name, int port) throws DDNSException {
		// Your code here
	}
	
	@Override
	public void register(DDNSFullNameInterface name, int port, String password, String ddnsServiceName) throws DDNSException {
	  // Your code here
	  return;
	}
	
	/**
	 * Resolves a name to an ARecord containing an address.  Throws an exception if no ARecord w/ address can be found.
	 * @param name The name to resolve
	 * @return The ARecord for the name, if one is found
	 * @throws DDNSException
	 */
	@Override
	public ARecord resolve(String nameStr) throws DDNSException, JSONException {
		return resolve(nameStr, "ddns");
	}
	
	@Override
	public ARecord resolve(String nameStr, String ddnsServiceName) throws DDNSException, JSONException {
		// Your code here
		return null;
	}
	
	@Override
	public String dumpState() {
		return toString();
	}

	@Override
	public String toString() {
	  // Your code here
		return "Not yet implemented";
	}
}
