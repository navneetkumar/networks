package edu.uw.cs.cse461.consoleapps.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableConsoleApp;
import edu.uw.cs.cse461.util.ConfigManager;

/**
 * Base class for console shells (which we call app managers).  Each
 * is responsible for a subset of applications.  Their is a single,
 * global set of loaded apps as far as NetBase is concerend, so each
 * app manager keeps track of the apps it should offer.
 * 
 * @author zahorjan
 *
 */
public class AppManagerBase extends NetLoadableConsoleApp {

  protected ArrayList<String> mAppNameVec;

  protected AppManagerBase(String appname, String appField) throws Exception {
    super(appname);
    
    mAppNameVec  = new ArrayList<String>();

    // load apps managed by the testdriver
    ConfigManager config = NetBase.theNetBase().config();
    String[] testAppClassNames = config.getAsStringVec(appField);
    if ( testAppClassNames == null ) throw new Exception("Required config file field " + appField + " isn't defined"); 
    for ( String appClass : testAppClassNames ) {
      String appName = NetBase.theNetBase().loadApp(appClass); 
      if ( appName == null ) continue;
      mAppNameVec.add(appName);
    }
    Collections.sort(mAppNameVec);
  }
  
  private void _listApps() {
    for ( String appName : mAppNameVec ) {
      System.out.println(" * " + appName);
    }
    System.out.println("Type exit to exit");
  }
  
  @Override
  public void run() throws IOException {
    // Eclipse doesn't support System.console()
    BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    while(true) {
      System.out.print(loadablename() + "> ");
      String appName = console.readLine();
      if (appName == null) break;  // this is EOF, at least in Eclipse.  If we don't break, we go into an infinite loop
      appName = appName.trim();
      if (appName.isEmpty() ) {
        _listApps();
        continue;
      }
      if ( appName.equalsIgnoreCase("exit") ) break;

      // verify that we're managing this app.  (It's confusing if you mistakenly type the name of 
      // any old app and it actually runs under test driver
      if ( !mAppNameVec.contains(appName) ) {
        System.out.println(appName + " is not managed by " + loadablename());
        _listApps();
        continue;
      }
      
      // run the requested app
      try {
        NetBase.theNetBase().startApp(appName);
      } catch (Exception e) {
        System.out.println("Test code threw exception: " + e.getMessage());
      }
    }
  }
}
