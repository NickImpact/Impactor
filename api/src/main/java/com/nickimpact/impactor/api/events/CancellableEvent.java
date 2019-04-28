package com.nickimpact.impactor.api.events;

public interface CancellableEvent {

	boolean isCancelled();

	void setCancelled(boolean flag);

}
