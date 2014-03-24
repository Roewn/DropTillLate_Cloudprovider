package ch.droptilllate.cloudprovider.commons;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.droptilllate.cloudprovider.error.CloudError;
import ch.droptilllate.cloudprovider.error.CloudException;

public class WebHelperTest
{

	@Test
	public void testValidInternetConncetion()
	{
		try
		{
			assertTrue(WebHelper.pingURL("http://www.google.com",2000));
		} catch (CloudException e)
		{
			System.err.println(e.getMessage());
		}		
	}
	
	@Test
	public void testInvalidInternetConncetion()
	{
		try
		{
			WebHelper.pingURL("http://www.ewrziui",2000);
		} catch (CloudException e)
		{
			assertEquals(e.getError(), CloudError.NO_INTERNET);
		}		
	}

}
