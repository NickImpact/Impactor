package com.nickimpact.impactor.api.event;

import com.google.gson.reflect.TypeToken;
import com.nickimpact.impactor.api.event.annotations.Param;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A super-interface for all Impactor events.
 */
public interface ImpactorEvent {

    /**
     * Gets the type of the event.
     *
     * @return the type of the event
     */
    @NonNull Class<? extends ImpactorEvent> getEventType();

    interface Generic<T> extends ImpactorEvent {

        @Param(-2)
        @NonNull TypeToken<T> type();

    }

}
