/**
 * 
 */
package ch.droptilllate.cloudprovider.commons;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import ch.droptilllate.cloudprovider.error.CloudError;
import ch.droptilllate.cloudprovider.error.CloudException;

/**
 * @author Rene Amrhein
 * 
 */
public class ShareHelper
{
	
	public static boolean isValidEmailAddress(String email) throws CloudException
	{
		boolean result = true;
		try
		{
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException e)
		{
			throw new CloudException(CloudError.INVALID_EMAIL, e.getMessage());
		}
		return result;
	}

}
