package edu.uw.cs.cse461.httpd;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import edu.uw.cs.cse461.net.base.NetBase;
import edu.uw.cs.cse461.net.base.NetLoadable.NetLoadableService;
import edu.uw.cs.cse461.net.ddns.DDNSFullName;
import edu.uw.cs.cse461.net.ddns.DDNSResolverServiceInterface;
import edu.uw.cs.cse461.util.Log;

/**
 * Implements a simple HTTP server.  The URI's are of form http://host:ip/<servicename>/....
 * It's up to the service to implement the HTTPProvider interface.
 * <p>
 * There's a small, technical issue that affects the implementation.  The service
 * loading code, in the OS, needs a constructor that takes no arguments.  We want to
 * extend NanoHTTPD, though, and its constructor requires a port number.  Thus, the 
 * public class contains an inner class that e
 *
 */
public class HTTPDService extends NetLoadableService implements HTTPProviderInterface {
	private static final String TAG="HTTPDService";
	
	private NanoHTTPDService mNanoService;
	private boolean mHaveRegistered = false;  // records whether or not this server's name has been registered with DDNS
	private String mWwwName;

	@Override
	public String dumpState() {
		StringBuilder sb = new StringBuilder();
		sb.append(loadablename()).append(" is ").append(isShutdown() ? "down" : "up").append("\nListening on port: ");
		if ( mNanoService != null ) sb.append(mNanoService.localPort());
		if (!mHaveRegistered) sb.append("\nNot registered with DDNS\n");
		else sb.append("\nRegistered with DDNS as ").append(mWwwName).append("\n");
		return sb.toString();
	}
	
	@Override
	public String httpServe(String[] uriArray) throws Exception {
		NetBase theNetBase = NetBase.theNetBase();
		StringBuilder sb = new StringBuilder("<html><head><title>HTTPD Service</title></head><body><h2>HTTPD Service</h2>");
		
		sb.append("<h4>Services implementing web pages:</h4><ul>");
		List<String> services = theNetBase.loadedServiceNames();
		for ( String service : services ) {
			try {
				HTTPProviderInterface provider = (HTTPProviderInterface)theNetBase.getService(service);
				sb.append("<li> <a href='").append(service).append("'>").append(service).append("</a>\n");
			} catch (Exception e) {}
		}
		sb.append("</ul>\n");

		sb.append("<h4>Applications implementing web pages:</h4><ul>");
		List<String> apps = theNetBase.loadedAppNames();
		for ( String app : apps ) {
			try {
				HTTPProviderInterface provider = (HTTPProviderInterface)theNetBase.getApp(app);
				sb.append("<li> <a href='").append(app).append("'>").append(app).append("</a>\n");
			} catch (Exception e) {}
		}

		sb.append("</body></html>");
		return sb.toString();
	}
	
	
	@Override
	public void shutdown() { 
		if ( mNanoService != null ) mNanoService.stop();
		mNanoService = null;
		if ( mHaveRegistered ) try {
			DDNSResolverServiceInterface resolver = (DDNSResolverServiceInterface)NetBase.theNetBase().getService("ddnsresolver");
			if ( resolver == null ) Log.w(TAG, "No local resolver.  Can't unregister name www");
			else resolver.unregister(new DDNSFullName(NetBase.theNetBase().hostname() + ".www") );  
		} catch (Exception e) {
			Log.w(TAG, "ADVISORY: Caught exception while unregistering with parent:\n" + e.getMessage());
		}
		
		super.shutdown();
	}

	private class NanoHTTPDService extends NanoHTTPD {
		/**
		 * Specify port 0 if you don't care what port the name server uses.
		 * @param port
		 * @throws IOException
		 */
		public NanoHTTPDService(int port) throws IOException {
			super(port,null); // nano won't like the null webroot, if it ever uses it, preventing inadvertently allowing access to local files via nano 
		}

		@Override
		public Response serve( String uri, String method, Properties header, Properties parms, Properties files ) {
			if ( uri == null ) return new Response( HTTP_NOTFOUND, MIME_HTML, HTTP_NOTFOUND);
			try {
				Log.i(TAG,  "method = '" + method + "'  uri=" + uri);

				String[] uriVec = uri.split("/");

				try {
					if ( uriVec.length < 2 ) {
						uriVec = new String[2];
						uriVec[1] = "httpd";
					}
						
					//return new Response( HTTP_NOTFOUND, MIME_HTML, HTTP_NOTFOUND);
					HTTPProviderInterface provider = (HTTPProviderInterface)NetBase.theNetBase().getService(uriVec[1]);
					if ( provider == null ) provider = (HTTPProviderInterface)NetBase.theNetBase().getApp(uriVec[1]);
					if ( provider == null ) return new Response( HTTP_NOTFOUND, MIME_HTML, HTTP_NOTFOUND);
					String response = provider.httpServe(uriVec);
					String mimeType = MIME_PLAINTEXT;
					if ( response.startsWith("<html>")) mimeType = MIME_HTML;
					return new Response( HTTP_OK, mimeType, response );
				} catch (Exception e) {
					Log.e(TAG, "server response exception:");
					e.printStackTrace();
					return new Response( HTTP_NOTFOUND, MIME_PLAINTEXT, e.getMessage());
				}

			} catch (Exception e) {
				Log.e(TAG, "server: " + e.getMessage());
				e.printStackTrace();
				return new Response( HTTP_INTERNALERROR, MIME_HTML, HTTP_INTERNALERROR + "<p><pre>" + e.getMessage() + "</pre>");
			}
		}
	}
	
	int httpdPort() {
		if ( mNanoService != null ) return mNanoService.localPort();
		return -1;
	}

	public HTTPDService() throws IOException {
		super("httpd");
		
		NetBase theNetBase = NetBase.theNetBase();
		int port = theNetBase.config().getAsInt("httpd.port", 0, 0);
		this.mNanoService = new NanoHTTPDService(port);
		port = httpdPort();

		// create a name entry for me, but dont' fail to start just because we can't register
		try {
			mHaveRegistered = false;
			mWwwName = theNetBase.config().getProperty("httpd.name");
			if ( mWwwName != null && !mWwwName.isEmpty()) {
				DDNSResolverServiceInterface resolver = (DDNSResolverServiceInterface)theNetBase.getService("ddnsresolver");
				if ( resolver == null ) Log.w(TAG, "No local resolver.  Can't register name " + mWwwName);
//				else resolver.register(new DDNSFullName(OS.hostname() + ".www"), port );
				else {
					resolver.register(new DDNSFullName(mWwwName), port );
					mHaveRegistered = true;
				}
			} else {
				Log.w(TAG,  "No httpd.name entry in config file.  Won't try to register name server address.");
			}
		} catch (Exception e) {
			Log.w(TAG , "Couldn't register '" + mWwwName + "': " + e.getMessage());
		}
		Log.i(TAG, "Service started on port " + mNanoService.localPort());
	}
}
