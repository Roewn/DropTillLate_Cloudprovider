/**
 * 
 */
package ch.droptilllate.cloudprovider.dropbox;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import ch.droptilllate.cloudprovider.commons.Timer;
import ch.droptilllate.cloudprovider.error.CloudError;
import ch.droptilllate.cloudprovider.error.CloudException;

/**
 * @author Rene Amrhein
 * 
 */
public class HeadlessBrowserDB
{
	private WebDriver webDriver;

	private static String IDENTIFIER_LOGIN = "login_email";
	private static String IDENTIFIER_PW = "login_password";

	// private static String IDENTIFIER_SHARE_FORM = "invite-more-form";
	private static String IDENTIFIER_SHARE_FORM = "//form[@class='invite-more-form']";
	private static String IDENTIFIER_RESHARE_FORM = "//div[starts-with(@id,'folder-share-')]";
	
	private static String IDENTIFIER_RESHARE_USER_SCRIPT = "//div[starts-with(@class,'ids textinput')]";

	private static String IDENTIFIER_SHARE_USER_LIST = "//form[@class='invite-more-form']//input[starts-with(@id,'invite-wizard-')][@type='text'][contains(@class,'new-collab-input')]";
	private static String IDENTIFIER_RESHARE_USER_LIST = "//form[@class='invite-more-form']//input[starts-with(@id,'sharing-options-')][@type='text'][contains(@class,'new-collab-input')]";

	private static String IDENTIFIER_SHARE_MESSAGE = "custom-message-wizard";

	private static String IDENTIFIER_SHARE_BUTTON = "//form[@class='invite-more-form']//input[contains(@class,'confirm-button')][@type='button'][@value='Share folder']";

	private static int WAIT_SHORT = 3;
	private static int WAIT_MEDIUM = 5;

