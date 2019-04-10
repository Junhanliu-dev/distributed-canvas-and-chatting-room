package boarduser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;

public class UserPanel extends JPanel {

	private CanvasBoard canvasBoard;
	private JLabel editorLabel = new JLabel("Editor:");
	private static JLabel currentEditor = new JLabel("");
	private JButton colorButton;
	private JSlider sizeSlider;
	private JButton drawLineButton;
	private JButton drawOvalButton;
	private JButton addTextButton;
	private JButton circleButton;
	private JButton drawRectButton;
	private JButton freePenButton;
	private JButton eraseButton;
	private JLabel sizeLabel;
	private static int sizeSlideVal = 5;
	private static String text;
	private final Font font = new Font("Times New Roman", 0, 18);
	GroupLayout canvasPanelLayout;

	public UserPanel(String canvasName) {

		canvasPanelLayout = new GroupLayout(this);
		canvasBoard = new CanvasBoard(canvasName, new CopyOnWriteArrayList<GraphObj>());
		canvasBoard.setColor(Color.BLACK);
		colorButton = new JButton("Color");
		sizeSlider = new JSlider(5, 25, 5);
		sizeLabel = new JLabel("Size:");
		addTextButton = new JButton("Text");
		circleButton = makeButton("Circle");
		drawLineButton = makeButton("Line");
		drawOvalButton = makeButton("Oval");
		drawRectButton = makeButton("Rect");
		freePenButton = makeButton("Pen");
		eraseButton = makeButton("Erase");

		initComponent();
		initLayout();
	}

	public CanvasBoard getCurrentCanvas() {
		return canvasBoard;
	}

	public JButton makeButton(String action) {

		JButton theBut = new JButton(action);

		// Make the proper actionPerformed method execute when the
		// specific button is pressed

		theBut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				canvasBoard.setShape(action);
			}
		});

		return theBut;

	}

	public void initComponent() {

		sizeLabel.setFont(font);
		editorLabel.setFont(font);
		currentEditor.setFont(font);
		ListenForSlider lForSlider = new ListenForSlider();
		sizeSlider.addChangeListener(lForSlider);

		colorButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				canvasBoard.setColor(JColorChooser.showDialog(null, "Please choose a color", Color.BLACK));

			}
		});
		addTextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addTextButtonClieked(e);
			}

		});
	}

	private void addTextButtonClieked(ActionEvent e) {
		
		canvasBoard.setShape("Text");
		
		text = JOptionPane.showInputDialog(null, "Please input your text, and click where you want to show your text");
		
		while(text.equals(null)|| text.equals("")) {
			
			text = JOptionPane.showInputDialog(null, "please input something...");
			
		}
		
	}


	public static int getSlideValue() {
		return sizeSlideVal;
	}


	public static String getText() {

		return text;

	}

	public static void setEditor(String editor) {

		currentEditor.setText(editor);

	}

	private class ListenForSlider implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource() == sizeSlider) {

				sizeLabel.setText("Size: " + sizeSlider.getValue());
				sizeSlideVal = sizeSlider.getValue();

			}
		}

	}

	public void initLayout() {

		this.setLayout(canvasPanelLayout);
		canvasPanelLayout.setHorizontalGroup(canvasPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING, canvasPanelLayout.createSequentialGroup().addGroup(
						canvasPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(canvasPanelLayout
								.createSequentialGroup().addContainerGap()
								.addGroup(canvasPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(canvasPanelLayout
												.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
												.addComponent(drawLineButton, GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(drawOvalButton, GroupLayout.Alignment.LEADING,
														GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(drawRectButton, GroupLayout.Alignment.LEADING,
														GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(freePenButton, GroupLayout.Alignment.LEADING,
														GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(eraseButton, GroupLayout.Alignment.LEADING,
														GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(circleButton,GroupLayout.Alignment.LEADING,
														GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(addTextButton, GroupLayout.PREFERRED_SIZE, 77,
														GroupLayout.PREFERRED_SIZE))
										
										.addComponent(colorButton))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(canvasBoard,
										GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(canvasPanelLayout.createSequentialGroup().addGap(154, 154, 154)
										.addComponent(sizeLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(sizeSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(editorLabel).addGap(58, 58, 58).addComponent(currentEditor)))
						.addContainerGap()));
		canvasPanelLayout.setVerticalGroup(canvasPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(canvasPanelLayout.createSequentialGroup().addContainerGap()
						.addGroup(canvasPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(sizeSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(sizeLabel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addGroup(canvasPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(currentEditor).addComponent(editorLabel)))
						.addGap(6, 6, 6)
						.addGroup(canvasPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(canvasPanelLayout.createSequentialGroup().addComponent(colorButton)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(drawLineButton)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(drawOvalButton)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(drawRectButton)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(freePenButton)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(eraseButton)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(circleButton)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(addTextButton).addGap(0, 0, Short.MAX_VALUE))
								.addComponent(canvasBoard, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
						.addContainerGap()));
	}

}
