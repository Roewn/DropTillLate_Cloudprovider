/**
 * 
 */
package ch.droptilllate.cloudprovider.dropbox;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.droptilllate.cloudprovider.error.CloudError;
import ch.droptilllate.cloudprovider.error.CloudException;

/**
 * @author Rene Amrhein
 * 
 */
public class HeadlessBrowserDB
{
	private WebDriver webDriver;

	//private static String IDENTIFIER_LOGIN = "login_email";
	//private static String IDENTIFIER_PW = "login_password";
	private static String IDENTIFIER_LOGIN = "//input[@type='email'][@name='login_email']";	
	private static String IDENTIFIER_PW = "//input[@type='password'][@name='login_password']";
	private static String IDENTIFIER_SHARE_FORM = "//form[@class='invite-more-form']";
//	private static String IDENTIFIER_RESHARE_FORM = "//div[starts-with(@id,'folder-share-')]";
	private static String IDENTIFIER_USER_LIST = "//form[@class='invite-more-form']//input[starts-with(@id,'invite-wizard-')][@type='text'][contains(@class,'new-collab-input')]";
	private static String IDENTIFIER_MESSAGE = "custom-message-wizard";
	private static String IDENTIFIER_SHARE_BUTTON = "//form[@class='invite-more-form']//input[contains(@class,'confirm-button')][@type='button']";

	private static int WAIT_SHORT = 3;
	private static int WAIT_MEDIUM = 8;
	private static int WAIT_LONG = 12;

	/**
	 * Initiates the driver for the headlessBrowser
	 */
	public HeadlessBrowserDB()
	{
		// TODO Remove firefox .. just for testing
		 webDriver = new FirefoxDriver();
		
//		webDriver = new HtmlUnitDriver();
//		((HtmlUnitDriver) webDriver).setJavascriptEnabled(true);

		// turn off htmlunit warnings
		Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
	}

	/**
	 * Logs into the passed dropbox account
	 * 
	 * @param cloundUser Username for the could login
	 * @param cloundPW Password for the cloud login
	 */
	public void loginAccount(String cloundUser, String cloundPW) throws CloudException
	{
		// navigate to the web page
		webDriver.get(ConstantsDB.BASIC_URL);

		// check if account is not already logged in
		String currentURL = webDriver.getCurrentUrl();
		if (!currentURL.equals(ConstantsDB.BASIC_URL))
		{
			System.out.println("Login dropbox account");
			WebDriverWait wait1 = new WebDriverWait(webDriver, WAIT_MEDIUM);
			try
			{
				//wait1.until(ExpectedConditions.visibilityOfElementLocated(By.name(IDENTIFIER_LOGIN)));
				//WebElement login = webDriver.findElement(By.name(IDENTIFIER_LOGIN));
				
				wait1.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(IDENTIFIER_LOGIN)));
				WebElement login = webDriver.findElement(By.xpath(IDENTIFIER_LOGIN));
				
				login.sendKeys(cloundUser);
			} catch (Exception e)
			{
				throw new CloudException(CloudError.WEBERROR_LOGINFIELD,
						"Unable to add user for login, could not identifiy webelement by using: " + IDENTIFIER_LOGIN);
			}

			WebElement pw;
			try
			{
				//pw = webDriver.findElement(By.name(IDENTIFIER_PW));
				pw = webDriver.findElement(By.xpath(IDENTIFIER_PW));
				pw.sendKeys(cloundPW);
			} catch (Exception e)
			{
				throw new CloudException(CloudError.WEBERROR_PWFIELD,
						"Unable to add password for login, could not identifiy webelement by using: " + IDENTIFIER_PW);
			}
			pw.submit();

