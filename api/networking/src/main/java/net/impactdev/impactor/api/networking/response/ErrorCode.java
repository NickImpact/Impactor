package net.impactdev.impactor.api.networking.response;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.TranslatableComponent;

/**
 * @param key
 * @param display
 * @param description
 */
public record ErrorCode(Key key, TranslatableComponent display, TranslatableComponent description) { }
