package edu.uw.cs.cse461.consoleapps.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableConsoleApp;
import edu.uw.cs.cse461.net.ddns.DDNSException.DDNSNoAddressException;
import edu.uw.cs.cse461.net.ddns.DDNSException.DDNSNoSuchNameException;
import edu.uw.cs.cse461.net.ddns.DDNSRRecord;
import edu.uw.cs.cse461.net.ddns.DDNSResolverServiceInterface;

/**
 * This application resolves a name and displays the resulting resource record.
 * It can be used to test and debug DDNS implementations.
 * 
 * @author zahorjan
 *
 */
public class Nslookup extends NetLoadableConsoleApp {
	
	/**
	 * Constructor required by NetLoadableConsoleApp interface.  There's nothing to do for this app.
	 */
	public Nslookup() {
		super("nslookup");
	}

	/**
	 * Fetches the resource record for a name, using the locally running DDNS name resolver service.
	 * (All systems are required to run a name resolver.)
	 */
	@Override
	public void run() {
		try {
			// Eclipse doesn't support System.console()
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			while ( true ) {
				String targetStr = null;
				DDNSRRecord record = null;
				try {
					System.out.print("Enter a host name, or exit to exit: ");
					targetStr = console.readLine();
					if ( targetStr == null ) targetStr = "";
					else if ( targetStr.equals("exit")) break;
					DDNSResolverServiceInterface ddnsResolver = ((DDNSResolverServiceInterface)NetBase.theNetBase().getService("ddnsresolver"));
					if ( ddnsResolver == null ) throw new Exception("No DDNS resolver running on this node");
					record = ddnsResolver.resolve(targetStr);
					System.out.println( targetStr + ":  [" + record.toString() + "]");
				} catch (DDNSNoAddressException nae) {
					System.out.println("No address is currently associated with that name");
				} catch (DDNSNoSuchNameException nsne) {
					System.out.println("No such name: " + targetStr);
				} catch (Exception e) {
					System.out.println("Exception: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.out.println("EchoConsole.run() caught exception: " + e.getMessage());
		}
		
	}
}
