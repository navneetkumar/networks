package edu.uw.cs.cse461.net.ddns;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class DDNSException extends Exception {
	
	public DDNSErrorCode errorCode;
	protected List<String> args;
	
	public static enum DDNSErrorCode {
		NOSUCHNAME(1, "NoSuchName"),       // name doesn't exist
		NOADDRESS(2, "NoAddress"),         // name exists, but has no registered address
		AUTHORIZATION(3, "Authorization"), // some operation required authorization caller didn't have
		RUNTIME(4, "Runtime"),             // misc. exception
		TTLEXPIRED(5, "TTLExpired"),       // exceeded max steps allowed
		ZONE(6, "Zone");                   // request about name not in zone
		
		public final String mString;
		public final int mInt;
		
		DDNSErrorCode(int i, String s) { mInt = i; mString = s; }
		@Override
		public String toString() { return mString; }
		public int toInt() { return mInt; }
	};
	
	/**
	 * Base class.  Only more specific subclass objects can be constructed.
	 * @param msg
	 */
	protected DDNSException(String msg) {
		super(msg);
	}
	
	@Override
	public String getMessage() {
		return super.getMessage();
	}
	
	public List<String> getArgs() {
	  return args;
	}
	
	/**
	 * messing routine (DDNSException -> JSONObject)
	 * @return
	 * @throws JSONException
	 */
	public JSONObject marshall() throws JSONException {
		JSONObject result = new JSONObject().put("exceptionnum", errorCode.toInt())
											.put("message", getMessage());
		if ( args != null ) result.put("args", args);
		return result;
	}
	
	/**
	 * Demarshalling routine (JSONObject -> DDNSException subclass)
	 * @param obj
	 * @return
	 */
	public static DDNSException unmarshall(JSONObject obj) throws JSONException, DDNSException {
		switch(obj.getInt("exceptionnum")) {
		case 1:	throw new DDNSNoSuchNameException(new DDNSFullName(obj.getString("name")));
		case 2: throw new DDNSNoAddressException(new DDNSFullName(obj.getString("name")));
		case 3: throw new DDNSAuthorizationException(new DDNSFullName(obj.getString("name")));
		case 4: throw new DDNSRuntimeException(obj.getString("message"));
		case 5: throw new DDNSTTLExpiredException(new DDNSFullName(obj.getString("name")));
		case 6: throw new DDNSZoneException(new DDNSFullName(obj.getString("name")), new DDNSFullName(obj.getString("zone")));
		default: throw new DDNSRuntimeException("Can't decode DDNSException: " + obj.toString());
		}
	}
	
	/**
	 * Catch-all exception for something went wrong.  The message should explain what.
	 * @author zahorjan
	 *
	 */
	public static class DDNSRuntimeException extends DDNSException {
		public DDNSRuntimeException(String msg) {
			super(msg);
			errorCode = DDNSErrorCode.RUNTIME;
		}
		public JSONObject marshall() throws JSONException {
			return super.marshall();
		}
	}
	
	/**
	 * A password was required, but either wasn't supplied or wasn't correct.
	 * @author zahorjan
	 *
	 */
	public static class DDNSAuthorizationException extends DDNSException {
		public DDNSAuthorizationException(DDNSFullNameInterface name) {
			super("Bad password for '" + name + "'");
			errorCode = DDNSErrorCode.AUTHORIZATION;
			args = Arrays.asList(name.toString());
		}
		public JSONObject marshall() throws JSONException {
			return super.marshall().put("name", args.get(0));
		}
	}
	
	/**
	 * The name doesn't exist - there is no DDNS entry for it.
	 * @author zahorjan
	 *
	 */
	public static class DDNSNoSuchNameException extends DDNSException {
		public DDNSNoSuchNameException(DDNSFullNameInterface name) {
			super("No such name: '" + name + "'");
			errorCode = DDNSErrorCode.NOSUCHNAME;
			args = Arrays.asList(name.toString());
		}
		public JSONObject marshall() throws JSONException {
			return super.marshall().put("name", args.get(0));
		}
	}

	/**
	 * The name exists and its record can hold an address, but it doesn't have an address.
	 * @author zahorjan
	 *
	 */
	public static class DDNSNoAddressException extends DDNSException {
		public DDNSNoAddressException(DDNSFullNameInterface name) {
			super("No address exists for name '"+ name + "'");
			errorCode = DDNSErrorCode.NOADDRESS;
			args = Arrays.asList(name.toString());
		}
		public JSONObject marshall() throws JSONException {
			return super.marshall().put("name", args.get(0));
		}
	}

	/**
	 * The maximum number of steps allowed to resolve the name has been exceed.
	 * @author zahorjan
	 *
	 */
	public static class DDNSTTLExpiredException extends DDNSException {
		public DDNSTTLExpiredException(DDNSFullNameInterface name) {
			super("TTL expired resolving '" + name + "'");
			errorCode = DDNSErrorCode.TTLEXPIRED;
			args = Arrays.asList(name.toString());
		}
		public JSONObject marshall() throws JSONException {
			return super.marshall().put("name", args.get(0));
		}
	}

	public static class DDNSZoneException extends DDNSException {
		public DDNSZoneException(DDNSFullNameInterface name, DDNSFullNameInterface zone) {
			super("Name '" + name +"' isn't in my zone [" + zone + "]");
			errorCode = DDNSErrorCode.ZONE;
			args = Arrays.asList(name.toString(), zone.toString());
		}
		public JSONObject marshall() throws JSONException {
			return super.marshall().put("name", args.get(0)).put("zone", args.get(1));
		}
	}

}
