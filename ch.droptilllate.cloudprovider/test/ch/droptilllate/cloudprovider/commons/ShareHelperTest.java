package ch.droptilllate.cloudprovider.commons;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.droptilllate.cloudprovider.commons.ShareHelper;
import ch.droptilllate.cloudprovider.error.CloudError;
import ch.droptilllate.cloudprovider.error.CloudException;

public class ShareHelperTest
{

	@Test
	public void testValidEmail()
	{
		try
		{
			assertTrue(ShareHelper.isValidEmailAddress("test@test.com"));
		} catch (CloudException e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInvalidEmail()
	{
		try
		{
			ShareHelper.isValidEmailAddress("test.test.com");
		} catch (CloudException e)
		{
			assertEquals(e.getError(), CloudError.INVALID_EMAIL);
		}
	}

}
