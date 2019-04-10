package Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import Protocol.ProtocolData;

public class ServerConnectionSend
{
	private ServerConnection connection;
	private DataOutputStream out;
	private static int strlen = 1000;
	
	public ServerConnectionSend(ServerConnection connection, OutputStream output)
	{
		this.connection = connection;
		this.out = new DataOutputStream(output);
	}
	
	public void send(ProtocolData data)
	{
		try
		{
			out.writeUTF(data.getProtocolString());
			out.flush();
		}
		catch (IOException e)
		{
			connection.close();
		}
	}
	
	public void sendUpdate(int ID, String content)
	{
		connection.send(ProtocolData.canvasUpdate(ID, content));
	}
	
	public void sendCanvas(ProtocolData data)
	{
		//byte[] m = p.getProtocolString().getBytes();
		String name = data.get(1);
		String content = data.get(2);
		int cl = content.length();
		int part = Math.floorDiv(cl,strlen)+1;
		//System.out.println(part);
		
		if(cl>strlen)
		{
			int i = 0;
			int strpart = 0;
			ProtocolData np = ProtocolData.canvasResponse(name, "", "start",part,0);
			this.send(np);
			while(i < part)
			{
				if(i==part-1)
				{
					np = ProtocolData.canvasResponse(name, content.substring(strpart),String.valueOf(i),cl-strpart,0);
					this.send(np);
					break;
				}else{
					np = ProtocolData.canvasResponse(name, content.substring(strpart, strpart+strlen),String.valueOf(i),strlen,0);
					//System.out.println(content.substring(strpart, strpart+strlen));
					strpart += strlen;
					i+=1;
					this.send(np);
				}
			}
			np = ProtocolData.canvasResponse(name, "", "end", -1,0);
			this.send(np);
		}else{
			ProtocolData np = ProtocolData.canvasResponse(name, content, "full", cl,0);
			this.send(np);
			
		}
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
