package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient
{
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	public DataInputStream getInput()
	{
		if (socket == null)
		{
			throw new IllegalStateException();
		}
		return in;
	}
	
	public DataOutputStream getOutput()
	{
		if (socket == null)
		{
			throw new IllegalStateException();
		}
		return out;
	}
	
	public TcpClient()
	{
		this.socket = null;
		this.in = null;
		this.out = null;
	}
	
	public void connect(String address, int port) throws UnknownHostException,ConnectException,IOException
	{
		socket = new Socket(address, port);
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		
	}
	
	public void close() throws IOException
	{
		socket.close();
		in.close();
		out.close();
	}
}
