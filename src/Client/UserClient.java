package Client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import Protocol.ProtocolData;
import Protocol.Serialiser;
import boarduser.GraphObj;

public class UserClient extends Thread implements Communication
{
	private TcpClient client;
	
	private int ID = -1;
	private String name;
	
	private Boolean isRunning;
	private UserClientReceive in;
	private UserClientSend out;
	private Boolean isConnected = false;
	
	private String address;
	private int port;
	
	public TcpClient getClient()
	{
		return client;
	}
	
	public UserClientSend getOutput()
	{
		return out;
	}
	
	public int getID()
	{
		return this.ID;
	}
	
	public void setID(int ID)
	{
		this.ID = ID;
	}
	
	public void setClientName(String name)
	{
		this.name = name;
	}
	
	public String getClientName()
	{
		return this.name;
	}
	
	public UserClient(String address, int port) throws ConnectException, UnknownHostException, IOException
	{
		this.client = new TcpClient();
		this.client.connect(address, port);
		this.isConnected = true;
		this.address = address;
		this.port = port;
		this.isRunning = false;
		this.out = new UserClientSend(this, client.getOutput());
		this.in = new UserClientReceive(this, client.getInput());
	}
	
	public void run()
	{
		this.isRunning = true;
		while (isRunning)
		{
			in.readData();
		}
	}
	
	private void stopListening()
	{
		this.isRunning = false;
		this.interrupt();
	}
	
	
	public void reconnect() throws UnknownHostException,ConnectException,IOException
	{
		this.client.connect(address, port);
		this.setConnected(true);
		this.out.setOut(client.getOutput());
		this.in.setIn(client.getInput());
	}
	
	public boolean getConnected()
	{
		return this.isConnected;
	}
	
	public void setConnected(boolean t)
	{
		this.isConnected = t;
	}
	
	public void send(ProtocolData data)
	{
		System.out.println("client > server: " + data.getType());
		out.send(data);
	}
	
	public boolean checkUser()
	{
		return this.ID != -1;
	}
	
	public void close()
	{
		this.stopListening();
		this.in.close();
		this.out.close();	
	}
	
	@Override
	public void sendDraw(GraphObj obj)
	{
		try
		{
			String objString = Serialiser.serialize(obj);
			send(ProtocolData.canvasUpdate(getID(), objString));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void sendChat(String message)
	{
		send(ProtocolData.chatUpdate(getID(), message));
	}

	@Override
	public void windowClose()
	{
		send(ProtocolData.offline(getID()));
	}

	@Override
	public void sendSwitch()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void sendDeny(int ID) {
		// TODO Auto-generated method stub
		
	}
}
