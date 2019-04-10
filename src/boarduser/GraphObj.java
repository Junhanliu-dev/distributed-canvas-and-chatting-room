package boarduser;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class GraphObj implements Externalizable {

	public enum ShapeType
	{
		NONE,
		LINE,
		RECTANGLE,
		TEXT,
		CIRCLE,
		OVAL
	};
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Shape shape = null;
	private String text = "";
	private int x1, y1;
	private Color strokeColor = Color.BLACK;
	private Color shapeFill = Color.black;

	public GraphObj()
	{
		//for serialization
	}
	
	public GraphObj(Shape shape, Color shapeColor)
	{

		this.shape = shape;
		shapeFill = shapeColor;

	}

	public GraphObj(String text, int x1, int y1) {
		this.text = text;
		this.x1 = x1;
		this.y1 = y1;

	}

	public int getX1() {

		return x1;

	};

	public int getY1() {

		return y1;

	}

	public Shape getShape() {

		return shape;

	}

	public String getText() {

		return text;

	}

	public Color getStroke() {

		return strokeColor;

	}

	public Color getFillColor() {

		return shapeFill;

	}

	@Override
	public void readExternal(ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		ShapeType shapeType = (ShapeType)in.readObject();
		if (shapeType == ShapeType.LINE)
		{
			Line2D.Float line = new Line2D.Float();
			line.x1 = Float.parseFloat(in.readUTF());
			line.x2 = Float.parseFloat(in.readUTF());
			line.y1 = Float.parseFloat(in.readUTF());
			line.y2 = Float.parseFloat(in.readUTF());
			strokeColor = new Color(Integer.parseInt(in.readUTF()));
			shapeFill = new Color(Integer.parseInt(in.readUTF()));
			shape = line;
			//System.out.println("read pos: " + line.x1+" "+line.y1+" "+line.x2+" "+line.y2);
		}
		else if (shapeType == ShapeType.RECTANGLE)
		{
			Rectangle2D.Float rectangle = new Rectangle2D.Float();
			rectangle.height = Float.parseFloat(in.readUTF());
			rectangle.width = Float.parseFloat(in.readUTF());
			rectangle.x = Float.parseFloat(in.readUTF());
			rectangle.y = Float.parseFloat(in.readUTF());
			strokeColor = new Color(Integer.parseInt(in.readUTF()));
			shapeFill = new Color(Integer.parseInt(in.readUTF()));
			shape = rectangle;
		}
		else if (shapeType == ShapeType.OVAL)
		{
			Ellipse2D.Float oval = new Ellipse2D.Float();
			oval.height = Float.parseFloat(in.readUTF());
			oval.width = Float.parseFloat(in.readUTF());
			oval.x = Float.parseFloat(in.readUTF());
			oval.y = Float.parseFloat(in.readUTF());
			strokeColor = new Color(Integer.parseInt(in.readUTF()));
			shapeFill = new Color(Integer.parseInt(in.readUTF()));
			shape = oval;
			//System.out.println("read pos: " + oval.x+" "+oval.y);
		}
		else if (shapeType == ShapeType.TEXT)
		{
			text = in.readUTF();
			x1 = Integer.parseInt(in.readUTF());
			y1 = Integer.parseInt(in.readUTF());
		}
		else
		{
			System.out.println("GraphObj.readExternal - unknown shape type");
			shape = null;
		}
		
	}

	@Override
	public void writeExternal(ObjectOutput out)
		throws IOException
	{
		if (shape instanceof Line2D.Float)
		{
			out.writeObject(ShapeType.LINE);
			
			Line2D.Float line = (Line2D.Float)shape;
			out.writeUTF(String.valueOf(line.x1));
			out.writeUTF(String.valueOf(line.x2));
			out.writeUTF(String.valueOf(line.y1));
			out.writeUTF(String.valueOf(line.y2));
			//System.out.println("write pos: " + line.x1+" "+line.y1+" "+line.x2+" "+line.y2);
			out.writeUTF(String.valueOf(strokeColor.getRGB()));
			out.writeUTF(String.valueOf(shapeFill.getRGB()));
		}
		else if (shape instanceof Rectangle2D.Float)
		{
			out.writeObject(ShapeType.RECTANGLE);
			
			Rectangle2D.Float rectangle = (Rectangle2D.Float)shape;
			out.writeUTF(String.valueOf(rectangle.height));
			out.writeUTF(String.valueOf(rectangle.width));
			out.writeUTF(String.valueOf(rectangle.x));
			out.writeUTF(String.valueOf(rectangle.y));
			out.writeUTF(String.valueOf(strokeColor.getRGB()));
			out.writeUTF(String.valueOf(shapeFill.getRGB()));
		}
		else if (shape instanceof Ellipse2D.Float)
		{
			out.writeObject(ShapeType.OVAL);
			
			Ellipse2D.Float oval = (Ellipse2D.Float)shape;
			out.writeUTF(String.valueOf(oval.height));
			out.writeUTF(String.valueOf(oval.width));
			out.writeUTF(String.valueOf(oval.x));
			out.writeUTF(String.valueOf(oval.y));
			//System.out.println("write pos: " + oval.x+" "+oval.y);
			out.writeUTF(String.valueOf(strokeColor.getRGB()));
			out.writeUTF(String.valueOf(shapeFill.getRGB()));
		}
		else if(shape == null && !text.equals(""))
		{
			out.writeObject(ShapeType.TEXT);
			out.writeUTF(text);
			out.writeUTF(String.valueOf(x1));
			out.writeUTF(String.valueOf(y1));
		}
		else
		{
			out.writeObject(ShapeType.NONE);			
		}
		
	}
}
