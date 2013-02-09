package edu.uw.cs.cse461.httpd;

public interface HTTPProviderInterface {
	/**
	 * The interface for components that are accessible through the web server component.
	 * URLs accessing such services specify the service name as the first component of the url, which is
	 * used by the infrastructure to dispatch the HTTP GET to the correct HTTPProvider.  The remainder of the
	 * URL is provided to the service's httpServe() method, which interprets it in a service-specific way. 
	 * <p>
	 * If the returned string starts with the string "<html>" the entire return value is pesumed to contain html.
	 * Otherwise, the returned value is presumed to contain plain text.
	 *  <p>
	 * @param uriArray uriArray[0] is empty; uriArray[1] is service name (e.g., "ddns"); successive elements are some uri path specific to the service 
	 * @return an HTML page
	 * @throws Exception  The infrastructure will catch throw Exceptions and try to do the right thing.
	 */
	public String httpServe(String[] uriArray) throws Exception;
}

