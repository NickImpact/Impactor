package net.impactdev.impactor.api.gui;

public class InventoryDimensions {
	private final int columns;
	private final int rows;

	public InventoryDimensions(int columns, int rows) {
		this.columns = columns;
		this.rows = rows;
	}

	public int getColumns() {
		return this.columns;
	}

	public int getRows() {
		return this.rows;
	}
}
