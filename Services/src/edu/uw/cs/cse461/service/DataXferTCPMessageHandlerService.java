package edu.uw.cs.cse461.service;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableService;
import edu.uw.cs.cse461.net.tcpmessagehandler.TCPMessageHandler;
import edu.uw.cs.cse461.service.DataXferServiceBase;
import edu.uw.cs.cse461.util.IPFinder;
import edu.uw.cs.cse461.util.Log;

/**
 * An DataXfer service that uses TCPMessageHandler messsages.
 * @author zahorjan
 *
 */
public class DataXferTCPMessageHandlerService extends NetLoadableService  {
	private static final String TAG="DataXferTCPMHService";

	private static final int RESPONSE_TRANSFER_CHUNK_SIZE = 1000;

	private ServerSocket mServerSocket;

	public DataXferTCPMessageHandlerService() throws Exception {
		super("dataxfertcpmh");

		// You need to create a server socket and start a thread that waits for connections.
		// The thread handles the bulk of the DataXferTCPMessageHandlerService.  (You need
		// to write the code the thread executes.)
	}

  /**
   * Returns string summarizing the status of this server.  The string is printed by the dumpservicestate
   * console application, and is also available by executing dumpservicestate through the web interface.
   */
	@Override
	public String dumpState() {
		return "dumpState() not yet implemented";
	}
}
