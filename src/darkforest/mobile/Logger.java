package darkforest.mobile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Logger 
{
	private static final int TIMEOUT_DELAY = 10000;
	
	private DataInputStream in;
	
	private DataOutputStream out;

	public static final byte[] READ_RANGES = {(byte)0x8A};

	public static final byte[] READ_SENSORS = {(byte)0x54};

	public static final byte[] READ_VALUES = {(byte)0x40};

	public static final byte[] ENABLE_EXTENDED_COMMANDS = {(byte)0x52};

	public static final byte[] POWER_UP_SENSORS = {(byte)0x36};
	
	public Logger(DataInputStream in, DataOutputStream out)
	throws IOException
	{
		this.in = in;
		this.out = out;
		sendBytes(POWER_UP_SENSORS, 0);
		sendBytes(ENABLE_EXTENDED_COMMANDS, 0);
	}
	
	public int[] readValues() 
	throws IOException
	{
		return sendBytes(READ_VALUES, 5);
	}
	
	public int[] readSensors()
	throws IOException
	{
		return sendBytes(READ_SENSORS, 4);
	}
	
	public int[] readRanges()
	throws IOException
	{
		return sendBytes(READ_RANGES, 4);
	}
	
	private int[] sendBytes(byte[] command, int expected)
	throws IOException
	{
		return sendBytes(command, expected, TIMEOUT_DELAY);
	}
	
	private synchronized int[] sendBytes(byte[] command, int expected, long timeout) 
	throws IOException
	{
		int[] returnArray = new int[expected];
		int read = 0;
		
		out.flush();
		in.skip(in.available());
		out.write(command);
		out.flush();
		
		long endTime = System.currentTimeMillis() + timeout;
		while(read < expected + 1)
		{
			if(System.currentTimeMillis() >= endTime)
				throw new IOException("Command timed out");
						
			int nextByte = in.readUnsignedByte();
			if(read > 0)
				returnArray[read - 1] = nextByte;
			read++;
		}
		return returnArray;
	}
}
