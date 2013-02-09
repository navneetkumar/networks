package edu.uw.cs.cse461.net.tcpmessagehandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.cs.cse461.net.base.NetBase;


/**
 * Sends/receives a message over an established TCP connection.
 * To be a message means the unit of write/read is demarcated in some way.
 * In this implementation, that's done by prefixing the data with a 4-byte
 * length field.
 * <p>
 * Design note: TCPMessageHandler cannot usefully subclass Socket, but rather must
 * wrap an existing Socket, because servers must use ServerSocket.accept(), which
 * returns a Socket that must then be turned into a TCPMessageHandler.
 *  
 * @author zahorjan
 *
 */
public class TCPMessageHandler implements TCPMessageHandlerInterface {
	private static final String TAG="TCPMessageHandler";
	// The maxmsglength is the maximum number of bytes of data in the TCPMessageHandler message.
	// The default gives a total length (including the header) of 2MB.
	private static final int DEFAULT_MAXMSGLENGTH = 2 * 1024 * 1024 - 4;
	private static final int DEFAULT_SOCKET_TIMEOUT = 10000;  // 10 seconds
	private static final int DEFAULT_TIMEOUT_GRANULARITY = 500; // 1/2 second
	
	protected int mMaxMsgLength;
	protected int mTimeout;             // this is how long to wait, in all, for the next data byte to arrive
	protected int mTimeoutGranularity;  // how often to wake up from trying to read from underlying socket
	
	protected Socket mSock;
	protected InputStream mIS;
	protected OutputStream mOS;
	
	//--------------------------------------------------------------------------------------
	// helper routines
	//--------------------------------------------------------------------------------------

	/**
	 * We need an "on the wire" format for a binary integer.
	 * This method encodes into that format, which is little endian
	 * (low order bits of int are in element [0] of byte array, etc.).
	 * @param i
	 * @return A byte[4] encoding the integer argument.
	 */
	protected static byte[] intToByte(int i) {
	  // You need to implement this method.  It's operation
	  // is the inverse of byteToInt().
		byte buf[] = {0, 0, 0, 0};  // FIX ME
		return buf;
	}
	
	/**
	 * We need an "on the wire" format for a binary integer.
	 * This method decodes from that format, which is little endian
	 * (low order bits of int are in element [0] of byte array, etc.).
	 * @param buf
	 * @return 
	 */
	protected static int byteToInt(byte buf[]) {
		ByteBuffer b = ByteBuffer.wrap(buf);
		b.order(ByteOrder.LITTLE_ENDIAN);
		int result = b.getInt();
		return result;
	}
	
	/**
	 * Constructor, associating this TCPMessageHandler with a connected socket. The constructor
	 * is package protected, to prevent use by client code.  Client code should fetch the loaded
	 * tcpmhservice service (using NetBase.theNetBase.getService()) and invoke its createTCPMH()
	 * method to construct a tcp message handler.
	 * @param sock A connected socket.
	 * @throws IOException
	 */
	 TCPMessageHandler(Socket sock) throws IOException {
		if ( sock == null) throw new IOException("TCPMessageHandler constructor: socket argument is null");
		if ( !sock.isConnected() ) throw new IOException("TCPMessageHandler constructor: socket argument isn't in connected state");
		
		// This is the default value.  It can over-ridden by an explicit call to setMaxReadLength().
		mMaxMsgLength = NetBase.theNetBase().config().getAsInt("tcpmessagehandler.maxmsglength", DEFAULT_MAXMSGLENGTH);
		mTimeout = NetBase.theNetBase().config().getAsInt("net.timeout.socket", DEFAULT_SOCKET_TIMEOUT);
		mTimeoutGranularity = NetBase.theNetBase().config().getAsInt("net.timeout.granularity", DEFAULT_TIMEOUT_GRANULARITY);
		
		mSock = sock;
		
		mIS = sock.getInputStream();
		mOS = sock.getOutputStream();
	}
	
	/**
	 * Closes the underlying socket and renders this TCPMessageHandler useless.
	 */
	public void close() {
		try {
			// Java says that if you close one of these, you close them all.  We conservatively close them all.
			if ( mIS != null ) mIS.close();
			if ( mOS != null ) mOS.close();
			if ( mSock != null ) mSock.close();
		} catch (IOException e) { 
		}
	}
	
