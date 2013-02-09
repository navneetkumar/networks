package edu.uw.cs.cse461.consoleapps.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableConsoleApp;
import edu.uw.cs.cse461.net.ddns.DDNSRRecord.ARecord;
import edu.uw.cs.cse461.net.ddns.DDNSResolverServiceInterface;
import edu.uw.cs.cse461.net.rpc.RPCCall;

public class EchoDDNS extends NetLoadableConsoleApp {

	/**
	 * A public constructor taking no arguments is required for loadable apps.
	 */
	public EchoDDNS() {
		super("echoddns");
	}
	
	@Override
	public void run() {
		try {
			// Eclipse doesn't support System.console()
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			
			int timeout = NetBase.theNetBase().config().getAsInt("net.timeout.socket", 500);

			while ( true ) {
				try {
					System.out.print("Enter server's DDNS name, or exit to exit: ");
					String remoteHostName = console.readLine();
					if ( remoteHostName == null ) remoteHostName = "";
					else if ( remoteHostName.equals("exit")) break;

					System.out.print("Enter message to echoed, or empty line to exit: ");
					String msg = console.readLine();
					if ( msg == null || msg.isEmpty() ) break;
					
					
					// resolve DDNS name of remote host to an ip/port.
					DDNSResolverServiceInterface ddnsResolver = (DDNSResolverServiceInterface)NetBase.theNetBase().getService("ddnsresolver");
					if ( ddnsResolver == null ) throw new IOException("RPCCall.invoke() called by there is no local DDNS resolver running");
					ARecord remoteAddr = ddnsResolver.resolve(remoteHostName);

					JSONObject response = RPCCall.invoke(remoteAddr.ip(), remoteAddr.port(), "echorpc", "echo", 
															new JSONObject().put("msg", msg), timeout );
					
					System.out.println(response.getString("msg"));
				} catch (Exception e) {
					System.out.println("Exception: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.out.println("EchoDDNS.run() caught exception: " +e.getMessage());
		}
	}
}
