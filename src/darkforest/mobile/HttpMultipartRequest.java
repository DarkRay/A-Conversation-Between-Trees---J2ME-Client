package darkforest.mobile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

public class HttpMultipartRequest 
{
	private class File
	{
		public final String fileName;
		public final String fileType;
		public final byte[] fileData;
		public File(String fileName, String fileType, byte[] fileData)
		{
			this.fileName = fileName;
			this.fileType = fileType;
			this.fileData = fileData;
		}
	}
	
	private static final int BOUNDARY_LENGTH = 10;
	
	/** Double hypen string for HTTP request boundaries. */
	private static final String HH = "--";
	
	/** Newline string for HTTP requests. */
	private static final String NL = "\r\n";
	
	private final String boundary;
	
	private Hashtable fields = new Hashtable();
	
	private Hashtable files = new Hashtable();
	
	public HttpMultipartRequest()
	{
		boundary = generateBoundary();
	}
	
	public void addField(String fieldName, String fieldValue)
	{
		fields.put(fieldName, fieldValue);
	}
	
	public void addFile(String fieldName, String fileName, String fileType, byte[] fileData)
	{
		files.put(fieldName, new File(fileName, fileType, fileData));
	}
	
	public String getBoundary()
	{
		return boundary;
	}
	
	public InputStream getRequest()
	throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		for(Enumeration e = fields.keys(); e.hasMoreElements();)
		{
			String key = (String)e.nextElement();
			
			out.write(HH.getBytes());
			out.write(boundary.getBytes());
			out.write(NL.getBytes());
			out.write("Content-Disposition: form-data; name=\"".getBytes());
			out.write(key.getBytes());
			out.write("\"".getBytes());
			out.write(NL.getBytes());
			out.write(NL.getBytes());
			out.write(((String)fields.get(key)).getBytes());
			out.write(NL.getBytes());
		}
		
		for(Enumeration e = files.keys(); e.hasMoreElements();)
		{
			String key = (String)e.nextElement();
			File file = (File)files.get(key);
		
			out.write(HH.getBytes());
			out.write(boundary.getBytes());
			out.write(NL.getBytes());
			out.write("Content-Disposition: form-data; name=\"".getBytes());
			out.write(key.getBytes());
			out.write("\"; filename=\"".getBytes());
			out.write(file.fileName.getBytes());
			out.write("\"".getBytes());
			out.write(NL.getBytes());
			out.write("Content-Type: ".getBytes());
			out.write(file.fileType.getBytes());
			out.write(NL.getBytes());
			out.write(NL.getBytes());
			out.write(file.fileData);
		}
		
		out.write(NL.getBytes());
		out.write(HH.getBytes());
		out.write(boundary.getBytes());
		out.write(HH.getBytes());
		out.write(NL.getBytes());
		
		return new ByteArrayInputStream(out.toByteArray());	
	}
	
	private String generateBoundary()
	{
		StringBuffer buffer = new StringBuffer(BOUNDARY_LENGTH);
		Random random = new Random(System.currentTimeMillis());
		for(int i = 0; i < BOUNDARY_LENGTH; i++)
		{
			switch(random.nextInt(3))
			{
			case 0:
				buffer.append(random.nextInt(10));
				break;
			case 1:
				buffer.append((char)(random.nextInt(26) + (int)'A'));
				break;
			case 2:
			default:
				buffer.append((char)(random.nextInt(26) + (int)'a'));
			}
		}
		return buffer.toString();
	}
}