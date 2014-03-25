package ch.droptilllate.cloudprovider.dropbox;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.droptilllate.cloudprovider.commons.WebHelper;

import com.gargoylesoftware.htmlunit.BrowserVersion;

/**
 * @author Roewn
 * 
 */
public class DropboxFolderSharer
{
	private final static String BASIC_URL = "https://www.dropbox.com/home";
	private final static String URL_PARAMS = "?share=1";
	// private final static String URL_SHARE_PARAMS = "";
	private final static String USER_LIMITER = ";";

	public static void shareFolder(String shareRelation)
	{
		WebHelper.openWebPage(BASIC_URL + shareRelation + URL_PARAMS);
	}

	public static void shareFolder(String shareRelation, List<String> userList)
	{
		// Copy user list to clipboard
		StringBuilder sb = new StringBuilder();

		// TODO Check for valid email
		for (String user : userList)
		{
			sb.append(user);
			sb.append(USER_LIMITER);
		}

		// open the folder in the webbrowser and add the params
		// shareFolder(shareRelation);

		// test to wait on web page
		// DesiredCapabilities caps = DesiredCapabilities.htmlUnitWithJs();
		// caps.setBrowserName("firefox");

		 WebDriver webDriver = new HtmlUnitDriver(BrowserVersion.FIREFOX_3_6);
		 ((HtmlUnitDriver) webDriver).setJavascriptEnabled(true);
//		WebDriver webDriver = new FirefoxDriver();

		/* turn off annoying htmlunit warnings */
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

		// navigate to the web page
		webDriver.get(BASIC_URL + "/" + shareRelation + URL_PARAMS);

		WebElement login = webDriver.findElement(By.id("login_email"));
		login.sendKeys("droptilllate@mail.com");

		WebElement pw = webDriver.findElement(By.id("login_password"));
		pw.sendKeys("supersecure");
		pw.submit();

		
		WebDriverWait wait = new WebDriverWait(webDriver, 10);
		try
		{
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("invite-more-form")));
		} catch (Exception e)
		{
			System.err.println(e.getMessage());
		}		
		 System.out.println("page found");
		
		
		 WebElement users =
		 webDriver.findElement(By.xpath("//form[@class='invite-more-form']//input[starts-with(@id,'invite-wizard-')][@type='text'][contains(@class,'new-collab-input')]"));
		 users.sendKeys(sb.toString());
		
		
		 System.out.println("users send");
		
		 WebElement message = webDriver.findElement(By.id("custom-message-wizard"));
		 message.sendKeys("I do it the french way");
		 System.out.println("message attached");
			
		 
		 WebElement submitButton =
		 webDriver.findElement(By.xpath("//form[@class='invite-more-form']//input[contains(@class,'confirm-button')][@type='button']"));
		 submitButton.click();
		 System.out.println("folder shared");
		
		 try
		 {
		 Thread.sleep(2000);
		 } catch (InterruptedException e)
		 {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }

		 webDriver.quit();
		System.out.println("Quit Driver");

	}

	public static void main(String[] args)
	{

		List<String> userList = new ArrayList<String>();
		userList.add("wurst@hotmail.com");
		userList.add("homo@gay.ch");

		shareFolder("droptilllate/1111", userList);

	}

}
