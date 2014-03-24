/**
 * 
 */
package ch.droptilllate.cloudprovider.error;


/**
 * This Exception is thrown when an error occurs during sharing a folder over a cloud provider.
 * @author Rene Amrhein
 *
 */
@SuppressWarnings("serial")
public class CloudException extends Exception
{
private CloudError error;
	
	public CloudException(String msg)
	{
		super(msg);
	}

	public CloudException(CloudError error, String msg)
	{
		super(msg);
		this.error = error;
		this.error.setMessage(msg);
	}


	public String getMessage() {
		return super.getMessage();
	}

	/**
	 * @return the error enumerator (CloudError)
	 */
	public CloudError getError() {
		return error;
	}

	/**
	 * @param error the error enumerator to set (CloudError)
	 */
	public void setError(CloudError error) {
		this.error = error;
	}

}
