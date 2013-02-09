package edu.uw.cs.cse461.consoleapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.cs.cse461.consoleapps.DataXferInterface.DataXferRPCInterface;
import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableConsoleApp;
import edu.uw.cs.cse461.service.DataXferServiceBase;
import edu.uw.cs.cse461.util.ConfigManager;
import edu.uw.cs.cse461.util.Log;
import edu.uw.cs.cse461.util.SampledStatistic.TransferRate;
import edu.uw.cs.cse461.util.SampledStatistic.TransferRateInterval;

/**
 * Raw sockets version of ping client.
 * @author zahorjan
 *
 */
public class DataXferRPC extends NetLoadableConsoleApp implements DataXferRPCInterface {
	private static final String TAG="DataXferRPC";

	// ConsoleApp's must have a constructor taking no arguments
	public DataXferRPC() throws Exception {
		super("dataxferrpc");
	}

	/**
	 * This method is invoked each time the infrastructure is asked to launch this application.
	 */
	@Override
	public void run() {
		
		try {

			// Eclipse doesn't support System.console()
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			ConfigManager config = NetBase.theNetBase().config();
			
			int socketTimeout = config.getAsInt("net.timeout.socket", 2000);
			
			String server = config.getProperty("net.server.ip");
			if ( server == null ) {
				System.out.print("Enter the server's ip, or exit to exit: ");
				server = console.readLine();
				if ( server == null ) return;
				if ( server.equals("exit")) return;
			}

			System.out.print("Enter port number, or empty line to exit: ");
			String portStr = console.readLine();
			if ( portStr == null || portStr.trim().isEmpty() ) return;
			int port = Integer.parseInt(portStr);

			System.out.print("Enter number of trials: ");
			String trialStr = console.readLine();
			int nTrials = Integer.parseInt(trialStr);

			System.out.print("Enter amount of data to transfer (-1 to exit): ");
			String xferLengthStr = console.readLine();
			int xferLength = Integer.parseInt(xferLengthStr);
			if ( xferLength < 0 ) return;
			
			System.out.println("Transferring " + xferLength + " bytes from " + server + ":" + port);

			TransferRate.clear();
				
			JSONObject header = new JSONObject().put(DataXferServiceBase.HEADER_TAG_KEY, DataXferServiceBase.HEADER_STR)
												                  .put(DataXferServiceBase.HEADER_LENGTH_KEY, xferLength);
			TransferRateInterval rpcStats = DataXferRate(header, server, port, socketTimeout, nTrials);

			System.out.println("RPC: xfer rate = " + String.format("%9.0f", rpcStats.mean() * 1000.0) + " bytes/sec.");
			System.out.println("RPC: failure rate = " + String.format("%5.1f", rpcStats.failureRate()) +
						           " [" + rpcStats.nAborted()+ "/" + rpcStats.nTrials() + "]");

			
		} catch (Exception e) {
			System.out.println("Unanticipated exception: " + e.getMessage());
		}
	}
	

	/**
	 * Performs actual data transfer using an RPC call.  Doesn't measure performance
	 * @param header The header should be included as under field HEADER_KEY as part of the call args
	 * @param hostIP The IP of the server
	 * @param The port the server is listening for RPC calls on
	 * @param timeout How long to wait for a response before giving up (msec)
	 * @return The transferred bytes
	 */
	@Override
	public byte[] DataXfer(JSONObject header, String hostIP, int port, int timeout) throws JSONException, IOException {
	  // implemenet data xfer here
	  if ( true ) throw new IOException("Not yet implemented");
		return null;
	}
	
	/**
	 * Performs nTrials trials via UDP of a data xfer to host hostIP on port udpPort.  Expects to get xferLength
	 * bytes in total from that host/port.  Is willing to wait up to socketTimeout msec. for new data to arrive.
	 * @return A TransferRateInterval object that measured the total bytes of data received over all trials and
	 * the total time taken.  The measured time should include socket creation time.
	 */
	@Override
	//public TransferRateInterval tcpDataXferRate(String hostIP, int tcpPort, int socketTimeout, int xferLength, int nTrials) {
	public TransferRateInterval DataXferRate(JSONObject header, String hostIP, int port, int timeout, int nTrials) {
		
		if ( !header.has(DataXferServiceBase.HEADER_LENGTH_KEY)) {
			System.err.println("DataXferRate: No length field in header");
			return null;
		}
		
		int xferLength = header.optInt(DataXferServiceBase.HEADER_LENGTH_KEY);

		for ( int trial=0; trial<nTrials; trial++) {
			try {
				TransferRate.start("tcp");
				byte[] result = DataXfer(header, hostIP, port, timeout);
				if ( result == null ) throw new Exception("Result from DataXfer() is null");
				if ( result.length != xferLength ) throw new Exception("Wanted " + xferLength + " bytes, got " + result.length);
				TransferRate.stop("tcp", xferLength);
			} catch (Exception e) {
				TransferRate.abort("tcp", xferLength);
				Log.d(TAG, "TCP trial failed: " + e.getMessage());
			}
		}
		return TransferRate.get("tcp");
	}
	
}
