package com.nickimpact.impactor.gui.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.item.inventory.property.InventoryDimension;

import java.util.List;
import java.util.Map;

/**
 * Describes the contents of a UI with their exact positioning. For instance, a Layout would typically contain
 * the icons that describe quick and easy actions, or no actions at all. For example, a Layout can be described
 * with a border,
 *
 * @author NickImpact (Nick DeGruccio)
 */
@RequiredArgsConstructor
public class Layout {

	@Getter private final ImmutableMap<Integer, Icon> elements;
	@Getter private final InventoryDimension dimension;

	public Icon getIcon(Integer index) {
		return elements.getOrDefault(index, Icon.EMPTY);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private static final InventoryDimension BIG_CHEST = InventoryDimension.of(9, 6);

		private Map<Integer, Icon> elements = Maps.newHashMap();
		private InventoryDimension dimension = BIG_CHEST;
		private int rows = 6;
		private int columns = 9;
		private int capacity = 54;

		public Builder from(Layout layout) {
			elements.clear();
			dimension(layout.getDimension());
			for(int slot : layout.getElements().keySet()) {
				slot(layout.getElements().get(slot), slot);
			}
			return this;
		}

		public Builder dimension(InventoryDimension dimension) {
			this.dimension = dimension;
			this.rows = dimension.getRows();
			this.columns = dimension.getColumns();
			this.capacity = this.rows * this.columns;
			return this;
		}

		public Builder dimension(int columns, int rows) {
			return dimension(InventoryDimension.of(columns, rows));
		}

		/**
		 * Sets an icon at the specified location
		 *
		 * @param icon The icon to display
		 * @param index The position to place the icon at
		 * @return An updated version of this builder
		 */
		public Builder slot(Icon icon, int index) throws IndexOutOfBoundsException {
			Preconditions.checkElementIndex(index, capacity);
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
			for(int i : indices) {
				slot(icon, i);
			}

			return this;
		}

		public Builder replace(Icon prior, Icon replacement) {
			for (Map.Entry<Integer, Icon> entry : elements.entrySet()) {
				if (entry.getValue() == prior) {
					entry.setValue(replacement);
				}
			}
			return this;
		}

		public Builder page(List<Icon> icons) {
			return this.page(icons, this.dimension);
		}

		public Builder page(List<Icon> icons, InventoryDimension dimension) {
			return this.page(icons, dimension, 0, 0);
		}

		/**
		 * Given a set of icons, conform these items to a sub-dimension of the original inventory. The dimension can
		 * be offset such that it doesn't always point to the 0 slot index, but the result of the offset must keep
		 * the dimension within the inventory. Any breach of such will cause this method to throw an exception.
		 *
		 * @param icons The set of icons to place in the inventory
		 * @param dimension The sub-dimension of the original inventory
		 * @param rOffset The row offset for the sub-dimension
		 * @param cOffset The column offset for the sub-dimension
		 * @return An updated version of this builder
		 * @throws IndexOutOfBoundsException In the event the sub-dimension is bigger than the main dimension, or
		 * either offsets point to a location which causes the sub-dimension to breach normal bounds. Lastly, if offsets
		 * breach even the sub-dimensions size.
		 */
		public Builder page(List<Icon> icons, InventoryDimension dimension, int rOffset, int cOffset) throws IndexOutOfBoundsException {
			if(dimension.getRows() > this.rows || dimension.getColumns() > this.columns || dimension.getRows() * dimension.getColumns() > this.capacity) {
				throw new IndexOutOfBoundsException("Sub-dimension must be smaller than original dimension");
			}

			if(dimension.getRows() + rOffset > this.rows || dimension.getColumns() + cOffset > this.columns) {
				throw new IndexOutOfBoundsException("Sub-dimension breaches main dimension limits");
			}

			if(rOffset > dimension.getRows() || cOffset > dimension.getColumns()) {
				throw new IndexOutOfBoundsException("Offsets must be within dimension bounds");
			}

			int index = cOffset + this.dimension.getColumns() * rOffset;
			int r = 0;
			int cap = index + dimension.getColumns() - 1 + 9 * (dimension.getRows() - 1);
			for(Icon icon : icons) {
				if(index > cap) {
					break;
				}

				if(r == dimension.getColumns()) {
					index += this.dimension.getColumns() - dimension.getColumns();
					r = 0;
				}

				slot(icon, index);

				index++;
				r++;
			}
			return this;
		}

		/**
		 * Fills any slots which do not currently have an icon registration
		 *
		 * @param icon The icon to fill the remaining slots with
		 * @return An updated version of this builder
		 */
		public Builder fill(Icon icon) {
			for(int i = 0; i < capacity; i++) {
				if(!elements.containsKey(i)) {
					slot(icon, i);
				}
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
			for(int i = 0; i < 9; i++) {
				slot(icon, i);
				slot(icon, capacity - i - 1);
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
		 * @param row The target row to decorate
		 * @return An updated version of this builder
		 */
		public Builder row(Icon icon, int row) {
			for(int i = row * 9; i < (row + 1) * 9; i++) {
				slot(icon, i);
			}

			return this;
		}

		/**
		 * Fills a set of rows based off the 0-based index location of a position in the layout.
		 * E.G. <code>...row(icon, 0, 5)</code> would decorate the top and bottom rows of a 9x6
		 * layout with the specified icon.
		 *
		 * @param icon The icon to decorate the rows with.
		 * @param rows The rows to decorate
		 * @return An updated version of this builder
		 */
		public Builder rows(Icon icon, int... rows) {
			for(int row : rows) {
				this.row(icon, row);
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
			for(int i = column; i < capacity; i += 9) {
				slot(icon, i);
			}

			return this;
		}

		/**
		 * Fills a set of columns based off the 0-based index location of a position in the layout.
		 * E.G. <code>...row(icon, 0, 8)</code> would decorate the left and right columns of a layout
		 * of 9 columns with the specified icon.
		 *
		 * @param icon The icon to decorate the rows with.
		 * @param cols The rows to decorate
		 * @return An updated version of this builder
		 */
		public Builder columns(Icon icon, int... cols) {
			for(int col : cols) {
				this.column(icon, col);
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
			if(rows % 2 == 1) {
				slot(icon, rows * 9 / 2);
			} else {
				int base = rows * 9 / 2;
				slot(icon, base - 5);
				slot(icon, base + 4);
			}

			return this;
		}

		private void squareCheck(int center) {
			// Check if the square is in a valid column
			if(center % 9 == 0 || center % 9 == 8) {
				throw new IllegalArgumentException("Center column must be between 1 and 7");
			}

			// Check if the square is in a valid row
			if(center / 9 == 0 || center / 9 == (rows - 1)) {
				throw new IllegalArgumentException("Center row must be between 1 and " + (rows - 1));
			}
		}

		/**
		 * Builds a 3x3 square of the {@link Icon} based around the center index.
		 *
		 * @param icon The icon to draw the square of
		 * @param center The center position of the square
		 * @return An updated version of this builder
		 * @throws IllegalArgumentException In the event the center index causes the square to go out-of-bounds
		 */
		public Builder square(Icon icon, int center) {
			this.squareCheck(center);

			int cc = center % 9;
			for(int row = center / 9 - 1; row < center / 9 + 2; row++)
				for(int i = -1; i <= 1; i++) {
					slot(icon, (cc + i) + row * 9);
				}

			return this;
		}

		/**
		 * Builds a 3x3 square of the {@link Icon} based around the center index, with a hole in its center.
		 *
		 * @param icon The icon to draw the square of
		 * @param center The center position of the square
		 * @return An updated version of this builder
		 * @throws IllegalArgumentException In the event the center index causes the square to go out-of-bounds
		 */
		public Builder hollowSquare(Icon icon, int center) {
			this.squareCheck(center);

			int cc = center % 9;
			for(int row = center / 9 - 1; row < center / 9 + 2; row++)
				for(int i = -1; i <= 1; i++) {
					if(row == center / 9 && i == 0) {
						continue;
					}
					slot(icon, (cc + i) + row * 9);
				}

			return this;
		}

		public Layout build() {
			Preconditions.checkArgument(rows > 0);
			return new Layout(ImmutableMap.copyOf(this.elements), this.dimension);
		}
	}
}
