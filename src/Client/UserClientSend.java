package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import Protocol.ProtocolData;


public class UserClientSend
{
	private DataOutputStream out;
	private UserClient client;
	
	public UserClientSend(UserClient client, DataOutputStream out)
	{
		this.out = out;
		this.client = client;
	}
	
	public void send(ProtocolData data)
	{
		try
		{
			out.writeUTF(data.getProtocolString());
		}
		catch (IOException e)
		{
			this.client.setConnected(false);
			client.close();
		}
	}
	
	public void setOut(DataOutputStream out)
	{
		this.out = out;
	}
	
	public void close()
	{
		try
		{
			this.out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
