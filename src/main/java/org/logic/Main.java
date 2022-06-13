package org.logic;


import org.ui.GuiBoard;
import org.ui.GuiPlayer;

import java.util.Random;

public class Main {

	public static double interpolate(double from, double to, double t) {
		return from * (1 - t) + to * t;
	}

	public static void main(String[] args) throws InterruptedException {

		int width = 32;
		int targetDisplayHeight = 32;

		double speedMultiplier = 1.25;

		double maxPace = speedMultiplier * 0.3 / width;
		double minPace = speedMultiplier * 0.05 / width;

		Minefield minefield = new Minefield(width, 0.15f);

		GuiBoard gui = new GuiBoard();
		GuiPlayer player = new GuiPlayer();
		gui.init(player, minefield.width,targetDisplayHeight);

		double last = System.currentTimeMillis() / 1000.0;

		double currentRow = 10;

		while (true) {
			double deltaTime = System.currentTimeMillis() / 1000.0 - last;
			currentRow += interpolate(minPace, maxPace, minefield.uncoveredRatio((int) currentRow - gui.getCurrentHeight(), (int) currentRow));
			player.uncover(minefield);

			gui.display(minefield, currentRow);
			last = System.currentTimeMillis() / 1000.0;
			if (minefield.getBombsUncovered() > 1) {
				break;
			}
			Thread.sleep((long) (100 - deltaTime));
		}
		last = System.currentTimeMillis() / 1000.0;
		while (System.currentTimeMillis() / 1000.0 - last < 5) {
			gui.display(minefield, currentRow);
			Thread.sleep(100);
		}
	}
}