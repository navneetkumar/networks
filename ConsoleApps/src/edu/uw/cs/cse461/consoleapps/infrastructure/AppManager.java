package edu.uw.cs.cse461.consoleapps.infrastructure;


/**
 * An AppManager acts someting like a shell on a traditional system - it lets
 * you run apps (OSConsoleApps).  Unlike traditional systems, but something
 * like Android, there is only one instance of an app created, no matter how
 * many times it's invoked.  In this system, all apps are loaded when the 
 * OS boots.  An app invocation is merely a call to its run() method.
 * The AppManager   
 * @author zahorjan
 *
 */
public class AppManager extends AppManagerBase {
	
	/**
	 * Constructor required by OSConsoleApp.
	 */
	public AppManager() throws Exception {
		super("appmanager", "console.apps");
	}
}
