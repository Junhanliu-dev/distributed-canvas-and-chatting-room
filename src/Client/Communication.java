package Client;

import boarduser.GraphObj;

public interface Communication
{
	public void sendDraw(GraphObj obj);
	
	public void sendChat(String message);
	
	public void windowClose();
	
	public void sendDeny(int ID);
	
	public void sendSwitch();
}
