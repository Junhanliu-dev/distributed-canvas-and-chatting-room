package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import Protocol.ProtocolData;
import Protocol.ProtocolType;

import javax.json.JsonException;
import javax.swing.*;
import Protocol.Serialiser;
import boarduser.GraphObj;

public class ServerConnectionReceive
{
	private ServerConnection connection;
	private ServerListener server;
	private DataInputStream in;
	
	
	public ServerConnectionReceive(ServerConnection connection, ServerListener server, InputStream input)
	{
		this.connection = connection;
		this.server = server;
		this.in = new DataInputStream(input);
	}
	
	public void readData()
	{
		try
		{
			if (in.available() > 0)
			{
				String line = in.readUTF();
				this.receivedProtocol(new ProtocolData(line));
			}
		}
		catch (IOException e)
		{
			connection.close();
		}
	}
	
	public void receivedProtocol(ProtocolData protocol)
	{
		try
		{
			switch (protocol.getType())
			{
			case CLIENT_CONNECT:
				onClientConnect(protocol);
				break;
				
			case CANVAS_REQUEST:
				onCanvasRequest(protocol);
				break;
			case CANVAS_UPDATE:
				onCanvasUpdate(protocol);
				break;
				
			case CHAT_UPDATE:
				onChatUpdate(protocol);
				break;
				
			case CLIENT_OFFLINE:
				onClientOffline(protocol);
				break;
				
			case USER_LIST_REQUEST:
				onUserListRequest(protocol);
				break;
				
			case SERVER_APPROVE:
			case SERVER_DENY:
			case SERVER_EXIT:
			case USER_LIST_RESPONSE:
				System.out.println("Invalid protocol received: " + protocol.getType());
				break;
				
			default:
				System.out.println("Unknown protocol received");
				break;
			}
		}
		catch (JsonException e)
		{
			e.printStackTrace();
		}
	}
	
	private void onClientConnect(ProtocolData data)
	{
		String name = data.get(0);
		
		if(server.checkName(name))
		{
			connection.send(ProtocolData.deny(-1));
			return;
		}
		
		int choice = JOptionPane.showOptionDialog(null,
			"Client " + name + " is coming! Approve?","Confirm Client",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE, 
			null, null, null);
		
		//TODO: check name?
		if (choice == JOptionPane.YES_OPTION)
		{
			int ID = server.getApproval(name, connection);
			connection.send(ProtocolData.approveConnect(ID, name));
		}
		else
		{
			connection.send(ProtocolData.deny(-2));
			connection.stopListening();
		}
	}
	
	private void onCanvasRequest(ProtocolData data)
	{
		try
		{
			System.out.println("Server send: "+server.getCurrentCanvas().size());
			String canvas = Serialiser.serialize(server.getCurrentCanvas());
			
			//String ID = data.get(0);

			ProtocolData response = ProtocolData.canvasResponse(
				server.getCanvasName(),
				canvas,
				"",
				-1,
				0);
			
			connection.sendCanvas(response);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void onChatUpdate(ProtocolData data)
	{
		//String content = data.get(1);
		//String ID = data.get(0);
		this.server.pipeline.putMessageUpdate(data);
		ProtocolData tmp = this.server.pipeline.getMessageUpdate();
		
		server.broadCast(tmp, ProtocolType.CHAT_UPDATE);
		
		//TODO:update the UI
		this.server.updateChatUI(tmp.get(1));
	}
	
	private void onCanvasUpdate(ProtocolData data)
	{
		//TODO: save the draw and broadcast the draw
		this.server.pipeline.putCanvasUpdate(data);
		ProtocolData tmp = this.server.pipeline.getCanvasUpdate();
		String content = tmp.get(1);
		int ID = Integer.parseInt(tmp.get(0));
		server.broadCast(tmp, ProtocolType.CANVAS_UPDATE);
		//TODO: update UI
		try
		{
			GraphObj obj = Serialiser.deserialize(content).get(0);
			server.updateUI(obj,ID);
		}
		catch (ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void onClientOffline(ProtocolData data)
	{
		int ID = Integer.parseInt(data.get(0));

		//TODO:remove the user from the list
		server.offline(ID);
		//server.broadCast(null, ProtocolType.USER_LIST_RESPONSE);
	}
	
	private void onUserListRequest(ProtocolData data)
	{
		int ID = Integer.parseInt(data.get(0));
		connection.send(ProtocolData.userlistResponse(this.server.getUserList()));
	}
	
	public void close()
	{
		try
		{
			this.in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
