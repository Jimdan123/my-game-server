package io.myserver.database;

import io.myserver.database.exception.KeyNotFoundException;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DummyDBConnector implements DBConnector
{
    private static final String                 _DEFAULT_DB_FILENAME = "dummy_db.ser";
    private ConcurrentHashMap<String, DBValue>  _dummy_db;

    private String  _filename;

    public DummyDBConnector()
    {
        this(_DEFAULT_DB_FILENAME);
    }

    public DummyDBConnector(String filename)
    {
        _filename = filename;

        _dummy_db = new ConcurrentHashMap<>(128);
    }

    public void connect() throws Exception
    {
        File f = new File(_filename);

        //fresh new db
        if (f.exists() == false)
        {
            return;
        }

        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream ois = new ObjectInputStream(fis);

        _dummy_db = (ConcurrentHashMap<String, DBValue>) ois.readObject();

        ois.close();
        fis.close();
    }

    public void close() throws Exception
    {
        FileOutputStream fos;
        ObjectOutputStream oos;

        try
        {
            fos = new FileOutputStream(_filename);
            oos = new ObjectOutputStream(fos);

            oos.writeObject(_dummy_db);

            oos.flush();
            oos.close();
            fos.close();
        }
        catch (Exception ex)
        {

        }
    }

    public String get(String key) throws Exception
    {
        DBValue v = _dummy_db.get(key);

        if (v != null)
        {
            return (String) v._value;
        }
        else
        {
            throw new KeyNotFoundException(key);
        }
    }

    public boolean containKey(String key)
    {
        return _dummy_db.contains(key);
    }

    public void set(String key, String value)
    {
        DBValue dbv = _dummy_db.get(key);

        if (dbv == null)
        {
            DBValue new_dbv = new DBValue(value);

            _dummy_db.put(key, new_dbv);
        }
        else
        {
            dbv.change(value);
        }
    }

    public void delete(String key)
    {
        _dummy_db.remove(key);
    }

    static class DBValue implements Serializable
    {
        Object _value;
        AtomicLong _cas;

        long _expire;

        public DBValue(Object v)
        {
            _value = v;
            _cas = new AtomicLong(1);
            _expire = 0;
        }

        public DBValue(Object v, long c)
        {
            _value = v;
            _cas = new AtomicLong(c);
            _expire = 0;
        }

        public long change(Object new_value)
        {
            _value = new_value;

            return _cas.incrementAndGet();
        }

        public long compareAndSet(Object new_value, long cas)
        {
            if (_cas.compareAndSet(cas, cas + 1))
            {
                _value = new_value;

                return cas + 1;
            }

            return 0;
        }

        public void setExpire(long new_exp)
        {
            _expire = new_exp;
        }

        public long setCounter(long init_value)
        {
            _cas.set(init_value);

            return init_value;
        }

        public long increase()
        {
            return _cas.incrementAndGet();
        }
    }
}
