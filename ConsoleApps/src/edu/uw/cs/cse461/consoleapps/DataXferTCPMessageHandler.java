package edu.uw.cs.cse461.consoleapps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;

import edu.uw.cs.cse461.consoleapps.DataXferInterface.DataXferTCPMessageHandlerInterface;
import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableConsoleApp;
import edu.uw.cs.cse461.service.DataXferServiceBase;
import edu.uw.cs.cse461.util.ConfigManager;
import edu.uw.cs.cse461.util.SampledStatistic.TransferRate;
import edu.uw.cs.cse461.util.SampledStatistic.TransferRateInterval;

/**
 * Raw sockets version of ping client.
 * @author zahorjan
 *
 */
public class DataXferTCPMessageHandler extends NetLoadableConsoleApp implements DataXferTCPMessageHandlerInterface {
  private static final String TAG="DataXferTCPMessageHandler";

  // ConsoleApp's must have a constructor taking no arguments
  public DataXferTCPMessageHandler() throws Exception {
    super("dataxfertcpmh");
  }

  /**
   * This method is invoked each time the infrastructure is asked to launch this application.
   */
  @Override
  public void run() {

    try {

      // Eclipse doesn't support System.console()
      BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

      ConfigManager config = NetBase.theNetBase().config();
      String serverIP = config.getProperty("net.server.ip");
      if ( serverIP == null ) {
        System.out.print("Enter a host ip, or exit to exit: ");
        serverIP = console.readLine();
        if ( serverIP == null ) return;
        if ( serverIP.equals("exit")) return;
      } else System.out.print("Server location: " + serverIP + "\n");

      System.out.print("Enter port number, or empty line to exit: ");
      String portStr = console.readLine();
      if ( portStr == null || portStr.trim().isEmpty() ) return;
      int port = Integer.parseInt(portStr);

      int socketTimeout = config.getAsInt("net.timeout.socket", -1);
      if ( socketTimeout < 0 ) {
        System.out.print("Enter socket timeout (in msec.): ");
        String timeoutStr = console.readLine();
        socketTimeout = Integer.parseInt(timeoutStr);

      }

      System.out.print("Enter number of trials: ");
      String trialStr = console.readLine();
      int nTrials = Integer.parseInt(trialStr);

      int xferLength = -1; 
      do {
        System.out.print("Enter amount of data to transfer (-1 to exit): ");
        String xferLengthStr = console.readLine();
        xferLength = Integer.parseInt(xferLengthStr);
        if ( xferLength < 0) break;

        System.out.println("\n" + xferLength + " bytes");

        //-----------------------------------------------------
        // TCP transfer
        //-----------------------------------------------------

        TransferRateInterval tcpStats = DataXferRate(DataXferServiceBase.HEADER_STR, serverIP, port, socketTimeout, xferLength, nTrials);

        System.out.println("\nxfer rate = " + String.format("%9.0f", tcpStats.mean() * 1000.0) + " bytes/sec.");
        System.out.println("failure rate = " + String.format("%5.1f", tcpStats.failureRate()) +
                           " [" + tcpStats.nAborted()+ "/" + tcpStats.nTrials() + "]");
      } while ( xferLength >= 0 );

    } catch (Exception e) {
      System.out.println("Unanticipated exception: " + e.getMessage());
    }
  }

  /**
   * Reads data as TCPMesssageHandler messages.
   * @param hostIP Server's IP address
   * @param tcpPort Port server is listening on
   * @param socketTimeout How long to wait for a response before giving up (msec)
   * @param xferLength  The number of bytes to transfer
   * @return byte[] with all requested bytes
   */
  @Override
  public byte[] DataXfer(String header, String hostIP, int tcpPort, int socketTimeout, int xferLength) throws JSONException, IOException {
    // perform data xfer here
    if ( true ) throw new IOException("Not yet implemented");
    return null;
  }

  /**
   * Performs nTrials trials via UDP of a data xfer to host hostIP on port udpPort.  Expects to get xferLength
   * bytes in total from that host/port.  Is willing to wait up to socketTimeout msec. for new data to arrive.
   * @return A TransferRateInterval object that measured the total bytes of data received over all trials and
   * the total time taken.  The measured time should include socket creation time.
   */
  @Override
  public TransferRateInterval DataXferRate(String header, String hostIP, int tcpPort, int socketTimeout, int xferLength, int nTrials) {
    String timerKey = "dataxfertcpmh";
    TransferRate.clear(timerKey);
    for ( int trial=0; trial<nTrials; trial++) {
      try {
        TransferRate.start("tcp");
        DataXfer( header, hostIP, tcpPort, socketTimeout, xferLength );
        TransferRate.stop("tcp", xferLength);
      } catch (Exception e) {
        TransferRate.abort("tcp", xferLength);
      }
    }

    return TransferRate.get("tcp");
  }
}