	/**
	 * Set the read timeout on the underlying socket.
	 * @param timeout Time out, in msec.
	 */
	@Override
	public int setTimeout(int timeout) throws SocketException {
		int oldValue = mTimeout;
		mTimeout = timeout;
		return oldValue;
	}
	
	/**
	 * Enable/disable TCPNoDelay on the underlying TCP socket.
	 */
	@Override
	public boolean setNoDelay(boolean value) throws SocketException {
		boolean oldValue = mSock.getTcpNoDelay();
		mSock.setTcpNoDelay(value);
		return oldValue;
	}
	
	/**
	 * Sets the maximum allowed size for which decoding of a message will be attempted.
	 * @return The previous setting of the maximum allowed message length.
	 */
	@Override
	public int setMaxReadLength(int maxLen) {
		if ( maxLen <= 0 ) throw new IllegalArgumentException("setMaxReadLength argument must be positive: " + maxLen);
		int result = mMaxMsgLength;
		mMaxMsgLength = maxLen;
		return result;
	}

	/**
	 * Returns the current setting for the maximum read length
	 */
	@Override
	public int getMaxReadLength() {
		return mMaxMsgLength;
	}
	
	//--------------------------------------------------------------------------------------
	// send routines
	//--------------------------------------------------------------------------------------
	
	@Override
	public void sendMessage(byte[] buf) throws IOException {
	  // You need to implement this routine.
	  // The output stream to write to, mOS, is already open.
	}
	
	/**
	 * Uses str.getBytes() for conversion.
	 */
	@Override
	public void sendMessage(String str) throws IOException {
		sendMessage(str.getBytes());
	}

	/**
	 * We convert the int to the one the wire format and send as bytes.
	 */
	@Override
	public void sendMessage(int value) throws IOException{
		sendMessage( intToByte(value) );
	}
	
	/**
	 * Sends JSON string representation of the JSONArray.
	 */
	@Override
	public void sendMessage(JSONArray jsArray) throws IOException {
		sendMessage(jsArray.toString());
	}
	
	/**
	 * Sends JSON string representation of the JSONObject.
	 */
	@Override
	public void sendMessage(JSONObject jsObject) throws IOException {
		sendMessage(jsObject.toString());
	}
	
	//--------------------------------------------------------------------------------------
	// read routines
	//   All of these invert any encoding done by the corresponding send method.
	//--------------------------------------------------------------------------------------
	
	@Override
	public byte[] readMessageAsBytes() throws IOException {
	  // You need to implement all of the read methods.
	  // They're inverses of the corresponding write methods.
	  // (For example, if I send a string with sendMessage("foo")
	  // and read it at the other end with readMessageAsString(), 
	  // I should recover the string "foo".)
		return new byte[0];
	}
	
	@Override
	public String readMessageAsString() throws IOException {
    // You need to implement this.
		return "Not yet implemented";
	}

	@Override
	public int readMessageAsInt() throws IOException {
    // You need to implement this.
		return -1;
	}
	
	
	@Override
	public JSONArray readMessageAsJSONArray() throws IOException, JSONException {
    // You need to implement this.
		return new JSONArray();
	}
	
	@Override
	public JSONObject readMessageAsJSONObject() throws IOException, JSONException {
    // You need to implement this.
		return new JSONObject();
	}
	
	/**
	 * Helper function that forms a printable name for the socket.
	 * @param sock
	 * @return
	 */
	private String sockAddrString(Socket sock) {
		InetAddress addr = sock.getInetAddress();
		int port = sock.getPort();
		return " [" + addr.toString() + ":" + port + "]";
	}
	
	/**
	 * Helper routine that reads a particular number of bytes from connection.
	 * @param nBytes Number of bytes to read.  Must be greater than zero.
	 * @return Otherwise returns a <code>byte[nBytes]</code> array of received data.
	 * @throws IOException Throws an IOException if less than nBytes read or the length
	 * field contains a value that exceeds the maximum allowed message length; throws EOFException if EOF reached; throws SocketTimeoutException. 
	 */
	protected byte[] readFromStream(int nBytes) throws IOException {
		// try to assemble nBytes bytes from the stream.  You should spend no more than the socket timeout amount
	  // of time waiting for that much data to arrive.  You should not block on the socket for the entire time -
	  // instead, set a short socket timeout so that you can wake up and check for program termination.  If the program
	  // hasn't terminated, tried to read the bytes again; wake up check for termination, repeat until the overall timeout
	  // limit is exceeded.
	  return null;
	}
}
