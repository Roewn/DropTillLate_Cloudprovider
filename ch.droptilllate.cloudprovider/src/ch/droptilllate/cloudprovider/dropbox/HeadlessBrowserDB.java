/**
 * 
 */
package ch.droptilllate.cloudprovider.dropbox;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.droptilllate.cloudprovider.error.CloudError;
import ch.droptilllate.cloudprovider.error.CloudException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * @author Rene Amrhein
 * 
 */
public class HeadlessBrowserDB
{
	private WebDriver webDriver;

	private static String IDENTIFIER_LOGIN = "login_email";
	private static String IDENTIFIER_PW = "login_password";
	private static String IDENTIFIER_SHARE_FORM = "invite-more-form";
	private static String IDENTIFIER_USER_LIST = "//form[@class='invite-more-form']//input[starts-with(@id,'invite-wizard-')][@type='text'][contains(@class,'new-collab-input')]";
	private static String IDENTIFIER_MESSAGE = "custom-message-wizard";
	private static String IDENTIFIER_SHARE_BUTTON = "//form[@class='invite-more-form']//input[contains(@class,'confirm-button')][@type='button']";

	/**
	 * Initiates the driver for the headlessBrowser
	 */
	public HeadlessBrowserDB()
	{
		// TODO Remove firefox .. just for testing
//		 webDriver = new FirefoxDriver();
//		 webDriver = initiateChromeDriver();

		webDriver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
		((HtmlUnitDriver) webDriver).setJavascriptEnabled(true);

		// turn off htmlunit warnings
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
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

			WebDriverWait wait = new WebDriverWait(webDriver, 2);
			try
			{
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(IDENTIFIER_LOGIN)));
				//
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
		String url = ConstantsDB.BASIC_URL + "/" + droptilllatePath;
		webDriver.get(url);

		// TODO does not work propperly with headless browser
		
		WebDriverWait wait = new WebDriverWait(webDriver, 10);
		try
		{
			// wait.until(ExpectedConditions.titleContains(Integer.toString(shareRelationID)));
			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@data-fq_path," + shareRelationID + ")]")));

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href," + shareRelationID + ")]")));
		} catch (Exception e)
		{
			throw new CloudException(CloudError.FOLDER_NOT_FOUND, url + "/" + shareRelationID);
		}

		System.out.println("Folder " + shareRelationID + " exists on dropbox");
		return true;
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
		String url = ConstantsDB.BASIC_URL + "/" + droptilllatePath + "/" + shareRelationID + ConstantsDB.URL_PARAMS;
		webDriver.get(url);
		// wait for the share dialog to open
		WebDriverWait wait = new WebDriverWait(webDriver, 10);
		try
		{
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(IDENTIFIER_SHARE_FORM)));
//			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form[@class='invite-more-form']")));
		} catch (Exception e)
		{
			throw new CloudException(CloudError.WEBERROR_SHAREDIALOG, "Folder already shared or share dialog could no be identified");
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
//			submitButton.submit();
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

	private ChromeDriver initiateChromeDriver() {
		DesiredCapabilities chromeCapabilities = DesiredCapabilities.chrome();

		String chromeBinary = System.getProperty(" ");
		if (chromeBinary == null || chromeBinary.equals("")) {
		    String os = System.getProperty("os.name").toLowerCase().substring(0, 3);
		    chromeBinary = "lib/chromedriver-" + os + (os.equals("win") ? ".exe" : "");
		    System.setProperty("webdriver.chrome.driver", chromeBinary);
		}
		
		chromeCapabilities.setCapability("chrome.binary", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");

		return new ChromeDriver(chromeCapabilities);
	}
}
