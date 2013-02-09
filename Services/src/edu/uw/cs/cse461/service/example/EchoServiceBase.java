package edu.uw.cs.cse461.service.example;

import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableService;


public abstract class EchoServiceBase  extends NetLoadableService {
	
	public static final int MAX_ECHO_LENGTH = 10*1024*1024;  // If you reach 10MB, stop echo'ing and give up
	
  //---------------------------------------------------------------------------------------
  // Echo header information

	/**
	 * raw and tcpmh header information
	 */
	public static final String HEADER_STR = "echo";
	public static final byte[] HEADER_BYTES = HEADER_STR.getBytes();
	public static final int HEADER_LEN = HEADER_BYTES.length;

	public static final String RESPONSE_OKAY_STR = "okay";
	public static final byte[] RESPONSE_OKAY_BYTES = RESPONSE_OKAY_STR.getBytes();
	public static final int RESPONSE_LEN = RESPONSE_OKAY_BYTES.length;
	
	 /**
   * Key used for EchoRPC's header, in the args of an RPC call.
   * The header element is a string (EchoServiceBase.HEADER_STR).
   */
  public static final String HEADER_KEY = "header";
  public static final String HEADER_TAG_KEY = "tag";

  /**
   * Key used for EchoRPC's payload, in the args of an RPC call
   */
  public static final String PAYLOAD_KEY = "payload";
  
  // Echo header information
  //---------------------------------------------------------------------------------------
  
  /**
	 * Pass subclass's loadable name up to base class.
	 */
	protected EchoServiceBase(String loadablename) {
		super(loadablename);
	}

	@Override
	public String dumpState() {
		return loadablename() + (mAmShutdown ? " is down" : " is up");
	}
	
}
