package boarduser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManagerPanel extends JPanel {

	private JButton saveButton;
	private JButton saveAsButton;
	private JButton newCanvasButton;
	private JButton listOpen;
	private JButton listDelete;
	private JScrollPane canvasListPane;
	private JList<String> canvasList;

	private ClientMns boss;
	private UserPanel userPanel;
	private DefaultListModel model;

	public ManagerPanel(ClientMns boss, UserPanel userPanel) {

		this.boss = boss;

		this.userPanel = userPanel;
		canvasListPane = new JScrollPane();
		newCanvasButton = new JButton("New Canvas");
		saveButton = new JButton("Save");
		saveAsButton = new JButton("Save As Image");
		listOpen = new JButton("Open Canvas");
		listDelete = new JButton("Delete Canvas");
		listOpen.setEnabled(false);
		listDelete.setEnabled(false);
		canvasList = new JList<String>();

		initComponent();

	}

	public void initComponent() {
		updateCanvasList();

		/*
		 * model = new AbstractListModelDefaultListModel<String>() {
		 * 
		 * 
		 * String[] strings = boss.getCanvasList();//{ "Item 1", "Item 2", "Item 3",
		 * "Item 4", "Item 5" };//
		 * 
		 * public int getSize() { return strings.length; }
		 * 
		 * public String getElementAt(int i) { return strings[i]; } };
		 * canvasList.setModel(model);
		 * 
		 * /*canvasList.setModel(new AbstractListModel<String>() {
		 * 
		 * 
		 * String[] strings = boss.getCanvasList();//{ "Item 1", "Item 2", "Item 3",
		 * "Item 4", "Item 5" };//
		 * 
		 * public int getSize() { return strings.length; }
		 * 
		 * public String getElementAt(int i) { return strings[i]; } });
		 * canvasListPane.setViewportView(canvasList);
		 * 
		 * canvasList.addMouseListener(new MouseAdapter() { public void
		 * mouseClicked(MouseEvent event) { canvasMouseClicked(event); }
		 * 
		 * });
		 */
		saveAsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				saveAsButtonClicked(e);

			};

		});

		newCanvasButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				newCanvasButtonClicked(e);
			}

		});
		listDelete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				deleteCanvasButtonClicked(e);
			}

		});
		listOpen.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				openCanvasButtonClicked(e);
			}

		});
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				saveButtonActionClicked(e);
			}

		});

		initialLayout();

	}

	private void deleteCanvasButtonClicked(ActionEvent e) {

		String targetCanvas = canvasList.getSelectedValue();

		String currentCanvas = userPanel.getCurrentCanvas().getCanvasName();

		if (targetCanvas.equalsIgnoreCase(currentCanvas)) {
			JOptionPane.showMessageDialog(null, "Currently in use, cannot delete!");
		}

		else {

			if (!boss.deleteCanvas(targetCanvas)) {
				JOptionPane.showMessageDialog(null, "Canvas does not exist");
			} else {
				try {
					boss.flushCanvasFile();
					JOptionPane.showMessageDialog(null, "File deleted");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Cannot update canvas file");
				}
			}
			updateCanvasList();
		}

	}

	private void updateCanvasList() {
		model = new /* AbstractListModel */DefaultListModel<String>() {

			String[] strings = boss.getCanvasList();// { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };//

			public int getSize() {
				return strings.length;
			}

			public String getElementAt(int i) {
				return strings[i];
			}
		};
		canvasList.setModel(model);
		canvasListPane.setViewportView(canvasList);

		canvasList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				canvasMouseClicked(event);
			}

		});

	}

	private void openCanvasButtonClicked(ActionEvent e) {

		String targetCanvas = canvasList.getSelectedValue();
		try {

			boss.openCanvas(targetCanvas);
			// userPanel.updateActiveBoard(boss.openCanvas(targetCanvas));
			JOptionPane.showMessageDialog(null, "Canvas is open!");

		} catch (NotExistException e1) {

			JOptionPane.showMessageDialog(null, "Canvas does not exist!");
		}

	}

	private void saveAsButtonClicked(ActionEvent e) {

		CanvasBoard currentBoard = userPanel.getCurrentCanvas();
		BufferedImage image = new BufferedImage(currentBoard.getWidth(), currentBoard.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		currentBoard.paint(g);
		g.dispose();

		JFileChooser fileChooser = new JFileChooser();
		File directory = new File(".");
		fileChooser.setCurrentDirectory(directory);
		FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG file (*.png)", "png");
		fileChooser.addChoosableFileFilter(pngFilter);
		fileChooser.setFileFilter(pngFilter);
		int status = fileChooser.showSaveDialog(ManagerPanel.this);

		if (status == JFileChooser.APPROVE_OPTION) {
			try {
				//fileChooser.getSelectedFile().getAbsolutePath()+fileChooser.getSelectedFile().getName()+".png"
				String filename =  fileChooser.getSelectedFile().getAbsolutePath()
								  +fileChooser.getSelectedFile().getName()
								  +".png";
				ImageIO.write(image, "png", new File(filename));
				JOptionPane.showMessageDialog(null, "Image saved to " + fileChooser.getSelectedFile().getName());
			} catch (Exception ex) {
				System.out.println("Can`t save to file...");
			}
		}
	}

	private void newCanvasButtonClicked(ActionEvent e) {
		String canvasName = JOptionPane.showInputDialog("Please input drawing name");

		if (!boss.checkCanvas(canvasName)) {

			boss.createNewCanvas(canvasName);

			// userPanel.updateActiveBoard(newBoard);
		} else {
			JOptionPane.showMessageDialog(null, "Canvas already exist! Pleaes change name");
		}

	}

	private void saveButtonActionClicked(ActionEvent e) {

		List<GraphObj> content = userPanel.getCurrentCanvas().shapes;

		boss.saveCanvas(content);

		try {
			boss.flushCanvasFile();
			JOptionPane.showMessageDialog(null, "Paint saved!");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Not able to save! Try again later!");
		}
		// String[] data = boss.getCanvasList();
		// canvasList = new JList<String>(data);

		// model.addElement(userPanel.getCurrentCanvas().getCanvasName());
		// canvasList.setModel(model);
		// canvasListPane.setViewportView(canvasList);
		updateCanvasList();
		System.out.println("Refreshing");

	}

	private void canvasMouseClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		listOpen.setEnabled(true);
		listDelete.setEnabled(true);
	}

	public void initialLayout() {

		GroupLayout managerPanelLayout = new GroupLayout(this);
		this.setLayout(managerPanelLayout);
		managerPanelLayout.setHorizontalGroup(managerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(managerPanelLayout.createSequentialGroup().addGroup(managerPanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(managerPanelLayout.createSequentialGroup()
								.addComponent(newCanvasButton, GroupLayout.PREFERRED_SIZE, 108,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(saveButton)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(saveAsButton)
								.addGap(0, 393, Short.MAX_VALUE))
						.addGroup(managerPanelLayout.createSequentialGroup().addContainerGap()
								.addComponent(canvasListPane).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(managerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
										.addComponent(listDelete, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(listOpen, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))))
						.addContainerGap()));
		managerPanelLayout.setVerticalGroup(managerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(managerPanelLayout.createSequentialGroup()
						.addGroup(managerPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(newCanvasButton).addComponent(saveButton).addComponent(saveAsButton))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(managerPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(canvasListPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addGroup(managerPanelLayout.createSequentialGroup().addComponent(listOpen)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(listDelete)))
						.addGap(0, 579, Short.MAX_VALUE)));

	}
}
