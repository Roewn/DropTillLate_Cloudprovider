/**
 * 
 */
package ch.droptilllate.cloudprovider.dropbox;

import java.util.List;

import ch.droptilllate.cloudprovider.api.ICloudProvider;
import ch.droptilllate.cloudprovider.commons.ShareHelper;
import ch.droptilllate.cloudprovider.commons.Timer;
import ch.droptilllate.cloudprovider.commons.WebHelper;
import ch.droptilllate.cloudprovider.error.CloudError;
import ch.droptilllate.cloudprovider.error.CloudException;
import ch.droptilllate.cloudprovider.preferences.Constants;

/**
 * This class handles the sharing of a folder in a specific Dropbox account.
 * 
 * @author Rene Amrhein
 * 
 */
public class DropboxHandler implements ICloudProvider
{
	// TODO quit browser when object gets disposed
	private HeadlessBrowserDB browser;

	public DropboxHandler()
	{
		browser = new HeadlessBrowserDB();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.cloudprovider.api.ICloudProvider#testCloudAccount(java.lang.String, java.lang.String)
	 */
	@Override
	public CloudError testCloudAccount(String cloundUser, String cloundPW)
	{
		printStartToConsole("Test Dropbox account (username, password)");
		Timer.start();
		try
		{
			// Test the internet connection and return an error if there is a problem
			WebHelper.pingURL(ConstantsDB.BASIC_URL, 20000);
			// Try to login into the account
			browser.loginAccount(cloundUser, cloundPW);
		} catch (CloudException e)
		{
			System.err.println(e.getError());
			return e.getError();
		}
		Timer.stop(true);
		return CloudError.NONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.cloudprovider.api.ICloudProvider#checkIfFolderExists(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public CloudError checkIfFolderExists(String droptilllatePath, int shareRelationID, String cloundUser, String cloundPW)
	{
		// TODO test tillate path
		printStartToConsole("Check if folder '" + droptilllatePath + "/" + shareRelationID + "' exists on Dropbox");
		Timer.start();
		try
		{
			// Test the internet connection and return an error if there is a problem
			WebHelper.pingURL(ConstantsDB.BASIC_URL, 20000);
			// Try to login into the account
			browser.loginAccount(cloundUser, cloundPW);
			// see if the folder exists
			browser.isFolderOnDB(droptilllatePath, shareRelationID);

		} catch (CloudException e)
		{
			System.err.println(e.getError());
			browser.quit();
			return e.getError();
		}
		Timer.stop(true);
		return CloudError.NONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.cloudprovider.api.ICloudProvider#shareFolder(java.lang.String, int, java.lang.String, java.lang.String,
	 * java.util.List)
	 */
	@Override
	public CloudError shareFolder(String droptilllatePath, int shareRelationID, String cloundUser, String cloundPW,
			List<String> shareEmailList)
	{
		printStartToConsole("Share folder: '" + droptilllatePath + "/" + shareRelationID + "' via Dropbox");
		String shareEmails = null;
		Timer.start();
		try
		{
			// Test the internet connection and return an error if there is a problem
			WebHelper.pingURL(ConstantsDB.BASIC_URL, 20000);
			// Try to login into the account
			browser.loginAccount(cloundUser, cloundPW);
			// see if the folder exists
//			browser.isFolderOnDB(droptilllatePath, shareRelationID);
			// check if the passed email addresses are in a valid format and build the email list in the valid format
			shareEmails = buildValidMailList(shareEmailList);
			// share the folder to the passed users
			browser.shareFolder(droptilllatePath, shareRelationID, shareEmails);

		} catch (CloudException e)
		{
			System.err.println(e.getError());
			browser.quit();
			return e.getError();
		}

		// TODO better delay handling
		try
		{
			Thread.sleep(2000);
		} catch (InterruptedException e)
		{
		}

		browser.quit();
		Timer.stop(true);
		return CloudError.NONE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.cloudprovider.api.ICloudProvider#shareFolderManuallyViaBrowser(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public CloudError shareFolderManuallyViaBrowser(String droptilllatePath, int shareRelationID, boolean alreadyShared)
	{
		printStartToConsole("Share folder: '" + droptilllatePath + "/" + shareRelationID + "' manually via default browser");
		Timer.start();
		try
		{
			// Test the internet connection and return an error if there is a problem
			WebHelper.pingURL(ConstantsDB.BASIC_URL, 20000);
			if (alreadyShared)
			{
				// open the dropbox website via default browser for not yet shared folder
				WebHelper
						.openWebPage(ConstantsDB.BASIC_URL + "/" + droptilllatePath + "/" + shareRelationID + ConstantsDB.URL_RESHARE_PARMS);

			} else
			{
				// open the dropbox website via default browser for not yet shared folder
				WebHelper
						.openWebPage(ConstantsDB.BASIC_URL + "/" + droptilllatePath + "/" + shareRelationID + ConstantsDB.URL_SHARE_PARAMS);
			}

		} catch (CloudException e)
		{
			System.err.println(e.getError());
			return e.getError();
		}

		Timer.stop(true);
		return CloudError.NONE;
	}

	/**
	 * Closes all browser windows and safely ends the session.
	 */
	public void quitBrowser()
	{
		browser.quit();
	}

	/**
	 * Checks if the passed email addresses are in a valid format and build the email list in the valid format
	 * 
	 * @param shareEmailList List of email addresses for all users the share relations hast to be shared with.
	 * @return valid list of email addresses for sharing. Example: test@test.ch;high@five.com;
	 * @throws CloudException if a email has an invalid format
	 */
	private String buildValidMailList(List<String> shareEmailList) throws CloudException
	{
		System.out.println("building email list ...");
		StringBuilder sbEmailList = new StringBuilder();
		for (String email : shareEmailList)
		{

			if (ShareHelper.isValidEmailAddress(email))
			{
				sbEmailList.append(email);
				sbEmailList.append(ConstantsDB.USER_LIMITER);
			}
		}
		return sbEmailList.toString();
	}

	private void printStartToConsole(String methode)
	{

		System.out.println(Constants.CONSOLE_LIMITER);
		System.out.println(methode);
	}

}
