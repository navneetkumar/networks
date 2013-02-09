package edu.uw.cs.cse461.net.rpc;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public interface RPCCallInterface {

  public JSONObject _invoke(
      String ip,          // ip or dns name of remote host
      int port,                 // port that RPC is listening on on the remote host
      String serviceName,       // name of the remote service
      String method,            // name of that service's method to invoke
      JSONObject userRequest,   // arguments to send to remote method
      int socketTimeout        // max time to wait for reply
      ) throws JSONException, IOException;
}
