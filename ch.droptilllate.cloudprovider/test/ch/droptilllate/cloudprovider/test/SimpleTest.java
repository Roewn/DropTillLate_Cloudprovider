package ch.droptilllate.cloudprovider.test;

import java.util.ArrayList;
import java.util.List;

import ch.droptilllate.cloudprovider.api.ICloudProvider;
import ch.droptilllate.cloudprovider.dropbox.DropboxHandler;
import ch.droptilllate.cloudprovider.error.CloudError;

public class SimpleTest
{
	private static String USER = "droptilllate@mail.com";
	private static String PW = "supersecure";
	private static String DROPTILLLATE_PATH = "droptilllate";
	private static int SHARERELATION_ID = 1111;

	public static void main(String[] args)
	{
		ICloudProvider dropbox = new DropboxHandler();
		CloudError error = CloudError.NONE;

		List<String> userList = new ArrayList<String>();
		userList.add("wurst@hotmail.com");
		userList.add("homo@gay.ch");

		error = dropbox.checkIfFolderExists(DROPTILLLATE_PATH, SHARERELATION_ID, USER, PW);
//		if (error == CloudError.NONE)
//		{
//			error = dropbox.shareFolder(DROPTILLLATE_PATH, SHARERELATION_ID, USER, PW, userList);
//		}
//		
//		error = dropbox.shareFolderManuallyViaBrowser(DROPTILLLATE_PATH, SHARERELATION_ID, true);
		
//		dropbox.testCloudAccount(USER, PW);
		
	}
}
