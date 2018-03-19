package com.nickimpact.impactor.gui.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Map;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public class Layout {

	@Getter private final ImmutableMap<Integer, Icon> elements;

	private Layout(Map<Integer, Icon> elements) {
		this.elements = ImmutableMap.copyOf(elements);
	}

	public Icon getIcon(Integer index) {
		return elements.getOrDefault(index, Icon.EMPTY);
	}

	public static Builder builder(int rows) {
		return new Builder(rows);
	}

	public static class Builder {
		private Map<Integer, Icon> elements = Maps.newHashMap();
		private int rows;
		private int size;

		public Builder(int rows) {
			this.rows = rows;
			this.size = rows * 9;
		}

		/**
		 * Sets an icon at the specified location
		 *
		 * @param icon The icon to display
		 * @param index The position to place the icon at
		 * @return An updated version of this builder
		 */
		public Builder slot(Icon icon, int index) throws IndexOutOfBoundsException {
			Preconditions.checkArgument(rows > 0 && rows <= 6);
			if(index >= size) {
				throw new IndexOutOfBoundsException(String.format("Size = %d, Index = %d", size, index));
			}
			elements.put(index, icon);
			return this;
		}

		/**
		 * Registers the given icon at the given indices
		 *
		 * @param icon The icon to display at the given location
		 * @param indices The locations the icon will be set in
		 * @return An updated version of this builder
		 */
		public Builder slots(Icon icon, int... indices) {
			Preconditions.checkArgument(rows > 0 && rows <= 6);
			for(int i : indices) {
				slot(icon, i);
			}

			return this;
		}

		/**
		 * Decorates the layout with a border which encompasses the entire interface, using the
		 * default border icon display.
		 *
		 * @return An updated version of this builder
		 */
		public Builder border() {
			Preconditions.checkArgument(rows > 0 && rows < 6);
			this.border(Icon.BORDER);
			return this;
		}

		/**
		 * Decorates the layout with a border which encompasses the entire interface.
		 *
		 * @param icon The icon to draw the border with
		 * @return An updated version of this builder
		 */
		public Builder border(Icon icon) {
			Preconditions.checkArgument(rows > 0 && rows <= 6);
			for(int i = 0; i < 9; i++) {
				slot(icon, i);
				slot(icon, size - i - 1);
			}

			for(int i = 1; i < rows - 1; i++) {
				slot(icon, i * 9);
				slot(icon, (i + 1) * 9 - 1);
			}

			return this;
		}

		/**
		 * Fills an entire row based off the 0-based index location of a position in the layout.
		 * E.g. <code>...row(icon, 0)</code> would decorate the entire top row of a layout with
		 * whatever icon is given.
		 *
		 * @param icon The icon to place across the column
		 * @param row The row to decorate
		 * @return An updated version of this builder
		 */
		public Builder row(Icon icon, int row) {
			Preconditions.checkArgument(rows > 0 && rows <= 6);
			for(int i = row * 9; i < (row + 1) * 9; i++) {
				slot(icon, i);
			}

			return this;
		}

		/**
		 * Fills an entire column based off the 0-based index location of a position in the layout.
		 * E.g. <code>...column(icon, 0)</code> would decorate the entire left side of a layout with
		 * whatever icon is given.
		 *
		 * @param icon The icon to place across the column
		 * @param column The column to decorate
		 * @return An updated version of this builder
		 */
		public Builder column(Icon icon, int column) {
			Preconditions.checkArgument(rows > 0 && rows <= 6);
			for(int i = column; i < rows; i += 9) {
				slot(icon, i);
			}

			return this;
		}

		/**
		 * Places the icon at the determined middle of an interface
		 *
		 * @param icon The icon to place at the center
		 * @return An updated version of this builder
		 */
		public Builder center(Icon icon) {
			Preconditions.checkArgument(rows > 0 && rows <= 6);
			if(rows % 2 == 1) {
				slot(icon, rows * 9 / 2);
			} else {
				int base = rows * 9 / 2;
				slot(icon, base - 5);
				slot(icon, base + 4);
			}

			return this;
		}

		public Layout build() {
			Preconditions.checkArgument(rows > 0 && rows <= 6);
			return new Layout(this.elements);
		}
	}
}
