package darkforest.mobile;

import java.io.IOException;
import java.io.Reader;

public class SimpleReaderTokenizer 
{
	private final Reader reader;
	
	private final char delimeter;
	
	public SimpleReaderTokenizer(Reader reader, char delimeter)
	{
		this.reader = reader;
		this.delimeter = delimeter;
	}
	
	public String getNextToken() 
	throws IOException
	{
		StringBuffer buf = new StringBuffer();
		int c;
		while((c = reader.read()) != -1 && ((char)c) != delimeter)
			buf.append((char)c);
		
		if(c == -1 && buf.length() == 0)
			return null;
		
		return buf.toString();
	}
}
