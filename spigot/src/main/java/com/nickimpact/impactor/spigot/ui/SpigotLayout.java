package com.nickimpact.impactor.spigot.ui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.gui.InventoryDimensions;
import com.nickimpact.impactor.api.gui.Layout;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class SpigotLayout implements Layout<SpigotIcon> {

	private final ImmutableMap<Integer, SpigotIcon> elements;
	private final InventoryDimensions dimension;


	@Override
	public ImmutableMap<Integer, SpigotIcon> getElements() {
		return this.elements;
	}

	@Override
	public Optional<SpigotIcon> getIcon(int slot) {
		return Optional.ofNullable(elements.get(slot));
	}

	@Override
	public InventoryDimensions getDimensions() {
		return this.dimension;
	}

	public static SpigotLayoutBuilder builder() {
		return new SpigotLayoutBuilder();
	}

	public static class SpigotLayoutBuilder {
		private Map<Integer, SpigotIcon> elements = Maps.newHashMap();
		private InventoryDimensions dimension = new InventoryDimensions(9, 6);

		private int rows = 6;
		private int columns = 9;
		private int capacity = 54;

		public SpigotLayoutBuilder from(Layout<SpigotIcon> layout) {
			elements.clear();
			dimension(dimension.getColumns(), dimension.getRows());
			for(int slot : layout.getElements().keySet()) {
				slot(layout.getElements().get(slot), slot);
			}

			return this;
		}

		public SpigotLayoutBuilder dimension(int columns, int rows) {
			this.dimension = new InventoryDimensions(columns, rows);
			this.rows = rows;
			this.columns = columns;
			this.capacity = rows * columns;
			return this;
		}

		public SpigotLayoutBuilder slot(SpigotIcon icon, int slot) {
			elements.put(slot, icon);
			return this;
		}

		public SpigotLayoutBuilder slots(SpigotIcon icon, int... slots) {
			for(int slot : slots) {
				slot(icon, slot);
			}
			return this;
		}

		public SpigotLayoutBuilder fill(SpigotIcon icon) {
			for(int i = 0; i < capacity; i++) {
				if(!elements.containsKey(i)) {
					slot(icon, i);
				}
			}
			return this;
		}

		public SpigotLayoutBuilder border() {
			return this.border(SpigotIcon.BORDER);
		}

		public SpigotLayoutBuilder border(SpigotIcon icon) {
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

		public SpigotLayoutBuilder row(SpigotIcon icon, int row) {
			for(int i = row * 9; i < (row + 1) * 9; i++) {
				slot(icon, i);
			}

			return this;
		}

		public SpigotLayoutBuilder rows(SpigotIcon icon, int... rows) {
			for(int row : rows) {
				this.row(icon, row);
			}

			return this;
		}

		public SpigotLayoutBuilder column(SpigotIcon icon, int col) {
			for(int i = col; i < capacity; i += 9) {
				slot(icon, i);
			}

			return this;
		}

		public SpigotLayoutBuilder columns(SpigotIcon icon, int... cols) {
			for(int col : cols) {
				this.column(icon, col);
			}

			return this;
		}

		public SpigotLayoutBuilder center(SpigotIcon icon) {
			if(rows % 2 == 1) {
				slot(icon, rows * 9 / 2);
			} else {
				int base = rows * 9 / 2;
				slot(icon, base - 5);
				slot(icon, base + 4);
			}

			return this;
		}

		public SpigotLayoutBuilder square(SpigotIcon icon, int center) {
			return square(icon, 2, center);
		}

		public SpigotLayoutBuilder square(SpigotIcon icon, int radius, int center) {
			int cc = center % 9;
			for(int row = center / 9 - (radius - 1); row <= center / 9 + (radius - 1); row++)
				for(int i = -radius + 1; i <= radius - 1; i++) {
					slot(icon, (cc + i) + row * 9);
				}

			return this;
		}

		public SpigotLayoutBuilder hollowSquare(SpigotIcon icon, int center) {
			return hollowSquare(icon, 2, center);
		}

		public SpigotLayoutBuilder hollowSquare(SpigotIcon icon, int radius, int center) {
			int cc = center % 9;
			for(int row = center / 9 - (radius - 1); row <= center / 9 + (radius - 1); row++)
				for(int i = -radius + 1; i <= radius - 1; i++) {
					if(row == center / 9 && i == 0) {
						continue;
					}
					slot(icon, (cc + i) + row * 9);
				}

			return this;
		}

		public SpigotLayout build() {
			return new SpigotLayout(ImmutableMap.copyOf(elements), dimension);
		}
	}
}
