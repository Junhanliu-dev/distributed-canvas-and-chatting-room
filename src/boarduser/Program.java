package boarduser;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import Client.ServerListener;
import Client.UserClient;
import Protocol.ProtocolData;

public class Program
{
	/**
	 * Aim: call the UI program to choose to be a User or Manager
	 */
	
	public static void main(String[] args)
	{
		ChoiceUI();
	}
	
	private static void runAsClient(String address, int port,String name) throws ConnectException, UnknownHostException, IOException
	{
		UserClient user = new UserClient(address,port);
		Thread userInCommuThread = new Thread(user);
		userInCommuThread.start();
		user.send(ProtocolData.createConnect(name));
	}
	
	private static void runAsManager(int port,String name,String canvasName)
	{
		ClientMns manager = new ClientMns(name);
		manager.readCanvasList();
		String cname = canvasName;	
		do
		{
			if(cname==null){System.exit(1);}
			if(cname.equals(""))
			{
				cname = JOptionPane.showInputDialog(null, "coundn`t be empty","Rename",JOptionPane.OK_CANCEL_OPTION);
				continue;
			}
			if(manager.checkCanvas(cname))
			{
				cname = JOptionPane.showInputDialog(null, "The canvas name is repeated","Rename",JOptionPane.OK_CANCEL_OPTION);
				continue;
			}
			break;
		}while(true);
		
		
		MainFrame mainframe = new MainFrame(cname,true,name,manager);
		mainframe.setVisible(true);
		
		ServerListener service = new ServerListener(port,mainframe,manager);
		Thread serviceThread = new Thread(service);
		serviceThread.start();
		
		mainframe.buildChannel(service);
	}
	
	private static void ChoiceUI()
	{
		do
		{
			Object[] options ={ "Manager", "Client" };  
			int choose = JOptionPane.showOptionDialog(null, "Launch as:", "Launch",JOptionPane.YES_NO_CANCEL_OPTION, 
					JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if(choose==0)
			{
				String[] argOptions = managerInputPanel();
				if(argOptions!=null)
				{
					runAsManager(Integer.parseInt(argOptions[0]),argOptions[1],argOptions[2]);
					return;
				}
				else
				{
					continue;
				}
				
			}
			else if(choose==1)
			{
				String[] argOptions = userInputPanel();
				if(argOptions!=null)
				{
					try {
						runAsClient(argOptions[0],Integer.parseInt(argOptions[1]),argOptions[2]);
					} catch (ConnectException e) {
						JOptionPane.showMessageDialog(null, "No Manager is running!");
						continue;
					} catch (UnknownHostException e) {
						JOptionPane.showMessageDialog(null, "Please check the host address!");
						continue;
					} catch(IOException e)
					{}
					return;
				}
				else
				{
					continue;
				}
			}
			else
			{
				break;
			}
		}while(true);
		
	}
	
	private static String[] userInputPanel()
	{
	    do
	    {
	    	JTextField nameField = new JTextField(20);
		    JTextField addressField = new JTextField(20);
		    JTextField port = new JTextField(20);
		    
		    JPanel myPanel = new JPanel();
		    myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		    myPanel.add(new JLabel("Your name:"));
		    myPanel.add(nameField);
		    myPanel.add(Box.createVerticalStrut(15)); // a spacer
		    myPanel.add(new JLabel("Server address:"));
		    myPanel.add(addressField);
		    myPanel.add(Box.createVerticalStrut(15)); // a spacer
		    myPanel.add(new JLabel("Server port:"));
		    myPanel.add(port);
		    int result = JOptionPane.showConfirmDialog(null, myPanel, 
		               "Please Input Your Info", JOptionPane.OK_CANCEL_OPTION);
		    
		    if(result == JOptionPane.OK_OPTION)
		    {
		    	try{
			    	Integer.parseInt(port.getText());
			    	return new String[]{addressField.getText(),port.getText(),nameField.getText()};
			    }catch(NumberFormatException e)
			    {
			    	JOptionPane.showMessageDialog(null, "Invalid port");
			    	continue;
			    }
		    	
		    }
		    else
		    {
		    	return null;
		    }
	    }while(true);
	    
	}
	
	private static String[] managerInputPanel()
	{
		do
		{
			JTextField nameField = new JTextField(20);
		    JTextField canvasField = new JTextField(20);
		    JTextField port = new JTextField(20);
		    JLabel label;
			try {
				label = new JLabel(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e1) {
				label = new JLabel("Unknown address");
			}
		    
		    JPanel myPanel = new JPanel();
		    myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		    myPanel.add(new JLabel("Your name:"));
		    myPanel.add(nameField);
		    myPanel.add(Box.createVerticalStrut(15)); // a spacer
		    myPanel.add(new JLabel("Canvas name:"));
		    myPanel.add(canvasField);
		    myPanel.add(Box.createVerticalStrut(15)); // a spacer
		    myPanel.add(new JLabel("Listening port:"));
		    myPanel.add(port);
		    myPanel.add(Box.createVerticalStrut(15)); // a spacer
		    myPanel.add(new JLabel("Your address:"));
		    myPanel.add(label);
		    int result = JOptionPane.showConfirmDialog(null, myPanel, 
		               "Please Input Your Info", JOptionPane.OK_CANCEL_OPTION);
		    if(result == JOptionPane.OK_OPTION)
		    {
		    	ServerSocket ss = null;
		    	try{
			    	int portValue = Integer.parseInt(port.getText());
			    	ss = new ServerSocket(portValue);
			    	return new String[]{port.getText(),nameField.getText(),canvasField.getText()};
			    }catch(NumberFormatException e)
			    {
			    	JOptionPane.showMessageDialog(null, "Invalid port");
			    	continue;
			    } catch (IOException e) {
			    	JOptionPane.showMessageDialog(null, "Port already in use");
			    	continue;
				} finally
			    {
					if(ss!=null){	
						try {
							ss.close();
						} catch (IOException e) {}
					}
			    }
		    }
		    else
		    {
		    	return null;
		    }
		}while(true);
		
	}
	
	private static String getServerHost()
	{
		Enumeration e;
		try {
			e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration ee = n.getInetAddresses();
			    while (ee.hasMoreElements())
			    {
			        InetAddress i = (InetAddress) ee.nextElement();
			        if(n.getName().matches("wlan.*"))
			        {
			        	return i.getHostAddress();
			        }
			    }
			}
		} catch (SocketException e1) {
			return "Unknown";
		}
		return "Unknown";
	}
}
