package io.myserver.database;

public class DBRecord
{
	private String 	_key;
	private String 	_value;
	private int 	_expire_duration;
	private long	_cas;

	public DBRecord(String key, String value)
	{
		this(key, value, 0, 0);
	}

	public DBRecord(String key, String value, int expire_duration)
	{
		this(key, value, expire_duration, 0);
	}

	public DBRecord(String key, String value, long cas)
	{
		this(key, value, 0, cas);
	}

	public DBRecord(String key, String value, int expire_duration, long cas)
	{
		_key = key;
		_value = value;
		_cas = cas;
		_expire_duration = expire_duration;
	}

	public String getKey()
	{
		return _key;
	}

	public String getValue()
	{
		return _value;
	}

	public long getCas()
	{
		return _cas;
	}

	public int getExpireDuration()
	{
		return _expire_duration;
	}
}
