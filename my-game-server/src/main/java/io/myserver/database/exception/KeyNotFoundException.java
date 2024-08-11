package io.myserver.database.exception;

public class KeyNotFoundException extends Exception
{
	public KeyNotFoundException(String key)
	{
		super("Key '" + key + "' not found.");
	}
}
