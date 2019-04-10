package Client;

import java.io.IOException;
import java.net.Socket;

import Protocol.ProtocolData;

public class ServerConnection extends Thread
{
	private Boolean isRunning = false;
	
	private ServerConnectionReceive in;
	private ServerConnectionSend out;
	
	public ServerConnectionSend getOutput()
	{
		return this.out;
	}
	
	public ServerConnection(Socket client, ServerListener server)
	{
		try
		{
			this.out = new ServerConnectionSend(this, client.getOutputStream());
			this.in = new ServerConnectionReceive(this, server, client.getInputStream());
		}
		catch (IOException e)
		{
			close();
		}
	}
	
	public void run()
	{
		this.isRunning = true;
		while (isRunning)
		{
			in.readData();
		}
	}
	
	public void stopListening()
	{
		this.isRunning = false;
		this.close();
		this.interrupt();
	}
	
	public void send(ProtocolData data)
	{
		System.out.println("server > client: " + data.getType());
		out.send(data);
	}
	
	public void sendCanvas(ProtocolData data)
	{
		System.out.println("server > client: " + data.getType());
		
		out.sendCanvas(data);
	}
	
	public void close()
	{
		this.in.close();
		this.out.close();
	}
}
