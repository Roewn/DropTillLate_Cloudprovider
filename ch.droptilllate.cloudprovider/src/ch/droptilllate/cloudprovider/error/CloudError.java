/**
 * 
 */
package ch.droptilllate.cloudprovider.error;

/**
 * Typ of Error for sharing a folder over a cloud provider. 
 * @author Rene Amrhein
 *
 */
public enum CloudError
{
	NONE("No error"),
	
	NO_INTERNET("No internet connection available"),
	UNKOWN_HOST("IP address of the host could not be determined"),
	
	INVALID_ACCOUNT("Could not login, invalid username or password"),	
	
	INVALID_EMAIL("The passed email is not in a valid format"),
	
	FOLDER_NOT_FOUND("Share-Folder could not be found on cloud service"),
	FOLDER_ALREADY_SHARED("Share-Folder exits, but is already shared"),
	
	WEBERROR_SHAREDIALOG("Share dialog could not be opened"),	
	WEBERROR_USERLIST("Userlist web element could not be fetched from current page"),
	WEBERROR_MESSAGE("Message textfield web element could not be fetched from current page"),
	WEBERROR_SHAREBUTTON("Share button web element could not be fetched from current page"),
	WEBERROR_LOGINFIELD("Login field for username could not be fetched from current page"),
	WEBERROR_PWFIELD("Login field for password could not be fetched from current page");
	
	
	private String error;
	private String message = "no message attached";


	private CloudError(String error) {
		this.error = error;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Return the String for the error in form of -> error +": "+ message
	 */
	public String toString(){
		return error +": "+ message;
	}

}
