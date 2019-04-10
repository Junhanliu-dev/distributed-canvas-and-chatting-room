package Client;

import java.util.concurrent.ConcurrentLinkedQueue;
import Protocol.ProtocolData;

public class UpdateQueue {

	private ConcurrentLinkedQueue<ProtocolData> canvasUpdate;
	private ConcurrentLinkedQueue<ProtocolData> messageUpdate; 
	
	public UpdateQueue()
	{
		this.canvasUpdate = new ConcurrentLinkedQueue<ProtocolData>();
		this.messageUpdate = new ConcurrentLinkedQueue<ProtocolData>();
	}
	
	public ProtocolData getCanvasUpdate()
	{
		ProtocolData update = this.canvasUpdate.poll();
		if(update == null)
		{
			//TODO
			return null;
		}else
		{
			return update;
		}
	}
	
	public ProtocolData getMessageUpdate()
	{
		ProtocolData update = this.messageUpdate.poll();
		if(update == null)
		{
			//TODO
			return null;
		}else
		{
			return update;
		}
	}
	
	public boolean putCanvasUpdate(ProtocolData d)
	{
		return this.canvasUpdate.offer(d);
	}
	
	public boolean putMessageUpdate(ProtocolData d)
	{
		return this.messageUpdate.offer(d);
	}
}