			WebDriverWait wait = new WebDriverWait(webDriver, WAIT_SHORT);
			try
			{
				//wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(IDENTIFIER_LOGIN)));
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(IDENTIFIER_LOGIN)));

			} catch (Exception e)
			{
				throw new CloudException(CloudError.INVALID_ACCOUNT, "Invalid account information for: " + cloundUser);
			}
		} else
		{
			System.out.println("Dropbox account already logged in");
		}
	}

	/**
	 * Checks if the folder is available on dropbox.
	 * 
	 * @param droptilllatePath path to the root folder (DropTillLate Folder) which holds all the shareRelation directories. Example:
	 *            C:\dropbox\droptilllate
	 * @param shareRelationID name of the folder to shared identified by the shareRelationID
	 * @return true if folder exists
	 * @throws CloudException In Case of an Error, a CloudError gets returned describing the problem. If error == CloudError.NONE the folder
	 *             is synchronized.
	 */
	public boolean isFolderOnDB(String droptilllatePath, int shareRelationID) throws CloudException
	{
		String url = ConstantsDB.BASIC_URL + "/" + droptilllatePath + "/" + shareRelationID;

		WebDriverWait wait = new WebDriverWait(webDriver, WAIT_MEDIUM);

		webDriver.get(url + ConstantsDB.URL_SHARE_PARAMS);
		// wait for the share dialog to open
		try
		{
			// test if the share dialog can be opened ...
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(IDENTIFIER_SHARE_FORM)));
			// if this can be done, folder is on dropbox
			System.out.println("Folder " + shareRelationID + " exists on dropbox");
			return true;
		} catch (Exception e1)
		{	
			//TODO remove in case of already shared check
			throw new CloudException(CloudError.FOLDER_NOT_FOUND, url);
		}

		// TODO if folder is already shared, this can not be detected! shoud be used
		
	}
	

	/**
	 * Shares the folder of the passed share Relation with the users specified in the userEmailList. *
	 * 
	 * @param droptilllatePath path to the root folder (DropTillLate Folder) which holds all the shareRelation directories. Example:
	 *            C:\dropbox\droptilllate
	 * @param shareRelationID name of the folder to shared identified by the shareRelationID
	 * @param shareEmails List of email addresses for all users the share relations has to be shared with.
	 * @return In Case of an Error, a CloudError gets returned describing the problem. If no error occurred, CloudError.NONE gets returned.
	 */
	public void shareFolder(String droptilllatePath, int shareRelationID, String shareEmails) throws CloudException
	{
		String url = ConstantsDB.BASIC_URL + "/" + droptilllatePath + "/" + shareRelationID + ConstantsDB.URL_SHARE_PARAMS;
		
		WebDriverWait wait = new WebDriverWait(webDriver, WAIT_MEDIUM);
		
		webDriver.get(url);
		// wait for the share dialog to open
		
		try
		{
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(IDENTIFIER_SHARE_FORM)));
			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@class='invite-more-form']")));
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_SHAREDIALOG, "Share dialog could not be identified\n" + url);
		}

		// get and fill user list
		try
		{
			WebElement users = webDriver.findElement(By.xpath(IDENTIFIER_USER_LIST));
			users.sendKeys(shareEmails);
			System.out.println("users form filled");
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_USERLIST, "Unable to add share emails, could not identifiy webelement by using: "
					+ IDENTIFIER_USER_LIST);
		}

		// get and fill message textfield
		try
		{
			WebElement message = webDriver.findElement(By.id(IDENTIFIER_MESSAGE));
			message.sendKeys(ConstantsDB.SHARE_MESSAGE);
			System.out.println("message attached");
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_MESSAGE, "Unable to add message, could not identifiy webelement by using: "
					+ IDENTIFIER_MESSAGE);
		}

		// get share button and submit
		try
		{
			WebElement submitButton = webDriver.findElement(By.xpath(IDENTIFIER_SHARE_BUTTON));
			submitButton.click();
			// submitButton.submit();
			System.out.println("folder " + shareRelationID + " shared");
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_SHAREBUTTON,
					"Unable to submit share form, could not identifiy webelement share button by using: " + IDENTIFIER_SHARE_BUTTON);
		}
	}

	/**
	 * Closes all browser windows and safely ends the session.
	 */
	public void quit()
	{
		try
		{
//			((HtmlUnitDriver) webDriver).manage().deleteAllCookies();
			webDriver.quit();
		} catch (Exception e)
		{
			System.out.println("Info when quitting WebDriver: " + e.getMessage());
		}
	}
}
