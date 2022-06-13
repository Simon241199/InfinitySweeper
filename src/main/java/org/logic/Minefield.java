package org.logic;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Minefield {
	public int getBombsUncovered() {
		return bombsUncovered;
	}

	int bombsUncovered = 0;

	public void flipFlag(int flagX, int flagY) {
		Position flag = new Position(flagX, flagY);
		if (flags.contains(flag)) {
			flags.remove(flag);
			return;
		}
		if (!isUncovered(flagX, flagY)) {
			flags.add(flag);
		}
	}

	double uncoveredRatio(int fromRow, int toRow) {
		int uncovered = 0;
		int total = 1;
		for (int y = fromRow; y < toRow; y++) {
			for (int x = 0; x < width; x++) {
				total++;
				if(isUncovered(x, y))
					uncovered++;
			}
		}
		return uncovered/(double)total;
	}

	public boolean isFlag(int x, int y) {
		Position flag = new Position(x, y);
		return flags.contains(flag);
	}

	public int getCurrentHeight() {
		return bombs.height();
	}

	record Position(int x, int y) {
	}

	Set<Position> flags = new HashSet<>();
	Bitfield bombs;
	Bitfield uncovered;
	final long seed;
	final Random random;
	public final int width;
	float mineProbability;

	public void setMineProbability(float mineProbability) {
		this.mineProbability = mineProbability;
	}

	public Minefield(int width, float mineProbability) {
		if (width > 64 || width <= 0) {
			throw new IllegalArgumentException("Minefield konstruktor: Es muss 0 < width <= 64 gelten");
		}
		this.random = new Random();
		this.seed = random.nextLong();
		random.setSeed(seed);
		System.out.println("Seed: " + seed);

		this.width = width;
		this.mineProbability = mineProbability;

		bombs = new Bitfield(width);
		uncovered = new Bitfield(width);
		for (int x = 0; x < width; x++) {
			bombs.set(x,0,false);
			bombs.set(x,1,false);
		}
		uncover(0,0);
	}

	private void appendRow() {
		int y = bombs.height();
		for (int x = 0; x < width; x++) {
			bombs.set(x, y, random.nextDouble() < mineProbability);
		}
	}

	public boolean isBomb(int x, int y) {
		if (isInvalidPosition(x, y)) {
			return false;
		}
		while (y >= bombs.height()) {
			appendRow();
		}
		return bombs.isBitSet(x, y);
	}

	private void set(int x, int y, boolean isBombPresent) {
		if (isInvalidPosition(x, y)) {
			throw new IllegalArgumentException("Invalid Position");
		}
		bombs.set(x, y, isBombPresent);
	}

	public int getBombCount(int x, int y) {
		if (isBomb(x, y))
			return -1;

		int count = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (isBomb(dx + x, dy + y)) {
					count++;
				}
			}
		}
		return count;
	}

	public boolean isUncovered(int x, int y) {
		if (isInvalidPosition(x, y)) {
			return false;
		}
		return uncovered.isBitSet(x, y);
	}

	public boolean isInvalidPosition(int x, int y) {
		return x < 0 || x >= width || y < 0;
	}

	void print() {
		int height = bombs.height();
		for (int y = 0; y < height; y++) {
			System.out.print(y + "\t");
			for (int x = 0; x < width; x++) {
				System.out.print(getCharacter(x, y));
			}
			System.out.println();
		}
	}

	public void uncover(int x, int y) {
		if (isFlag(x, y)) {
			return;
		}
		if (isInvalidPosition(x, y)) {
			return;
		}
		if (isUncovered(x, y)) {
			return;
		}
		if(isBomb(x,y)){
			bombsUncovered++;
		}
		uncovered.set(x, y, true);

		if (getBombCount(x, y) != 0) {
			return;
		}

		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				uncover(x + dx, y + dy);
			}
		}
	}

	public char getCharacter(int x, int y) {
		if (isFlag(x, y)) {
			return '\u26F3';
		}
		if (!isUncovered(x, y))
			return '#';
		if (isBomb(x, y))
			return '\u26ED';
		int bombCount = getBombCount(x, y);
		if (bombCount == 0) {
			return ' ';
		}
		return Character.forDigit(bombCount, 10);
	}

}
