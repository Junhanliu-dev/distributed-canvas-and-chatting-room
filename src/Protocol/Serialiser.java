/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocol;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import boarduser.GraphObj;

/**
 *
 * @author Rena
 */
public class Serialiser
{
    public static String serialize(GraphObj obj)
    	throws IOException
    {
    	List<GraphObj> objs = new ArrayList<GraphObj>();
    	objs.add(obj);
    	return serialize(objs);
    }
    
    public static String serialize(List<GraphObj> objs)
    	throws IOException
    {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
	    if (objs == null || objs.size() == 0)
	    {
	    	out.write(0);
	    }
	    else
	    {
	    	int objSize = objs.size();
//	    	ByteBuffer wrapper = ByteBuffer.allocate(4);
//	    	wrapper.putInt(objSize);
//	    	out.write(wrapper.array());
	    	
	    	int s = objSize/127;
	    	int y = objSize % 127;
	    	out.write(s);
	    	out.write(y);
	    	
	    	ObjectOutputStream stream = new ObjectOutputStream(out);
	    	for (GraphObj obj : objs)
	    	{
	    		obj.writeExternal(stream);
	    	}
	    	stream.flush();
	    }

	    return new String(out.toByteArray());
    }
    
    public static List<GraphObj> deserialize(String dataInputString)
    	throws IOException, ClassNotFoundException
    {
	    ByteArrayInputStream in = new ByteArrayInputStream(dataInputString.getBytes());

	    List<GraphObj> objs = new CopyOnWriteArrayList<GraphObj>();
	    
//	    byte [] sizeByte = new byte[4];
//	    in.read(sizeByte, 0, 4);
//	    ByteBuffer wrapper = ByteBuffer.wrap(sizeByte);
//	    int numObjs = wrapper.getInt();
	    int s = in.read();
	    int y = in.read();
	    int numObjs = s * 127 + y;
	    
	    if (numObjs > 0)
	    {
	    	ObjectInputStream stream = new ObjectInputStream(in);
	    	for (int i = 0; i < numObjs; i++)
	    	{
	    		GraphObj obj = new GraphObj();
	    		obj.readExternal(stream);
	    		
	    		objs.add(obj);
	    	}
	    }
		return objs;
	}
    
}
