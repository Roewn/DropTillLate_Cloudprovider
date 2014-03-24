package ch.droptilllate.cloudprovider.dropbox;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import ch.droptilllate.cloudprovider.error.CloudError;
import ch.droptilllate.cloudprovider.error.CloudException;

public class HeadlessBrowserDBTest
{
	private static String USER = "droptilllate@mail.com";
	private static String PW = "supersecure";
	private static String DROPTILLLATE_PATH = "droptilllate";
	private static int SHARERELATION_ID = 1111;

	private HeadlessBrowserDB browser;
	
	public HeadlessBrowserDBTest() {
		browser = new HeadlessBrowserDB();
	}

	@Test
	public void testFolderIsOnDropbox()
	{
		// test available folder
		CloudError error = CloudError.NONE;
		try
		{
			browser.loginAccount(USER, PW);
			browser.isFolderOnDB(DROPTILLLATE_PATH, SHARERELATION_ID);
			
		} catch (CloudException e)
		{
			System.err.println(e.getError());
			error = e.getError();
		}		
		assertEquals(error, CloudError.NONE);
		
		// test unavailable folder
		error = CloudError.NONE;
		try
		{
			browser.isFolderOnDB(DROPTILLLATE_PATH, 2121);
			
		} catch (CloudException e)
		{
			System.err.println(e.getError());
			error = e.getError();
		}		
		assertEquals(error, CloudError.FOLDER_NOT_FOUND);
	}
	
	@After
	public void after()
	{
		browser.quit();
	}
	

}
