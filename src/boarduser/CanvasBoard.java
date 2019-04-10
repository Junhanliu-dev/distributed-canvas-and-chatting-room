package boarduser;
import Client.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class CanvasBoard extends JComponent {
	// ArrayLists that contain each shape drawn along with
	// that shapes stroke and fill

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private Client client;
	private String canvasName;
	List<GraphObj> shapes;

	Point drawStart, drawEnd;
	private String shape ="Line";
	private Color color;
	public Communication channel;
	
	// Monitors events on the drawing area of the frame

	public CanvasBoard(String canvasName, CopyOnWriteArrayList<GraphObj> contents) {
		// this.client = client;
		this.canvasName = canvasName;
		this.shapes = contents;
		initMouseListener();
		repaint();
	}

	public String getCanvasName() {
		return canvasName;
	}

	public List<GraphObj> getCurrentCanvasContent(){
		return this.shapes;
	}
	
	// Super mess
	private void initMouseListener() {

		this.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {

				// When the mouse is pressed get x & y position
				UserPanel.setEditor("You");

				drawStart = new Point(e.getX(), e.getY());
				drawEnd = drawStart;

				int x = e.getX();
				int y = e.getY();
				
				Shape aShape = null;
				GraphObj graohToDraw = null;
				if ("Pen".equals(shape)) {

					int penSize = UserPanel.getSlideValue();

					aShape = drawPen(x, y, penSize, penSize);
					graohToDraw = new GraphObj(aShape, color);
					
					shapes.add(graohToDraw);
					
	
					channel.sendDraw(graohToDraw);

				} else if ("Erase".equals(shape)) {

					int penSize = UserPanel.getSlideValue();

					aShape = drawPen(x, y, penSize, penSize);
					graohToDraw = new GraphObj(aShape, new Color(238, 238, 238));
					shapes.add(graohToDraw);
					channel.sendDraw(graohToDraw);
				}
				
		

				repaint();
			}

			public void mouseReleased(MouseEvent e) {

				if (!"Pen".equals(shape) && !"Erase".equals(shape)) {

					Shape aShape = null;
					GraphObj shapeToDraw;
					
					if ("Line".equals(shape)) {

						aShape = drawLine(drawStart.x, drawStart.y, e.getX(), e.getY());

					} else if ("Rect".equals(shape)) {

						aShape = drawRect(drawStart.x, drawStart.y, e.getX(), e.getY());

					} else if ("Oval".equals(shape)) {
						aShape = drawOval(drawStart.x, drawStart.y, e.getX(), e.getY());
					}
					else if ("Circle".equals(shape)) {
						
						aShape = drawCircle(drawStart.x, drawStart.y, e.getX(), e.getY());
						
					}
					else if ("Text".equals(shape)) {
							
							shapeToDraw = new GraphObj(UserPanel.getText(), e.getX(), e.getY());
							shapes.add(shapeToDraw);
							channel.sendDraw(shapeToDraw);
							repaint();
							return;
							
						
					}
					shapeToDraw = new GraphObj(aShape, color);
					
					shapes.add(shapeToDraw);
					
					channel.sendDraw(shapeToDraw);
					drawStart = null;
					drawEnd = null;

					// repaint the drawing area
					repaint();
				}
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {

			public void mouseDragged(MouseEvent e) {

				int x = e.getX();
				int y = e.getY();
				
				Shape aShape = null;			
				GraphObj shapeToDraw = null;
				
				int penSize = UserPanel.getSlideValue();
				if ("Pen".equals(shape)) {

					aShape = drawPen(x, y, penSize, penSize);
					
					shapeToDraw = new GraphObj(aShape, color);
					shapes.add(shapeToDraw);
					channel.sendDraw(shapeToDraw);

				} else if ("Erase".equals(shape)) {

					
					aShape = drawPen(x, y, penSize, penSize);
					
					shapeToDraw = new GraphObj(aShape, new Color(238, 238, 238));
					
					shapes.add(shapeToDraw);
					channel.sendDraw(shapeToDraw);

				}

				drawEnd = new Point(e.getX(), e.getY());
				
		
				repaint();
			}
		});

	}

	public synchronized void paint(Graphics g) {
		// Class used to define the shapes to be drawn

		Graphics2D graphSettings = (Graphics2D) g;
		// Antialiasing cleans up the jagged lines and defines rendering rules

		graphSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Defines the line width of the stroke
		graphSettings.setStroke(new BasicStroke(2));

		// Iterators created to cycle through strokes and fills

		// Eliminates transparent setting below

		graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

		for (GraphObj s : shapes)
		{
			// Grabs the next stroke from the color arraylist
			// graphSettings.setPaint(strokeCounter.next());
			if (s.getShape() != null)
			{
				graphSettings.draw(s.getShape());

				// Grabs the next fill from the color arraylist

				graphSettings.setPaint(s.getFillColor());

				// System.out.println(s.getFillColor());

				graphSettings.fill(s.getShape());

			}
			else if (s.getText() != null)
			{
				System.out.println(s.getText());
				int x1 = s.getX1();
				int y1 = s.getY1();
				graphSettings.setPaint(Color.BLACK);
				graphSettings.drawString(s.getText(), x1, y1);
			}
		}

		iniGuideShape(graphSettings);
		// Guide shape used for drawing

	}

	public void reloadCanvasContent(List<GraphObj> shapes)
	{
		//this.shapes.clear();
		this.shapes = shapes;
		
		// this.canvasName = canvasName;
		repaint();
	}

	public void receiveDrawing(GraphObj g) {
		
		shapes.add(g);
		repaint();

	}

	private void iniGuideShape(Graphics2D graphSettings) {

		if (drawStart != null && drawEnd != null && !"Pen".equals(shape) && !"Erase".equals(shape)
				&& !"Text".equals(shape)) {
			// Makes the guide shape transparent

			graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f));

			// Make guide shape gray for professional look
			graphSettings.setPaint(Color.LIGHT_GRAY);
			// Create a new rectangle using x & y coordinates
			
			Shape aShape;
			
			if ("Line".equals(shape)) {

				 aShape = drawLine(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
			//graphSettings.draw(aShape);
			} else if ("Oval".equals(shape)) {

				 aShape = drawOval(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
			//	graphSettings.draw(aShape);
			}
			else if("Circle".equals(shape)) {
				
				aShape = drawCircle(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
			}
			else {

				 aShape = drawRect(drawStart.x, drawStart.y, drawEnd.x, drawEnd.y);
			
			}

			graphSettings.draw(aShape);
		}

	}

	public void setCanvasName(String name) {
		this.canvasName = name;
	}

	private Ellipse2D.Float drawOval(int x1, int y1, int x2, int y2) {

		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		int width = Math.abs(x1 - x2);
		int height = Math.abs(y1 - y2);

		return new Ellipse2D.Float(x, y, width, height);

	}
	
	private Ellipse2D.Float drawCircle(int x1, int y1, int x2, int y2) {
		
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		int width = Math.abs(x1 - x2);
		int height = Math.abs(y1 - y2);
		
		int radius = (int) Math.sqrt((Math.pow(width, 2)+(Math.pow(height, 2))));
		
		return new Ellipse2D.Float(x, y, radius, radius);
	}

	private Rectangle2D.Float drawRect(int x1, int y1, int x2, int y2) {

		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		int width = Math.abs(x1 - x2);
		int height = Math.abs(y1 - y2);

		return new Rectangle2D.Float(x, y, width, height);

	}

	private Line2D.Float drawLine(int x1, int y1, int x2, int y2) {

		return new Line2D.Float(x1, y1, x2, y2);

	}

	private Ellipse2D.Float drawPen(int x1, int y1, int penWidth, int penHeight) {

		return new Ellipse2D.Float(x1, y1, penWidth, penHeight);
	}

	// Get the top left hand corner for the shape
	// Math.min returns the points closest to 0

	// The other shapes will work similarly
	// More on this in the next tutorial
	public void setColor(Color color) {
		
		this.color = color;
		
	}
	public void setShape(String shape) {
		
		this.shape = shape;
		
	}
	public void setChannel(Communication service)
	{
		this.channel = service;
	}
	
}
