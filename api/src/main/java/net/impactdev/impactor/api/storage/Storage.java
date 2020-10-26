package net.impactdev.impactor.api.storage;

public interface Storage {

	/**
	 * Initializes the Storage Provider
	 *
	 * @throws Exception In the event any error manages to occur during initialization
	 */
	void init() throws Exception;

	/**
	 * Closes the Storage provider. This is where we should perform our final operations before
	 * we kill the system.
	 *
	 * @throws Exception In the event any error manages to occur during shutdown
	 */
	void shutdown() throws Exception;
}
