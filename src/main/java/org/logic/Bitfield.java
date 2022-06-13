package org.logic;

import java.util.LinkedList;

public class Bitfield {
	private final LinkedList<Long> rows;
	public final int width;

	public Bitfield(int width) {
		if (width > 64 || width <= 0) {
			throw new IllegalArgumentException("Minefield konstruktor: Es muss 0 < width <= 64 gelten");
		}

		this.width = width;

		this.rows = new LinkedList<>();
	}

	public boolean isBitSet(int x, int y) {
		if (isInvalidPosition(x, y)) {
			System.err.println("Position invalid");
			return false;
		}
		if (y >= rows.size()) {
			return false;
		}
		return (rows.get(y) & (1L << x)) != 0;
	}

	public void set(int x, int y, boolean state) {
		if (isInvalidPosition(x, y)) {
			System.err.println("Position invalid");
			return;
		}
		while (y >= rows.size()) {
			rows.addLast(0L);
		}
		long bitMask = ~(1L << x);
		long row = rows.get(y);

		long lState = (state ? 1L : 0L);

		long newRow = (row & bitMask) | (lState << x);

		rows.set(y, newRow);
	}

	public boolean isInvalidPosition(int x, int y) {
		return x < 0 || x >= width || y < 0;
	}

	public int height(){
		return rows.size();
	}

	public void print() {
		for (int y = 0; y < rows.size(); y++) {

			for (int x = 0; x < width; x++) {
				System.out.print((isBitSet(x, y) ? 1 : 0));
			}
			System.out.println();
		}
	}
}
