/**
 * 
 */
package ch.droptilllate.cloudprovider.api;

import java.util.List;

import ch.droptilllate.cloudprovider.error.CloudError;

/**
 * This Interface can be used to share a folder to a list of users.
 * Depending on the Instance, different provider can be choosen.
 * @author Rene Amrhein
 *
 */
public interface ICloudProvider
{
	/**
	 * Shares the folder of the passed share Relation with the users specified in the userEmailList.	 * 
	 * @param droptilllatePath path to the root folder (DropTillLate Folder) which holds all the shareRelation directories.
	 * 			Example: C:\dropbox\droptilllate
	 * @param shareRelationID name of the folder to shared identified by the shareRelationID
	 * @param cloudUser Username for the could login
	 * @param cloundPW Password for the cloud login
	 * @param shareEmailList List of email addresses for all users the share relations has to be shared with.
	 * @return In Case of an Error, a CloudError gets returned describing the problem. If no error occurred, CloudError.NONE gets returned.
	 */
	CloudError shareFolder(String droptilllatePath, int shareRelationID , String cloudUser, String cloundPW, List<String> shareEmailList);
	
	/**
	 * Tests if the passed account for a cloud provider is valid
	 * @param cloudUser Username for the could login
	 * @param cloundPW Password for the cloud login
	 * @return In Case of an Error, a CloudError gets returned describing the problem. If no error occurred, CloudError.NONE gets returned.
	 */
	CloudError testCloudAccount(String cloudUser, String cloundPW);
	
	/**
	 * Checks if the passed share folder is already synchronized to the cloud providers service.
	 * @param droptilllatePath path to the root folder (DropTillLate Folder) which holds all the shareRelation directories.
	 * 			Example: C:\dropbox\droptilllate
	 * @param shareRelationID name of the folder to shared identified by the shareRelationID
	 * @param cloudUser Username for the could login
	 * @param cloundPW Password for the cloud login
	 * @return In Case of an Error, a CloudError gets returned describing the problem. If error == CloudError.NONE the folder is synchronized.
	 */
	CloudError checkIfFolderExists(String droptilllatePath, int shareRelationID , String cloudUser, String cloundPW);
}
