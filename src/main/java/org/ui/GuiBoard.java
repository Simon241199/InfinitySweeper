package org.ui;

import org.logic.Minefield;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedList;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

public class GuiBoard implements MouseListener {
	JFrame frame;
	JPanel panel;
	Font font = new Font("SansSerif", Font.BOLD, 20);
	LinkedList<ArrayList<JLabel>> field = new LinkedList<>();
	boolean initialized = false;

	int width;
	int targetHeight;
	int yOffset = 0;

	GuiPlayer player;

	public int getCurrentHeight(){
		return field.size();
	}

	public void init(GuiPlayer player, int width, int targetHeight) {
		initialized = true;
		this.width = width;
		this.player = player;
		this.targetHeight = targetHeight;
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 500, 500);
		panel = new JPanel();

		panel.setLayout(null);

		frame.add(panel);
		frame.setVisible(true);
	}

	public void display(Minefield minefield, double lastRow) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		if (!initialized)
			throw new UnsupportedOperationException("Call init first!");

		for (int y = yOffset; y < lastRow; y++) {
			if (y >= field.size() + yOffset) {
				addRow();
			}
			for (int x = 0; x < width; x++) {
				updateLabel(minefield, y, x);
			}
		}
		while (field.size() > targetHeight) {
			ArrayList<JLabel> row = field.removeLast();
			row.forEach(panel::remove);
			yOffset++;
		}

		updatePositions(ceil(lastRow) - lastRow);

		panel.setVisible(true);
		frame.setVisible(true);
	}

	private void updateLabel(Minefield minefield, int y, int x) {
		JLabel label = field.get(field.size() - 1 - y + yOffset).get(x);
		char c = minefield.getCharacter(x, y);
		if (minefield.isUncovered(x, y)) {
			label.setText("" + c);
			if ((x + y) % 2 == 0) {
				label.setBackground(new Color(192, 204, 192));
			} else {
				label.setBackground(new Color(171, 182, 168));
			}
			if (minefield.isBomb(x, y)) {
				label.setBackground(new Color(255, 0, 0));
				label.setForeground(new Color(0, 0, 0));
			}
		} else {
			if (minefield.isFlag(x, y)) {
				label.setForeground(Color.red);
				label.setText("" + c);
			} else {
				label.setText("");
			}
			label.setBackground(new Color(0x333333));
		}
		switch (c) {
			case '1':
				label.setForeground(new Color(0x0000ff));
				break;
			case '2':
				label.setForeground(new Color(0x009900));
				break;
			case '3':
				label.setForeground(new Color(0xff0000));
				break;
			case '4':
				label.setForeground(new Color(0x770000));
				break;
			case '5':
				label.setForeground(new Color(0x330000));
				break;
		}
	}

	private void updatePositions(double offset) {
		int panelWidth = panel.getWidth();
		int panelHeight = panel.getHeight() + 50;
		double labelWidth = panelWidth / (double) width;
		double labelHeight = panelHeight / (double) targetHeight;
		labelHeight = min(labelHeight, labelWidth);
		labelWidth = min(labelHeight, labelWidth);
		offset = offset * labelHeight + 30;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < field.size(); y++) {
				JLabel label = field.get(y).get(x);
				label.setBounds((int) (x * labelWidth), (int) (y * labelHeight - offset), (int) labelWidth + 1, (int) labelHeight + 1);
			}
		}
	}

	private void addRow() {
		ArrayList<JLabel> row = new ArrayList<>();
		for (int x = 0; x < width; x++) {
			JLabel label = new JLabel();
			label.setFont(font);
			label.setOpaque(true);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			label.addMouseListener(this);
			panel.add(label);
			row.add(label);
			label.setVisible(true);
		}
		field.addFirst(row);
	}

	public void clean() {
		initialized = false;
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		for (int y = 0; y < field.size(); y++) {
			for (int x = 0; x < field.get(y).size(); x++) {
				if (field.get(y).get(x).equals(e.getComponent())) {
					if (e.getButton() == MouseEvent.BUTTON1)
						player.setLastLeftClickLocation(x, field.size() - 1 - y + yOffset);
					if (e.getButton() == MouseEvent.BUTTON3)
						player.setLastRightClickLocation(x, field.size() - 1 - y + yOffset);
					return;
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
