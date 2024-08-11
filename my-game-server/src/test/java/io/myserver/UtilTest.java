package io.myserver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {
    @Test
    void runAllTestForReadShort()
    {
        readShortPositive();
        readShortNegative();
        readShortAtZero();
        readShortMax_short();
        readShortMin_short();
    }
//    @Test
    void readShortPositive()
    {
        byte[] inputArray = {4, (byte) 210};
        int beginIndex = 0;
        short outputShort = Util.readShort(inputArray,beginIndex);

        assertEquals(1234,outputShort);
    }

//    @Test
    void readShortNegative()
    {
        byte[] inputArray = {(byte)251,46};
        int beginIndex = 0;
        short outputShort = Util.readShort(inputArray,beginIndex);

        assertEquals(-1234,outputShort);
    }

    void readShortAtZero()
    {
        byte[] inputArray = {0};
        int beginIndex = 0;
        short outputShort = Util.readShort(inputArray, beginIndex);
        assertEquals( 0,outputShort);

    }

    void readShortMax_short()
    {
        byte[] inputArray = {(byte)128,0};
        int beginIndex = 0;
        short outputShort = Util.readShort(inputArray,beginIndex);
        assertEquals(-32768, outputShort);
    }

    void readShortMin_short()
    {
        byte[] inputArray = {(byte) 127, (byte) 255};
        int  beginIndex = 0;
        short outputShort = Util.readShort(inputArray, beginIndex);
        assertEquals(32767, outputShort);
    }
}