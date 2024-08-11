package io.myserver.database;

public interface DBConnector
{
    public void connect() throws Exception;
    public void close() throws Exception;

    public String get(String key) throws Exception;

    public boolean containKey(String key);

    public void set(String key, String value);

    public void delete(String key);

}
