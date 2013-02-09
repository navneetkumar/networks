package edu.uw.cs.cse461.net.tcpmessagehandler;

import java.io.IOException;
import java.net.Socket;

public interface TCPMessageHandlerServiceInterface {
  /**
   * To wrap an existing (and connected) Socket in a TCPMessageHandler, you should
   * invoke NetBase.theNetBase().getService("tcpmhservice") to get a handle to
   * the loaded TCPMessageHandlerServiceInterface object, and then invoke this
   * method to create a TCPMessageHandlerInterface object wrapping the socket.
   * @param sock The socket to be wrapped.  Must be connected
   * @return A TCPMessageHandlerInterface object.  
   */
  public TCPMessageHandlerInterface createTCPMH(Socket sock) throws IOException;

}
