package io.myserver;

public class Util
{
	public static short readShort(byte[] arr, int beginIndex) {
        //TODO:
        byte highByte = arr[beginIndex];
//		System.out.println(highByte);
		if(arr.length == 2)
			return highByte;
        byte lowByte = arr[beginIndex + 1];


        short value = (short) (((highByte & 0xFF) << 8) | (lowByte & 0xFF));

        return value;
    }

	public static short readShortArray(byte[] arr, int beginIndex) throws Exception
	{
//		System.out.println("GoTo");
		int arr_len = arr[beginIndex];
		beginIndex++;

		if (arr_len < 0) {
			throw new Exception("ErrorArrayLess than 0");
		}

		if (arr_len != arr.length/2 - 1)
		{
			throw new Exception("ErrorNotMatchLength");
		}

		if (arr_len > 5000)
		{
			throw new Exception("OverflowCantCalculate");
		}

		System.out.println("arr_len = " + arr_len);

		short[] number = new short[arr_len];
		int j = 0;

		for (int i = beginIndex; i + 1 < arr.length; i+= 2) //tìm tất cả số short trong mảng
		{
			byte highByte = arr[i];
	//		System.out.println(highByte)
			byte lowByte = arr[i+ 1];
			short value = (short) (((highByte & 0xFF) << 8) | (lowByte & 0xFF));

			number[j++] = value;
		}

		short total = 0;
		for (int i = 0; i < number.length; i++)
		{
			total += number[i];
		}

		return total;
	}

	public static void printByteArray(byte[] arr)
	{
		int[] int_arr = new int[arr.length];
		for (int i = 0; i < int_arr.length;i++)
		{
			int_arr[i] = arr[i]&0xFF;
		}
		StringBuilder sb = new StringBuilder(128);

		sb.append('[');
		if (arr != null && arr.length > 0)
		{
			sb.append(int_arr[0]);

			for (int i = 1; i < arr.length; i++)
			{
				sb.append(", ").append(int_arr[i]);
			}
		}
		sb.append(']');

		System.out.println(sb);
	}

//	private static int read_off;


	public static String readString(byte[] arr, int beginIndex)
	{
		int str_len = arr[beginIndex++];

		System.out.println("readString() - str_len = " + str_len);

		String str = new String(arr, beginIndex, str_len);
		beginIndex += str_len;

		System.out.println("readString() - str = " + str);

//		read_off = beginIndex;

		return str;
	}
}
