package ch.droptilllate.cloudprovider.commons;

import ch.droptilllate.cloudprovider.preferences.Constants;

public class Timer
{
	private static long startTime;

	public static void start()
	{
		startTime = System.currentTimeMillis();
	}

	public static long stop(boolean consoleOutput)
	{
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		if (consoleOutput)
		{
			System.out.println(Constants.TIMER_MESSAGE + elapsedTime);
		}
		return elapsedTime;
	}

	public static void pause(int seconds)
	{
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0;
		while (elapsedTime < seconds * 1000)
		{
			elapsedTime = System.currentTimeMillis() - startTime;
		}
	}
}
