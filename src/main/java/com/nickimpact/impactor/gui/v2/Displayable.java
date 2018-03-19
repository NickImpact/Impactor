package com.nickimpact.impactor.gui.v2;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public interface Displayable {

	UI getDisplay();

	default void open() {
		this.getDisplay().open();
	}

	default void close() {
		this.getDisplay().close();
	}
}
