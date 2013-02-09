package edu.uw.cs.cse461.net.ddns;

import org.json.JSONObject;

import edu.uw.cs.cse461.httpd.HTTPProviderInterface;
import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableService;
import edu.uw.cs.cse461.net.ddns.DDNSException.DDNSRuntimeException;
import edu.uw.cs.cse461.net.rpc.RPCCallableMethod;
import edu.uw.cs.cse461.net.rpc.RPCServiceInterface;
import edu.uw.cs.cse461.util.Log;

/**
 * Protocol: Based on RPC.  Supports register, unregister, and resolve RPC calls.  
 * 
 * @author zahorjan
 *
 */
public class DDNSService extends NetLoadableService implements HTTPProviderInterface, DDNSServiceInterface {
	private static String TAG="DDNSService";
	
	// variables used to register rpc callable methods
  private RPCCallableMethod resolve;
  private RPCCallableMethod register;
  private RPCCallableMethod unregister;

	/**
	 * Called to end execution.  Specifically, need to terminate any threads we've created.
	 */
	@Override
	public void shutdown() {
	  // Your shutdown code ehre
	}
	
	@Override
	public String httpServe(String[] uriArray) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head>DDNSService</head><body>");
		String zone = NetBase.theNetBase().hostname();
		sb.append("<p>Zone: ");
		if ( zone != null ) sb.append(zone);
		else sb.append("undefined");

		sb.append("<blockquote>");
		// dump your name information here as html
		// OR as text between <pre>...</pre> tags
		sb.append("</blockquote></body></html>");
		return sb.toString();
//		return toString();
	}
	
	/**
	 * Constructor.  Registers the system RPCServerSocket with the parent as
	 * this host's ip address.  Registers the root server and itself in the
	 * local name cache.
	 * @throws DDNSException
	 */
	public DDNSService() throws DDNSException {
		super("ddns");
		
		try {
		  
      RPCServiceInterface rpcService = (RPCServiceInterface)NetBase.theNetBase().getService("rpc");
      if ( rpcService == null ) throw new Exception("DDNSService requires that RPCService be loaded ahead of it.");
		  
      // Your initialization code here (or near here)
      
			//--------------------------------------------------------------
			// set up RPC callable methods
			//--------------------------------------------------------------

			// export methods via the rpc service
			resolve = new RPCCallableMethod(this, "_rpcResolve");
			register = new RPCCallableMethod(this, "_rpcRegister");
			unregister = new RPCCallableMethod(this, "_rpcUnregister");

			rpcService.registerHandler(loadablename(), "register", register );
			rpcService.registerHandler(loadablename(), "unregister", unregister );
			rpcService.registerHandler(loadablename(), "resolve", resolve );
			
		} catch (Exception e) {
			String msg = "DDNSService constructor caught exception: " + e.getMessage();
			Log.e(TAG, msg);
			e.printStackTrace();
			throw new DDNSRuntimeException(msg);
		}
	}
	
	
	//---------------------------------------------------------------------------
	// RPC callable routines
	
	/**
	 * Indicates host is going offline.
	 *      unregister( {name: name, password: password} ) => { status: "OK" } or errormsg
	 * @param args
	 * @return
	 */
	public JSONObject _rpcUnregister(JSONObject args) {
	  // Your code here
	  return null;
	}
	
	/**
	*   _rpcRegister( {name: <string>, password: <string>, ip: <string>,  port: <int>} ) => { DDNSNode } or errormsg
	*<p>
	* We accept only requests for names stored on this server.
	* 
	* @param args
	* @return
	*/
	public JSONObject _rpcRegister(JSONObject args) {
	  // Your code here
	  return null;
	}
	
	/**
	 * This version is invoked via RPC.  It's simply a wrapper that extracts the call arguments
	 * and invokes _resolve(host).
	 * @param args See the description of resolve in the assignment writeup
	 * @return See the description of resolve in the assignment writeup.
	 */
	public JSONObject _rpcResolve(JSONObject args) {
		// Your code here
	  return null;
	}
	
	// RPC callable routines
	//---------------------------------------------------------------------------

	/**
	 * The usual toString.
	 */
	@Override
	public String toString() {
	  // provide a string representing the zone managed by this service
		return "Not yet implemented";
	}

	@Override
	public String dumpState() {
	  return toString();
	}
}
