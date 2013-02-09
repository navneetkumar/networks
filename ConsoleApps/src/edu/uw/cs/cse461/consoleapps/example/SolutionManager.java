package edu.uw.cs.cse461.consoleapps.example;

import edu.uw.cs.cse461.consoleapps.infrastructure.AppManagerBase;

/**
 * Coordinates execution of provided solution code for applications.
 * There must be a server running.  Normally, the server is running on
 * localhost, using config localserver.config.ini, and the client is running
 * on localhost, using config localclient.config.ini.
 * @author zahorjan
 *
 */
public class SolutionManager extends AppManagerBase {
	
	public SolutionManager() throws Exception {
		super("solutionmanager", "solutionmanager.console.apps");
		
	}
	
}
