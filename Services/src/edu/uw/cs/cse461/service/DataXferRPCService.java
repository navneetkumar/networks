package edu.uw.cs.cse461.service;

import java.io.IOException;

/**
 * Sends back the number of bytes requested.
 * @author zahorjan
 *
 */
public class DataXferRPCService extends DataXferServiceBase  {
	private final String TAG="DataXferRPCService"; 
	
	/**
	 * Key used for DataXferRPC's header, in the args of an RPC call.
	 * The header element is a JSONObject containing the tag ("xfer")
	 * and the requested xfer length.
	 */
	public static final String HEADER_KEY = "header";
	public static final String HEADER_TAG_KEY = "tag";
	public static final String HEADER_LENGTH_KEY = "xferLength";
	
	/**
	 * The constructor registers RPC-callable methods with the RPCService.
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public DataXferRPCService() throws Exception {
		super("dataxferrpc");
		// Register your RPC callback here...
	}
	
  //--------
	// implement the callback function...
  //--------
	
  /**
   * Returns string summarizing the status of this server.  The string is printed by the dumpservicestate
   * console application, and is also available by executing dumpservicestate through the web interface.
   */
  @Override
  public String dumpState() {
    // You'll want to print the IP and port this server is listening 
    return "dumpState() not yet implemented";
  }
}
