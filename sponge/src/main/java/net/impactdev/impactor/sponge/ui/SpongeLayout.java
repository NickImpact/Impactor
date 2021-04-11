package net.impactdev.impactor.sponge.ui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.gui.InventoryDimensions;
import net.impactdev.impactor.api.gui.Layout;
import lombok.RequiredArgsConstructor;
import net.impactdev.impactor.sponge.ui.icons.SpongeIcon;
import net.impactdev.impactor.sponge.ui.icons.SpongeIcons;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class SpongeLayout implements Layout<SpongeIcon> {

	private final ImmutableMap<Integer, SpongeIcon> elements;
	private final InventoryDimensions dimension;

	@Override
	public ImmutableMap<Integer, SpongeIcon> getElements() {
		return this.elements;
	}

	@Override
	public Optional<SpongeIcon> getIcon(int slot) {
		return Optional.ofNullable(elements.get(slot));
	}

	@Override
	public InventoryDimensions getDimensions() {
		return this.dimension;
	}

	public static SpongeLayoutBuilder builder() {
		return new SpongeLayoutBuilder();
	}

	public static class SpongeLayoutBuilder {

		private Map<Integer, SpongeIcon> elements = Maps.newHashMap();
		private InventoryDimensions dimension = new InventoryDimensions(9, 6);

		private int rows = 6;
		private int columns = 9;
		private int capacity = 54;

		public SpongeLayoutBuilder from(Layout<SpongeIcon> layout) {
			elements.clear();
			dimension(dimension.getColumns(), dimension.getRows());
			for(int slot : layout.getElements().keySet()) {
				slot(layout.getElements().get(slot), slot);
			}

			return this;
		}

		public SpongeLayoutBuilder dimension(int columns, int rows) {
			this.dimension = new InventoryDimensions(columns, rows);
			this.rows = rows;
			this.columns = columns;
			this.capacity = rows * columns;
			return this;
		}

		public SpongeLayoutBuilder slot(SpongeIcon icon, int slot) {
			elements.put(slot, icon);
			return this;
		}

		public SpongeLayoutBuilder slots(SpongeIcon icon, int... slots) {
			for(int slot : slots) {
				slot(icon, slot);
			}
			return this;
		}

		public SpongeLayoutBuilder fill(SpongeIcon icon) {
			for(int i = 0; i < capacity; i++) {
				if(!elements.containsKey(i)) {
					slot(icon, i);
				}
			}
			return this;
		}

		public SpongeLayoutBuilder border() {
			return this.border(SpongeIcons.BORDER);
		}

		public SpongeLayoutBuilder border(SpongeIcon icon) {
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

		public SpongeLayoutBuilder row(SpongeIcon icon, int row) {
			for(int i = row * 9; i < (row + 1) * 9; i++) {
				slot(icon, i);
			}

			return this;
		}

		public SpongeLayoutBuilder rows(SpongeIcon icon, int... rows) {
			for(int row : rows) {
				this.row(icon, row);
			}

			return this;
		}

		public SpongeLayoutBuilder column(SpongeIcon icon, int col) {
			for(int i = col; i < capacity; i += 9) {
				slot(icon, i);
			}

			return this;
		}

		public SpongeLayoutBuilder columns(SpongeIcon icon, int... cols) {
			for(int col : cols) {
				this.column(icon, col);
			}

			return this;
		}

		public SpongeLayoutBuilder center(SpongeIcon icon) {
			if(rows % 2 == 1) {
				slot(icon, rows * 9 / 2);
			} else {
				int base = rows * 9 / 2;
				slot(icon, base - 5);
				slot(icon, base + 4);
			}

			return this;
		}

		public SpongeLayoutBuilder square(SpongeIcon icon, int center) {
			return square(icon, 2, center);
		}

		public SpongeLayoutBuilder square(SpongeIcon icon, int radius, int center) {
			int cc = center % 9;
			for(int row = center / 9 - (radius - 1); row <= center / 9 + (radius - 1); row++)
				for(int i = -radius + 1; i <= radius - 1; i++) {
					slot(icon, (cc + i) + row * 9);
				}

			return this;
		}

		public SpongeLayoutBuilder hollowSquare(SpongeIcon icon, int center) {
			return hollowSquare(icon, 2, center);
		}

		public SpongeLayoutBuilder hollowSquare(SpongeIcon icon, int radius, int center) {
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

		public SpongeLayout build() {
			return new SpongeLayout(ImmutableMap.copyOf(elements), dimension);
		}
	}
}
