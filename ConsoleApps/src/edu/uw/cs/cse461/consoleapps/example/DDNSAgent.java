package edu.uw.cs.cse461.consoleapps.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableConsoleApp;
import edu.uw.cs.cse461.net.ddns.DDNSException.DDNSNoAddressException;
import edu.uw.cs.cse461.net.ddns.DDNSException.DDNSNoSuchNameException;
import edu.uw.cs.cse461.net.ddns.DDNSFullName;
import edu.uw.cs.cse461.net.ddns.DDNSRRecord;
import edu.uw.cs.cse461.net.ddns.DDNSResolverServiceInterface;

/**
 * This application allows you to make invocations on the resolver
 * using terminal input.  (It's mainly for debugging, obviously.)
 * 
 * @author zahorjan
 *
 */
public class DDNSAgent extends NetLoadableConsoleApp {
	
	/**
	 * Constructor required by NetLoadableConsoleApp interface.  There's nothing to do for this app.
	 */
	public DDNSAgent() {
		super("ddnsagent");
	}

	/**
	 * User can innvoke register, unregister, or resolve.
	 */
	@Override
	public void run() {
		try {
			DDNSResolverServiceInterface ddnsResolver = ((DDNSResolverServiceInterface)NetBase.theNetBase().getService("ddnsresolver"));
			if ( ddnsResolver == null ) throw new Exception("No DDNS resolver running on this node");

			// Eclipse doesn't support System.console()
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			while ( true ) {
				String operationStr = null;
				String nameStr = null;
				String portStr = null;
				DDNSRRecord record = null;
				try {
					System.out.print("Enter operation (register, unregister, or resolve), or exit to exit: ");
					operationStr = console.readLine();
					if ( operationStr == null ) operationStr = "";
					else if ( operationStr.equals("exit")) break;
					
					if ( operationStr.equals("register")) {
						System.out.println("Enter DDNS name");
						nameStr = console.readLine();
						DDNSFullName fullname = new DDNSFullName(nameStr);
						System.out.println("Enter port: ");
						portStr = console.readLine();
						ddnsResolver.register(fullname, Integer.parseInt(portStr));
					}
					else if ( operationStr.equals("unregister")) {
						System.out.println("Enter DDNS name");
						nameStr = console.readLine();
						DDNSFullName fullname = new DDNSFullName(nameStr);
						ddnsResolver.unregister(fullname);
					}
					else if ( operationStr.equals("resolve")) {
						System.out.println("Enter DDNS name");
						nameStr = console.readLine();
						record = ddnsResolver.resolve(nameStr);
						System.out.println( nameStr + ":  [" + record.toString() + "]");
					}
					else System.out.println("Unrecognized DDNSAgent command: " + operationStr);
					
				} catch (DDNSNoAddressException nae) {
					//System.out.println("No address is currently associated with that name");
					System.out.println(nae.getMessage());
				} catch (DDNSNoSuchNameException nsne) {
					System.out.println(nsne.getMessage());
				} catch (Exception e) {
					System.out.println(e.getClass().getName() + " exception: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.out.println("DDNSAgent.run() caught exception: " + e.getMessage());
		}
		
	}
}
