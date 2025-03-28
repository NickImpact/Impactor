package net.impactdev.impactor.api.economy.events;

import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;

/**
 * Details the event of a transaction occurring on the economy service.
 */
public interface EconomyTransactionEvent {

    EconomyTransaction transaction();

}