	/**
	 * Initiates the driver for the headlessBrowser
	 */
	public HeadlessBrowserDB()
	{
		// TODO Remove firefox .. just for testing
//		 webDriver = new FirefoxDriver();

		webDriver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
		((HtmlUnitDriver) webDriver).setJavascriptEnabled(true);

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
			try
			{
				WebElement login = webDriver.findElement(By.id(IDENTIFIER_LOGIN));
				login.sendKeys(cloundUser);
			} catch (Exception e)
			{
				throw new CloudException(CloudError.WEBERROR_LOGINFIELD,
						"Unable to add user for login, could not identifiy webelement by using: " + IDENTIFIER_LOGIN);
			}

			WebElement pw;
			try
			{
				pw = webDriver.findElement(By.id(IDENTIFIER_PW));
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
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(IDENTIFIER_LOGIN)));

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
			// TODO remove in case of already shared check
			throw new CloudException(CloudError.FOLDER_NOT_FOUND, url);
		}		
	}

	/**
	 * Shares the folder of the passed share Relation with the users specified in the userEmailList. *
	 * 
	 * @param droptilllatePath path to the root folder (DropTillLate Folder) which holds all the shareRelation directories. Example:
	 *            C:\dropbox\droptilllate
	 * @param shareRelationID name of the folder to shared identified by the shareRelationID
	 * @param shareEmails List of email addresses for all users the share relations has to be shared with.
	 * @param alreadyShared this indicates if the passed folder indentified by the shareRelationID is already shared to other users (true =
	 *            already shared).
	 * @return In Case of an Error, a CloudError gets returned describing the problem. If no error occurred, CloudError.NONE gets returned.
	 */
	public void shareFolder(String droptilllatePath, int shareRelationID, String shareEmails, boolean alreadyShared) throws CloudException
	{
		String url = ConstantsDB.BASIC_URL + "/" + droptilllatePath + "/" + shareRelationID + ConstantsDB.URL_SHARE_PARAMS;
		String xpathShareForm =  IDENTIFIER_SHARE_FORM;
		String xpathUsers = IDENTIFIER_SHARE_USER_LIST;
		String idMessageTextbox = IDENTIFIER_SHARE_MESSAGE;
		String xpathButton = IDENTIFIER_SHARE_BUTTON;;


		if (alreadyShared)
		{
			// folder was already shared
			url = ConstantsDB.BASIC_URL + "/" + droptilllatePath + "/" + shareRelationID + ConstantsDB.URL_RESHARE_PARMS;
			xpathShareForm = IDENTIFIER_RESHARE_FORM;
			xpathUsers = IDENTIFIER_RESHARE_USER_LIST;			
			
		}
		// open the url in the browser
		webDriver.get(url);
		
		// wait for the share dialog to open		
		waitForPresenceOfElementByXPath(xpathShareForm, WAIT_SHORT, webDriver, CloudError.WEBERROR_SHAREDIALOG, "Share dialog could not be identified by xpath argument: " + xpathShareForm);

		
//		WebDriverWait wait = new WebDriverWait(webDriver, WAIT_SHORT);
//		try
//		{
//			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(IDENTIFIER_SHARE_FORM)));
//			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@class='invite-more-form']")));
//		} catch (Exception e)
//		{
//			throw new CloudException(CloudError.WEBERROR_SHAREDIALOG,
//					"Share dialog could not be identified, Folder does not exist on Dropbox or is already shared\n" + url);
//		}
		
		// if folder is already shared, the user script has to be activated before new users can be appended
		if (alreadyShared)
		{
			try
			{
				WebElement userScript = webDriver.findElement(By.xpath(IDENTIFIER_RESHARE_USER_SCRIPT));
				userScript.click();
				// submitButton.submit();
				System.out.println("script found");
			} catch (Exception e)
			{
				throw new CloudException(CloudError.WEBERROR_SHAREBUTTON,
						"Unable to submit share form, could not identifiy webelement share button by using: " + xpathButton);
			}
			
		}

		// get and fill user list
		try
		{
			WebElement users = webDriver.findElement(By.xpath(xpathUsers));
			users.sendKeys(shareEmails);
			System.out.println("users form filled");
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_USERLIST, "Unable to add share emails, could not identifiy webelement by using: "
					+ xpathUsers);
		}

		// get and fill message textfield
		try
		{
			WebElement message = webDriver.findElement(By.id(idMessageTextbox));
			message.sendKeys(ConstantsDB.SHARE_MESSAGE);
			System.out.println("message attached");
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_MESSAGE, "Unable to add message, could not identifiy webelement by using: "
					+ idMessageTextbox);
		}

		// get share button and submit
		try
		{
			WebElement submitButton = webDriver.findElement(By.xpath(xpathButton));
			submitButton.click();
			// submitButton.submit();
			System.out.println("folder " + shareRelationID + " shared");
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_SHAREBUTTON,
					"Unable to submit share form, could not identifiy webelement share button by using: " + xpathButton);
		}
	}

	/**
	 * Shares the the already shared folder of the passed share Relation with the new users specified in the userEmailList. *
	 * 
	 * @param droptilllatePath path to the root folder (DropTillLate Folder) which holds all the shareRelation directories. Example:
	 *            C:\dropbox\droptilllate
	 * @param shareRelationID name of the folder to shared identified by the shareRelationID
	 * @param shareEmails List of email addresses for all users the share relations has to be shared with.
	 * @return In Case of an Error, a CloudError gets returned describing the problem. If no error occurred, CloudError.NONE gets returned.
	 */
	public void reshareFolder(String droptilllatePath, int shareRelationID, String shareEmails) throws CloudException
	{
		String url = ConstantsDB.BASIC_URL + "/" + droptilllatePath + "/" + shareRelationID + ConstantsDB.URL_RESHARE_PARMS;

		webDriver.get(url);
		// wait for the share dialog to open
		WebDriverWait wait = new WebDriverWait(webDriver, WAIT_SHORT);
		try
		{
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(IDENTIFIER_SHARE_FORM)));
			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@class='invite-more-form']")));
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_SHAREDIALOG, "Share dialog could not be identified" + url);
		}

		System.out.println("JUHUUUUUUUUUUUUUUUII");

		// get and fill user list
		try
		{
			WebElement users = webDriver.findElement(By.xpath(IDENTIFIER_SHARE_USER_LIST));
			users.sendKeys(shareEmails);
			System.out.println("users form filled");
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_USERLIST, "Unable to add share emails, could not identifiy webelement by using: "
					+ IDENTIFIER_SHARE_USER_LIST);
		}

		// get and fill message textfield
		try
		{
			WebElement message = webDriver.findElement(By.id(IDENTIFIER_SHARE_MESSAGE));
			message.sendKeys(ConstantsDB.SHARE_MESSAGE);
			System.out.println("message attached");
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_MESSAGE, "Unable to add message, could not identifiy webelement by using: "
					+ IDENTIFIER_SHARE_MESSAGE);
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
			webDriver.quit();
		} catch (Exception e)
		{
			System.out.println("Info when quitting WebDriver: " + e.getMessage());
		}
	}

	/**
	 * Waits an amount of time for the presence of a web element
	 * 
	 * @param xpathCondition condition in xpath format to be checked
	 * @param timeOutInSeconds time in seconds to wait until exception is thrown
	 * @param driver webdriver instance
	 * @param error CloudError for the exception to throw
	 * @param errorMessage message for the exception to throw
	 * @throws CloudException thrown if the element could not be found during timeOutInSeconds
	 */
	private void waitForPresenceOfElementByXPath(String xpathCondition, int timeOutInSeconds, WebDriver driver, CloudError error,
			String errorMessage) throws CloudException
	{
		// wait for the web element to open
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		try
		{
//			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathCondition)));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathCondition)));
		} catch (Exception e)
		{
			throw new CloudException(error, errorMessage);
		}
	}

}
