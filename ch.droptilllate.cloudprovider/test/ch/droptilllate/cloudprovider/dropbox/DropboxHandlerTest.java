package ch.droptilllate.cloudprovider.dropbox;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.testng.annotations.AfterTest;

import ch.droptilllate.cloudprovider.error.CloudError;

public class DropboxHandlerTest
{
	private static String USER = "droptilllate@mail.com";
	private static String PW = "supersecure";
	private static String DROPTILLLATE_PATH = "droptilllate";
	private static int SHARERELATION_ID = 1111;

	private DropboxHandler dbHandler;

	public DropboxHandlerTest()
	{
		dbHandler = new DropboxHandler();
	}

	@Test
	public void testAccount()
	{
		CloudError error = CloudError.NONE;

		// test invalid account
		error = dbHandler.testCloudAccount(USER, "bla");
		assertEquals(error, CloudError.INVALID_ACCOUNT);

		// test correct account
		error = dbHandler.testCloudAccount(USER, PW);
		assertEquals(error, CloudError.NONE);
	}
	
	@Test
	public void testFolderExists()
	{
		CloudError error = CloudError.NONE;

		// test valid folder
		error = dbHandler.checkIfFolderExists(DROPTILLLATE_PATH, SHARERELATION_ID, USER, PW);
		assertEquals(error, CloudError.NONE);

		// test correct account
		error = dbHandler.checkIfFolderExists(DROPTILLLATE_PATH, 2121, USER, PW);
		assertEquals(error, CloudError.FOLDER_NOT_FOUND);
	}
	
	

	@After
	public void after()
	{
		dbHandler.quitBrowser();
	}

}
