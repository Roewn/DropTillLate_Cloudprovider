/**
 * 
 */
package ch.droptilllate.cloudprovider.dropbox;

import java.util.List;

import ch.droptilllate.cloudprovider.api.ICloudProvider;
import ch.droptilllate.cloudprovider.commons.ShareHelper;
import ch.droptilllate.cloudprovider.commons.WebHelper;
import ch.droptilllate.cloudprovider.error.CloudError;
import ch.droptilllate.cloudprovider.error.CloudException;

/**
 * This class handles the sharing of a folder in a specific Dropbox account.
 * 
 * @author Rene Amrhein
 * 
 */
public class DropboxHandler implements ICloudProvider
{
	private HeadlessBrowserDB browser;

	public DropboxHandler()
	{
		browser = new HeadlessBrowserDB();
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
		printStartToConsole("Share cloud Folder: " + shareRelationID);
		CloudError error = CloudError.NONE;
		String shareEmails = null;

		// check internet connection, login account and check if the folder exists
//		 error = checkIfFolderExists(droptilllatePath, shareRelationID, cloundUser, cloundPW);
		// TODO see if the folder check works and than change back to folder check
		error = testCloudAccount(cloundUser, cloundPW);

		// if no error occured procced ...
		if (error == CloudError.NONE)
		{
			try
			{

				// check if the passed email addresses are in a valid format and build the email list in the valid format
				shareEmails = buildValidMailList(shareEmailList);

				// share to folder to the passed users
				browser.shareFolder(droptilllatePath, shareRelationID, shareEmails);

			} catch (CloudException e)
			{
				System.err.println(e.getError());
				browser.quit();
				return e.getError();
			}
		}

		// TODO better delay handling
		try
		{
			Thread.sleep(2000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		browser.quit();
		return error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.droptilllate.cloudprovider.api.ICloudProvider#testCloudAccount(java.lang.String, java.lang.String)
	 */
	@Override
	public CloudError testCloudAccount(String cloundUser, String cloundPW)
	{
		printStartToConsole("Test cloud account");
		CloudError error = CloudError.NONE;
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

		return error;
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
		printStartToConsole("Check if folder is synchronized on cloud");
		CloudError error = CloudError.NONE;
		error = testCloudAccount(cloundUser, cloundPW);
		if (error == CloudError.NONE)
		{
			try
			{
				// see if the folder exists
				browser.isFolderOnDB(droptilllatePath, shareRelationID);

			} catch (CloudException e)
			{
				System.err.println(e.getError());
				return e.getError();
			}
		}
		return error;
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
		System.out.println("Checking passed email addresses");
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

		System.out.println(ConstantsDB.CONSOLE_LIMITER);
		System.out.println(methode);
	}

}
