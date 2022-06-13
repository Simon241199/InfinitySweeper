package org.ui;


import org.logic.Minefield;

import java.util.concurrent.Semaphore;

public class GuiPlayer {
	int uncoverX = -1;
	int uncoverY = -1;
	int flagX = -1;
	int flagY = -1;
	Semaphore sem = new Semaphore(0);

	public void uncover(Minefield minefield) {
		int uncoverX = -1;
		int uncoverY = -1;
		int flagX = -1;
		int flagY = -1;
		if (sem.availablePermits() == 0) {
			return;
		}
		try {
			sem.acquire();
			uncoverX = this.uncoverX;
			uncoverY = this.uncoverY;
			flagX = this.flagX;
			flagY = this.flagY;
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		if (!minefield.isInvalidPosition(uncoverX, uncoverY)) {
			minefield.uncover(uncoverX, uncoverY);
		}
		if (!minefield.isInvalidPosition(flagX, flagY)) {
			minefield.flipFlag(flagX, flagY);
		}
		this.uncoverX = -1;
		this.uncoverY = -1;
		this.flagX = -1;
		this.flagY = -1;
	}

	void setLastLeftClickLocation(int x, int y) {
		this.uncoverX = x;
		this.uncoverY = y;
		sem.release();
	}

	void setLastRightClickLocation(int x, int y) {
		this.flagX = x;
		this.flagY = y;
		sem.release();
	}
}
