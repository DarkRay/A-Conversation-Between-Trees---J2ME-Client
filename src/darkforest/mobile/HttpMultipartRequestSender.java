package darkforest.mobile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class HttpMultipartRequestSender
{
	private final String url;
	
	private final HttpMultipartRequest request;
	
	public HttpMultipartRequestSender(String url, HttpMultipartRequest request)
	{
		this.url = url;
		this.request = request;
	}
	
	public int doRequest()
	throws IOException
	{
		HttpConnection connection = null;
		try 
		{
			connection = (HttpConnection) Connector.open(url);
			connection.setRequestProperty("Content-Type", 
					"multipart/form-data; boundary=" + request.getBoundary());
			connection.setRequestMethod(HttpConnection.POST);
			
			OutputStream out = connection.openOutputStream();
			InputStream requestStream = request.getRequest();
			int bufSize = 1024;
			byte[] buf = new byte[bufSize];
			int read;
			while((read = requestStream.read(buf)) != -1)
				out.write(buf, 0, read);
			out.close();
			requestStream.close();
						
			return connection.getResponseCode();
		} 
		finally 
		{
			if(connection != null)
				connection.close();
		}
	}
}
