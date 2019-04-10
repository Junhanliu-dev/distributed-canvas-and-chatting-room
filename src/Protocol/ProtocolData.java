package Protocol;

import java.io.StringReader;
import java.io.StringWriter;
import javax.json.*;

public class ProtocolData
{
	JsonObject object;
	String[] data;
	ProtocolType type;
	
	public String get(int index)
	{
		if (data != null)
		{
			return data[index];
		}
		this.parseProtocolType();
		return data[index];
	}
	
	private ProtocolData(ProtocolType type, JsonObject obj)
	{
		this.object = obj;
		this.type = type;
	}
	
	public ProtocolData(String jsonString)
	{
		JsonReader reader = Json.createReader(new StringReader(jsonString));
		this.object = reader.readObject();
		reader.close();
		
		if (this.object != null)
		{
			parseProtocolType();
		}
	}
	
	public static ProtocolData createConnect(String name)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "connect");
		builder.add("argument", Json.createObjectBuilder()
				.add("name", name));
		return new ProtocolData(ProtocolType.CLIENT_CONNECT, builder.build());
	}
	
	public static ProtocolData denyConnect()
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "approve");
		builder.add("argument", Json.createObjectBuilder()
				.add("result","deny")
				.add("ID", "")
				.add("name", ""));
		return new ProtocolData(ProtocolType.SERVER_DENY, builder.build());
	}
	
	public static ProtocolData approveConnect(int ID,String name)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "approve");
		builder.add("argument", Json.createObjectBuilder()
				.add("result", "approved")
				.add("ID", String.valueOf(ID))
				.add("name", name)
				);
		return new ProtocolData(ProtocolType.SERVER_APPROVE, builder.build());
	}
	
	public static ProtocolData canvasRequest(int ID)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "canvasRequest");
		builder.add("argument", Json.createObjectBuilder()
				.add("ID", String.valueOf(ID)));
		return new ProtocolData(ProtocolType.CANVAS_REQUEST, builder.build());
	}
	
	public static ProtocolData canvasResponse(String canvasName,String canvas,String part,int length,int ID)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "canvasResponse");
		builder.add("argument", Json.createObjectBuilder()
				.add("ID", String.valueOf(ID))
				.add("canvasName", canvasName)
				.add("content", canvas)
				.add("part", part)
				.add("length", String.valueOf(length)));
		return new ProtocolData(ProtocolType.CANVAS_RESPONSE, builder.build());
	}
	
	public static ProtocolData chatRequest(int ID)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "chatRequest");
		builder.add("argument", Json.createObjectBuilder()
				.add("ID", String.valueOf(ID)));
		return new ProtocolData(ProtocolType.CHAT_REQUEST, builder.build());
	}
	
	public static ProtocolData chatResponse(String content)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "chatResponse");
		builder.add("argument", Json.createObjectBuilder()
				.add("content", content));
		return new ProtocolData(ProtocolType.CHAT_RESPONSE, builder.build());
	}
	
	public static ProtocolData chatUpdate(int ID,String content)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "chatUpdate");
		builder.add("argument", Json.createObjectBuilder()
				.add("ID", String.valueOf(ID))
				.add("content", content));
		return new ProtocolData(ProtocolType.CHAT_UPDATE, builder.build());
	}
	
	public static ProtocolData canvasUpdate(int ID,String content)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "canvasUpdate");
		builder.add("argument", Json.createObjectBuilder()
				.add("ID", String.valueOf(ID))
				.add("content", content)
				);
		return new ProtocolData(ProtocolType.CANVAS_UPDATE, builder.build());
	}
	
	public static ProtocolData userlistRequest(int ID)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "userlistRequest");
		builder.add("argument", Json.createObjectBuilder()
				.add("ID",String.valueOf(ID)));
		return new ProtocolData(ProtocolType.USER_LIST_REQUEST, builder.build());
	}
	
	public static ProtocolData userlistResponse(String content)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "userlistResponse");
		builder.add("argument", Json.createObjectBuilder()
				.add("content", content));
		return new ProtocolData(ProtocolType.USER_LIST_RESPONSE, builder.build());
	}
	
	public static ProtocolData offline(int ID)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "offline");
		builder.add("argument", Json.createObjectBuilder()
				.add("ID", String.valueOf(ID)));
		return new ProtocolData(ProtocolType.CLIENT_OFFLINE, builder.build());
	}
	
	public static ProtocolData exit()
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "exit");
		return new ProtocolData(ProtocolType.SERVER_EXIT, builder.build());
	}
	
	public static ProtocolData deny(int ID)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "deny");
		builder.add("argument", Json.createObjectBuilder()
				.add("ID", String.valueOf(ID)));
		return new ProtocolData(ProtocolType.SERVER_DENY, builder.build());
	}
	
	public static ProtocolData switchCanvas(String content,int ID)
	{
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("type", "switchCanvas");
		builder.add("argument", Json.createObjectBuilder()
				.add("ID",ID)
				.add("content", content));
		return new ProtocolData(ProtocolType.CANVAS_SWITCH, builder.build());
	}
	
	public String getProtocolString()
	{
		if(this.object != null)
		{
			StringWriter stringwriter = new StringWriter();
			JsonWriter writer = Json.createWriter(stringwriter);
			writer.writeObject(this.object);
			writer.close();
			return stringwriter.toString();
		}else{
			return "";
		}
		
	}
	
	public ProtocolType getType()
	{
		if (this.object != null)
		{
			switch (this.object.getString("type"))
			{
			case "connect":
				return ProtocolType.CLIENT_CONNECT;
			
			case "approve":
				return ProtocolType.SERVER_APPROVE;
			
			case "deny":
				return ProtocolType.SERVER_DENY;
				
				
			case "canvasRequest":
				return ProtocolType.CANVAS_REQUEST;
				
			case "canvasResponse":
				return ProtocolType.CANVAS_RESPONSE;
				
			case "canvasUpdate":
				return ProtocolType.CANVAS_UPDATE;
				
			case "canvasSwitch":
				return ProtocolType.CANVAS_SWITCH;
				
				
			case "chatRequest":
				return ProtocolType.CHAT_REQUEST;
				
			case "chatResponse":
				return ProtocolType.CHAT_RESPONSE;
				
			case "chatUpdate":
				return ProtocolType.CHAT_UPDATE;
				
				
			case "offline":
				return ProtocolType.CLIENT_OFFLINE;
				
			case "exit":
				return ProtocolType.SERVER_EXIT;
				
			case "userlistRequest":
				return ProtocolType.USER_LIST_REQUEST;
			
			case "userlistResponse":
				return ProtocolType.USER_LIST_RESPONSE;
			default:
				System.out.println(this.object.getString("type"));
				return ProtocolType.NONE;
			}
		}
		return ProtocolType.NONE;
	}
	
	private void parseProtocolType()
	{
		String type = this.object.getString("type");
		JsonObject arg = this.object.getJsonObject("argument");
		
		if (type.equals("switchCanvas"))
		{
			data = new String[]{arg.getString("content")};
		}
		
		switch (getType())
		{
		case SERVER_APPROVE:
			data = new String[]{arg.getString("result"),arg.getString("ID"),arg.getString("name")};
			break;
			
		case CANVAS_REQUEST:
			data = new String[]{arg.getString("ID")};
			break;
			
		case CANVAS_RESPONSE:
			data = new String[]{arg.getString("ID"),arg.getString("canvasName"),arg.getString("content"),arg.getString("part"),arg.getString("length")};
			break;
			
		case CANVAS_UPDATE:
			data = new String[]{arg.getString("ID"),arg.getString("content")};
			break;
			
		case CHAT_REQUEST:
			data = new String[]{arg.getString("ID")};
			break;
			
		case CHAT_RESPONSE:
			data = new String[]{arg.getString("content")};
			break;
			
		case CHAT_UPDATE:
			data = new String[]{arg.getString("ID"),arg.getString("content")};
			break;
			
		case CLIENT_CONNECT:
			data = new String[]{arg.getString("name")};
			break;
			
		case SERVER_DENY:
			data = new String[]{arg.getString("ID")};
			break;
			
		case SERVER_EXIT:
			break;
			
		case CLIENT_OFFLINE:
			data = new String[]{arg.getString("ID")};
			break;
			
		case USER_LIST_RESPONSE:
			data = new String[]{arg.getString("content")};
			break;
			
		case USER_LIST_REQUEST:
			data = new String[]{arg.getString("ID")};
			break;
			
		case CANVAS_SWITCH:
			data = new String[]{arg.getString("ID"),arg.getString("content")};
			break;
			
		default:
			break;
		}
	}
	
	public String getProtocolArgumentsAsString()
	{
		if(this.object != null)
		{
			 this.object.getString("argument");
		}
		return "";
	}
}
