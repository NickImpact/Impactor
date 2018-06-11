package com.nickimpact.impactor.api.events;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class CancellableEvent extends AbstractEvent implements Cancellable {

	private boolean cancelled;
	private Cause cause;

	public CancellableEvent() {
		this.cause = Sponge.getCauseStackManager().getCurrentCause();
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public Cause getCause() {
		return this.cause;
	}
}
