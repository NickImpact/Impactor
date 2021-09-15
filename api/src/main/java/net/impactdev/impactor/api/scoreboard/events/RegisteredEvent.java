package net.impactdev.impactor.api.scoreboard.events;

public final class RegisteredEvent {

    private final Object registration;

    public RegisteredEvent(Object registration) {
        this.registration = registration;
    }

    public Object getRegistration() {
        return this.registration;
    }

}
