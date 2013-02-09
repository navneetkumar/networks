package edu.uw.cs.cse461.consoleapps;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import edu.uw.cs.cse461.consoleapps.PingInterface.PingTCPMessageHandlerInterface;
import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableConsoleApp;
import edu.uw.cs.cse461.net.tcpmessagehandler.TCPMessageHandlerInterface;
import edu.uw.cs.cse461.service.example.EchoServiceBase;
import edu.uw.cs.cse461.util.ConfigManager;
import edu.uw.cs.cse461.util.Log;
import edu.uw.cs.cse461.util.SampledStatistic.ElapsedTime;
import edu.uw.cs.cse461.util.SampledStatistic.ElapsedTimeInterval;

/**
 * Raw sockets version of ping client.
 * @author zahorjan
 *
 */
public class PingTCPMessageHandler extends NetLoadableConsoleApp implements PingTCPMessageHandlerInterface {
	private static final String TAG="PingRaw";
	
	// ConsoleApp's must have a constructor taking no arguments
	public PingTCPMessageHandler() {
		super("pingtcpmh");
	}
	
	@Override
	public void run() {
		try {
			// Eclipse doesn't support System.console()
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			ConfigManager config = NetBase.theNetBase().config();

			try {

				ElapsedTime.clear();

				String targetIP = config.getProperty("net.server.ip");
				if ( targetIP == null ) {
					System.out.print("Enter the server's ip, or empty line to exit: ");
					targetIP = console.readLine();
					if ( targetIP == null || targetIP.trim().isEmpty() ) return;
				}

				System.out.print("Enter the server's TCP port, or empty line to exit: ");
				String targetTCPPortStr = console.readLine();
				if ( targetTCPPortStr == null || targetTCPPortStr.trim().isEmpty() ) return;
				int targetTCPPort = Integer.parseInt( targetTCPPortStr );

				System.out.print("Enter number of trials: ");
				String trialStr = console.readLine();
				int nTrials = Integer.parseInt(trialStr);

				int socketTimeout = config.getAsInt("net.timeout.socket", 2000);

				if ( targetTCPPort != 0 ) {
	        ElapsedTimeInterval tcpmhResult = ping(EchoServiceBase.HEADER_STR, targetIP, targetTCPPort, socketTimeout, nTrials);
	        if ( tcpmhResult != null ) System.out.println(String.format("%.2f msec (%d failures)", tcpmhResult.mean(), tcpmhResult.nAborted()));
					System.out.println("Raw measurement info: " + ElapsedTime.statString());
				}

			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
			} 
		} catch (Exception e) {
			System.out.println("PingTCPMessageHandler.run() caught exception: " + e.getMessage());
		}
	}
	
	/**
	 * Pings the host/port named by the arguments the number of times named by the argument
	 * and returns an ElapsedTimeInterval object describing the timing and failure results.
	 */
	@Override
	public ElapsedTimeInterval ping(String header, String hostIP, int port, int socketTimeout, int nTrials) throws Exception {
		ElapsedTime.clear("PingTCPMessageHandler_TotalTime");
		for ( int trial=0; trial<nTrials; trial++) {
			TCPMessageHandlerInterface tcpMessageHandlerSocket = null;
			try {
				ElapsedTime.start("PingTCPMessageHandler_TotalTime");
				// perform ping here
				if ( true ) throw new Exception("Code not yet implemented");
				ElapsedTime.stop("PingTCPMessageHandler_TotalTime");
			} catch (Exception e) {
				Log.d(TAG, "TCPMessageHandler read failed: " + e.getMessage());
				ElapsedTime.abort("PingTCPMessageHandler_TotalTime");
			} finally {
				if ( tcpMessageHandlerSocket != null ) try { tcpMessageHandlerSocket.close(); } catch (Exception e) {}
			}
		}
		return ElapsedTime.get("PingTCPMessageHandler_TotalTime");
	}
}
