package Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.json.JsonException;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Protocol.ProtocolData;
import Protocol.Serialiser;
import boarduser.GraphObj;
import boarduser.MainFrame;

public class UserClientReceive
{
	private DataInputStream in;
	
	private MainFrame mainframe;
	private UserClient client;
	private String[] content;
	
	public UserClientReceive(UserClient client, DataInputStream in)
	{
		this.client = client;
		this.in = in;
		
		this.mainframe = null;
	}
	
	public void readData()
	{
		try
		{
			if (this.in.available() > 0)
			{
				//TODO: handle chatIn, display on my board
				String line = this.in.readUTF();
				this.handleMessage(line);
			}
		}
		catch (IOException e)
		{
			client.close();
		}
	}
	
	private void handleMessage(String message)
	{
		try
		{
			ProtocolData data = new ProtocolData(message);
			switch (data.getType())
			{
			case SERVER_APPROVE:
				handleApprove(data);
				break;
				
			case SERVER_DENY:
				handleDeny(data);
				break;
				
			case CANVAS_RESPONSE:
				handleCanvasResponse(data);
				break;
				
			case CANVAS_UPDATE:
				handleCanvasUpdate(data);
				break;
				
			case CHAT_RESPONSE:
				//this.handleChatResponse(data);
				break;
				
			case CHAT_UPDATE:
				handleChatUpdate(data);
				break;
				
			case SERVER_EXIT:
				handleExit();
				break;
				
			case USER_LIST_RESPONSE:
				handleUserlist(data);
				break;
				
			case CLIENT_CONNECT:
			case CANVAS_REQUEST:
			case CHAT_REQUEST:
				System.out.println("Invalid protocol received: " + data.getType());
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
	
	public void setIn(DataInputStream in)
	{
		this.in = in;
	}
	
	private void handleApprove(ProtocolData data)
	{
		//TODO: parse arguments from protocol
		String approve = data.get(0);
		if(approve.equals("approved"))
		{
			int ID = Integer.parseInt(data.get(1));
			String name = data.get(2);
			this.client.setID(ID);
			this.client.setName(name);
			if(this.mainframe !=null)
			{
				this.mainframe.setUserID(client.getID());
			}
			client.send(ProtocolData.canvasRequest(client.getID()));
		}else{
			JOptionPane.showMessageDialog(null,"You have been rejected","Rejected",
					JOptionPane.WARNING_MESSAGE);
			this.client.close();
			System.exit(1);
		}
	}
	
	
	private void handleCanvasResponse(ProtocolData data)
	{
		String canvasName = data.get(2);
		//TODO: update the canvas to UI
		String result = this.combineContent(data);
		//System.out.println("handleResult: " + result);
		if(!result.equals("$$"))
		{
			try
			{
				this.content = null;
				List<GraphObj> graphList = Serialiser.deserialize(result);
				System.out.println("client: received " + graphList.size() + " object(s)");
				if(this.mainframe==null)
				{
					this.mainframe = new MainFrame(canvasName,false,this.client.getName());
					this.mainframe.buildChannel(client);
					this.mainframe.setUserID(client.getID());
					client.send(ProtocolData.userlistRequest(client.getID()));
				}
				
				List<GraphObj> list = new CopyOnWriteArrayList<GraphObj>();
				for (GraphObj obj : graphList)
				{
					list.add(obj);
				}
				this.mainframe.getUserPanel().getCurrentCanvas().reloadCanvasContent(list);
				this.mainframe.setVisible(true);
				
			}
			catch (ClassNotFoundException | IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void handleChatUpdate(ProtocolData data)
	{
		//String ID = data.get(0);
		String content = data.get(1);
		this.mainframe.upodateMessage(content);
	}
	
	private void handleCanvasUpdate(ProtocolData data)
	{
		int ID = Integer.parseInt(data.get(0));
		String content = data.get(1);
		try
		{
			GraphObj obj = Serialiser.deserialize(content).get(0);
			
			this.mainframe.getUserPanel().getCurrentCanvas().receiveDrawing(obj);
			this.mainframe.getUserPanel().setEditor(this.mainframe.getUserName(ID));
		}
		catch (ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleUserlist(ProtocolData data)
	{
		String content = data.get(0);
		this.mainframe.updateUserList(content);
	}
	
	private void handleExit()
	{
		//TODO: close the client and show a message
		client.setConnected(false);
		client.close();
		do
		{
			if(!client.getConnected())
			{
				Object[] options ={"Reconnect","Cancel"};
				int choice = JOptionPane.showOptionDialog(null,"Server close! Reconnect?","Reconnect",
						JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
				if(choice==0)
				{
					try {
						client.reconnect();
						client.start();
						this.client.send(ProtocolData.createConnect(client.getName()));
						this.client.send(ProtocolData.userlistRequest((client.getID())));
					} catch (ConnectException e) {
						continue;
					}catch (IOException e) {
						e.printStackTrace();
					}
				}
				else
				{
					this.client.close();
					System.exit(1);
				}
			}
		}while(true);
	
	}
	
	private String combineContent(ProtocolData data)
	{
		String contents = data.get(2);
		String part = data.get(3);
		//System.out.println("Client Receive Part: "+part);
		String length = data.get(4);

		if (part.equals("full"))
		{
			return contents;
		}
		else if (part.equals("start"))
		{
			this.content = new String[Integer.parseInt(length)];
			return "$$";
		}
		else if (part.equals("end"))
		{
			String c = "";
			for(String s: this.content){
				c+=s;
			}
			return c;
		}
		else
		{
			this.content[Integer.parseInt(part)] = contents;
			return "$$";
		}
	}
	
	private void handleDeny(ProtocolData data)
	{
		if(data.get(0).equals("-1"))
		{
			String input = JOptionPane.showInputDialog(null,"Your name is repeated","Rename",
					JOptionPane.WARNING_MESSAGE);
			client.send(ProtocolData.createConnect(input));
		}
		else
		{
			JOptionPane.showMessageDialog(null,"You have been kick by the server","Kicked",
					JOptionPane.WARNING_MESSAGE);
			System.exit(1);
		}
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
