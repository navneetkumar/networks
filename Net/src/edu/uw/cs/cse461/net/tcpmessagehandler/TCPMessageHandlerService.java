package edu.uw.cs.cse461.net.tcpmessagehandler;

import java.io.IOException;
import java.net.Socket;

import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableService;

public class TCPMessageHandlerService extends NetLoadableService implements TCPMessageHandlerServiceInterface {
  
  public TCPMessageHandlerService() {
    super("tcpmhservice");
  }
  
  /**
   * This method is the reason this class exists.  The loaded service can create and return a
   * TCPMessageHandlerInterface object.  You get either an object executing your code or an object
   * executing the solution code, depending on whether your config file load this 
   * TCPMessageHandlerServiceInterface object or the solution one.
   */
  @Override
  public TCPMessageHandlerInterface createTCPMH(Socket sock) throws IOException {
    return new TCPMessageHandler(sock); 
  }
  
  @Override
  public String dumpState() {
    return "Generating TCPMessageHandlers (i.e., using your code).";
  }

}
