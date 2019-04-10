package Client;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import Protocol.ProtocolData;
import Protocol.ProtocolType;
import boarduser.MainFrame;
import boarduser.GraphObj;
import boarduser.ClientMns;
import Protocol.Serialiser;
import boarduser.Encrypter;
import boarduser.JsonConverter;

public class ServerListener implements Communication, Runnable
{
	private int port;
	private Boolean isRunning = false;
	private Hashtable<Integer,ServerConnection> UserList;
	private MainFrame mainframe;
	private ClientMns manager;
	public UpdateQueue pipeline;
	
	public ServerListener(int port,MainFrame mainframe,ClientMns manager)
	{
		this.port = port;
		this.mainframe = mainframe;
		this.manager = manager;
		this.UserList = new Hashtable<Integer,ServerConnection>();
		this.pipeline = new UpdateQueue();
	}
	
	public void run()
	{
		this.isRunning = true;
		this.startListening();
	}
	
	private void startListening()
	{
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		ServerSocket service = null;
	
		//TODO: add to retry listening
		try
		{
			service = factory.createServerSocket(port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	
		while(isRunning)
		{
			Socket client = null;
			try
			{
				client = service.accept();				
				//TODO: The incomming socket may not from the same socket if two remote client try to connect to server together
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			ServerConnection connection = new ServerConnection(client, this);
			connection.start();	
		}
	}
	
	public void broadCast(ProtocolData data, ProtocolType type)
	{
		int currentID = Integer.parseInt(data.get(0));
		System.out.println("server > all: " + type);
		for (Entry<Integer, ServerConnection> entry : this.UserList.entrySet())
		{
			if(entry.getKey() != currentID)
			{
				ServerConnectionSend out = entry.getValue().getOutput();
				
				switch (type)
				{
				case CHAT_UPDATE:
					out.send(data);
					break;
					
				case SERVER_EXIT:
					out.send(data);
					break;
					
				case CANVAS_UPDATE:
					int ID = Integer.parseInt(data.get(0));
					String content = data.get(1);
					out.sendUpdate(ID, content);
					//out.sendCanvas(data);
					break;
					
				case CANVAS_SWITCH:
					out.sendCanvas(data);
					break;
					
				case USER_LIST_REQUEST:
					ProtocolData userdata = ProtocolData.userlistResponse(this.getUserList());
					out.send(userdata);
					break;
					
				case CANVAS_RESPONSE:
					out.sendCanvas(data);
					break;
					
				default:
					break;
				}
			}
			
		}
	}
	
	//Callback	
	public int getApproval(String name, ServerConnection connection)
	{
		//TODO: get an ID of this name
		int ID = this.manager.grantAccess(name);
		UserList.put(ID, connection);
		this.mainframe.updateUserList();
		this.broadCast(ProtocolData.userlistRequest(ID), ProtocolType.USER_LIST_REQUEST);
		return ID;
	}
	
	public boolean checkName(String name)
	{
		return this.manager.isUnique(name);
	}
	
	public List<GraphObj> getCurrentCanvas()
	{
		return this.mainframe.getUserPanel().getCurrentCanvas().getCurrentCanvasContent();
	}
	
	public void offline(int ID)
	{
		this.UserList.remove(ID);
		//TODO: remove the user from RENA
		this.manager.denyAccess(ID);
		//TODO: update userlist UI
		this.broadCast(ProtocolData.userlistRequest(0), ProtocolType.USER_LIST_REQUEST);
		this.mainframe.updateUserList();
		
	}
	
	public void stopListening()
	{
		this.isRunning = false;
	}
	
	public void updateUI(GraphObj obj,int ID)
	{
		mainframe.getUserPanel().getCurrentCanvas().receiveDrawing(obj);
		this.mainframe.getUserPanel().setEditor(this.mainframe.getUserName(ID));
	}
	
	public void updateChatUI(String content)
	{
		this.mainframe.upodateMessage(content);
	}
	
	public void updateUserList(String content)
	{
		this.mainframe.updateUserList(content); 
	}
	
	public String getCanvasName()
	{
		return this.mainframe.getUserPanel().getCurrentCanvas().getCanvasName();
	}
	
	public String getUserList()
	{
		return JsonConverter.converToJson(this.manager.getUserInfo());
	}

	@Override
	public void sendDraw(GraphObj obj)
	{
		try
		{
			String content = Serialiser.serialize(obj);
			
			ProtocolData data = ProtocolData.canvasUpdate(0, content);
			this.broadCast(data, ProtocolType.CANVAS_UPDATE);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void sendChat(String message)
	{
		ProtocolData data = ProtocolData.chatUpdate(0, message);
		this.broadCast(data, ProtocolType.CHAT_UPDATE);
	}
	
	@Override
	public void windowClose()
	{
		if (!this.UserList.isEmpty())
		{
			ProtocolData data = ProtocolData.exit();
			for (ServerConnection connection : this.UserList.values())
			{
				connection.getOutput().send(data);
			}
		}
	}
	
	@Override
	public void sendDeny(int ID)
	{
		ServerConnection connection = this.UserList.get(ID);
		
		connection.getOutput().send(ProtocolData.deny(-2));
		this.broadCast(ProtocolData.userlistRequest(0), ProtocolType.USER_LIST_REQUEST);
		
		connection.close();
		this.UserList.remove(ID);
	}
	
	@Override
	public void sendSwitch()
	{
		try
		{
			System.out.println("Server switch send: " + this.getCurrentCanvas().size());
			String canvas = Serialiser.serialize(this.getCurrentCanvas());
			String name = this.getCanvasName();
			ProtocolData data = ProtocolData.canvasResponse(name, canvas, "full", -1,0);
			this.broadCast(data, ProtocolType.CANVAS_RESPONSE);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
