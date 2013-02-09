package edu.uw.cs.cse461.net.rpc;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableService;
import edu.uw.cs.cse461.util.IPFinder;
import edu.uw.cs.cse461.util.Log;

/**
 * Implements the side of RPC that receives remote invocation requests.
 * 
 * @author zahorjan
 *
 */
public class RPCService extends NetLoadableService implements RPCServiceInterface {
	private static final String TAG="RPCService";
	
	ServerSocket mServerSocket = null;

	/**
	 * Constructor.  Creates the Java ServerSocket and binds it to a port.
	 * If the config file specifies an rpc.server.port value, it should be bound to that port.
	 * Otherwise, you should specify port 0, meaning the operating system should choose a currently unused port.
	 * <p>
	 * Once the port is created, a thread needs to be spun up to listen for connections on it.
	 * 
	 * @throws Exception
	 */
	public RPCService() throws Exception {
		super("rpc");

		String serverIP = IPFinder.localIP();
		int port = NetBase.theNetBase().config().getAsInt("rpc.server.port", 0);
		// if the port is 0, let the OS choose.  Otherwise, bind this socket to 
		// the port given in the config file.
		if ( serverIP == null || serverIP.trim().isEmpty() ) mServerSocket = new ServerSocket(port);
		else {
			mServerSocket = new ServerSocket();
			mServerSocket.bind(new InetSocketAddress(serverIP, port));
		}
		Log.i(TAG, "Server socket = " + mServerSocket.getLocalSocketAddress());
		
		// Set some socket options.
        // setReuseAddress lets you reuse a server port immediately after terminating
		// an application that has used it.  (Normally that port is unavailable for a while, for reasons we'll see
		// later in the course.
		mServerSocket.setReuseAddress(true); // allow port number to be reused immediately after close of this socket

		// setSoTimeout causes a thread waiting for connections to receive a timeout exception if no connection
		// is made before the timeout interval expires, instead of waiting forever.
		int socketTimeout = NetBase.theNetBase().config().getAsInt("net.timeout.granularity", 500);  // default is 1/2 second
		mServerSocket.setSoTimeout(socketTimeout); // well, we have to wake up every once and a while to check for program termination
		
		// start a thread to wait for and handle incoming calls
		// YOU NEED TO IMPLEMENT THIS
	}
	
	/**
	 * Services and applications with RPC callable methods register them with the RPC service using this routine.
	 * Those methods are then invoked as callbacks when an remote RPC request for them arrives.
	 * @param serviceName  The name of the service.
	 * @param methodName  The external, well-known name of the service's method to call
	 * @param method The descriptor allowing invocation of the Java method implementing the call
	 * @throws Exception
	 */
	@Override
	public synchronized void registerHandler(String serviceName, String methodName, RPCCallableMethod method) throws Exception {
	  // YOU NEED TO IMPLEMENT THIS
	}
	
	/**
	 * Some of the testing code needs to retrieve the current registration for a particular service and method,
	 * so this interface is required.  You probably won't find a use for it in your code, though.
	 * 
	 * @param serviceName  The service name
	 * @param methodName The method name
	 * @return The existing registration for that method of that service, or null if no registration exists.
	 */
	public RPCCallableMethod getRegistrationFor( String serviceName, String methodName) {
	  // YOU NEED TO IMPLEMENT THIS
		return null;
	}
	
	/**
	 * Returns the port to which the RPC ServerSocket is bound.
	 * @return The RPC service's port number on this node
	 */
	@Override
	public int localPort() {
		return mServerSocket.getLocalPort();
	}
	
	@Override
	public String dumpState() {
	  // IT'S WORTH THE TIME TO IMPLEMENT SOMETHING HERE, FOR DEBUGGING
	  return "dumpState() not yet implemented";
	}
}
