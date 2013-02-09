package edu.uw.cs.cse461.consoleapps;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONObject;

import edu.uw.cs.cse461.consoleapps.PingInterface.PingRPCInterface;
import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableConsoleApp;
import edu.uw.cs.cse461.service.example.EchoRPCService;
import edu.uw.cs.cse461.util.ConfigManager;
import edu.uw.cs.cse461.util.SampledStatistic.ElapsedTime;
import edu.uw.cs.cse461.util.SampledStatistic.ElapsedTimeInterval;

public class PingRPC extends NetLoadableConsoleApp implements PingRPCInterface {
	private static final String TAG="PingRPC";
	
	// OSConsoleApp's must have a constructor taking no arguments
	public PingRPC() {
		super("pingrpc");
	}
	
	@Override
	public void run() {
		try {
			ElapsedTime.clear();

			// Eclipse doesn't support System.console()
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			ConfigManager config = NetBase.theNetBase().config();
			
			int socketTimeout = config.getAsInt("net.timeout.socket", 500);

			String targetIP = config.getProperty("net.server.ip");
			if ( targetIP == null ) {
				System.out.print("Enter the server's ip, or an empty line to exit: ");
				targetIP = console.readLine();
				if ( targetIP == null || targetIP.trim().isEmpty() ) return;
			}

			System.out.print("Enter the RPC port, or empty line to exit: ");
			String targetPortStr = console.readLine();
			if ( targetPortStr == null || targetPortStr.trim().isEmpty() ) return;
			int targetPort = Integer.parseInt(targetPortStr);


			System.out.print("Enter number of trials: ");
			String trialStr = console.readLine();
			int nTrials = Integer.parseInt(trialStr);

			JSONObject header = new JSONObject().put(EchoRPCService.HEADER_TAG_KEY, EchoRPCService.HEADER_STR);
			ElapsedTimeInterval result = ping(header, targetIP, targetPort, socketTimeout, nTrials);
			
			if ( result != null ) {
			  System.out.println(String.format("%.2f msec (%d failures)", result.mean(), result.nAborted()));
			  System.out.println( "Raw timing info: " + ElapsedTime.statString());
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	@Override
	public ElapsedTimeInterval ping(JSONObject header, String hostIP, int port, int timeout, int nTrials) throws Exception {
	  final String timerKey = "PingRPC_invoke";
	  
		ElapsedTime.clear(timerKey);
		for ( int trial=0; trial<nTrials; trial++ ) {
		  try {
		    ElapsedTime.start(timerKey);
		    // perform pings
		    if ( true ) throw new Exception("Not yet implemented");
		    ElapsedTime.stop(timerKey);
		  } catch (Exception e) {
		    ElapsedTime.abort(timerKey);
		  }
		}
		return ElapsedTime.get(timerKey);
	}
	
}
