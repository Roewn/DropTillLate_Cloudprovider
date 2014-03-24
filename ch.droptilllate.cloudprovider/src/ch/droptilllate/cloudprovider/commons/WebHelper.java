package ch.droptilllate.cloudprovider.commons;

import java.awt.Desktop;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

import ch.droptilllate.cloudprovider.error.CloudError;
import ch.droptilllate.cloudprovider.error.CloudException;

/**
 * @author Rene Amrhein
 * 
 */
public class WebHelper
{

	/**
	 * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in the 200-399 range.
	 * 
	 * @param url The HTTP URL to be pinged.
	 * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that the total timeout is
	 *            effectively two times the given timeout.
	 * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the given timeout,
	 *         otherwise <code>false</code>.
	 */
	public static boolean pingURL(String url, int timeout) throws CloudException
	{
		System.out.println("Checking internet connetion: " + url);
		url = url.replaceFirst("https", "http"); // Otherwise an exception may be thrown on invalid SSL certificates.
		try
		{
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			return (200 <= responseCode && responseCode <= 399);
		} catch (IOException e)
		{
			throw new CloudException(CloudError.NO_INTERNET, e.getMessage());
		}
	}


	public static void openWebPage(String url)
	{
		if (Desktop.isDesktopSupported())
		{
			// For Windows
			Desktop desktop = Desktop.getDesktop();
			try
			{
				desktop.browse(new URI(url));
			} catch (IOException | URISyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
