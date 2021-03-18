package net.impactdev.impactor.sponge.text.placeholders;

import org.spongepowered.api.placeholder.PlaceholderParser;

public class PlaceholderMetadata {

    private final String token;
    private final PlaceholderParser parser;

    public PlaceholderMetadata(String token, PlaceholderParser parser) {
        this.token = token;
        this.parser = parser;
    }

    public String getToken() {
        return token;
    }

    public PlaceholderParser getParser() {
        return parser;
    }
}
