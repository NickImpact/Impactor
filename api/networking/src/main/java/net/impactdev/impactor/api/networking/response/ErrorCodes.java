package net.impactdev.impactor.api.networking.response;

import net.kyori.adventure.key.Key;

import static net.kyori.adventure.text.Component.translatable;

public final class ErrorCodes {

    /**
     * Used to detail a response which timed out while being processed.
     *
     * @since 6.0.0
     */
    public static final ErrorCode TIMEOUT = new ErrorCode(
            Key.key("impactor", "timeout"),
            translatable("impactor.networking.response.timeout.display"),
            translatable("impactor.networking.response.timeout.description")
    );

    /**
     * Used to detail a response which encountered some form of fatal exception, and was unable
     * to proceed.
     *
     * @since 6.0.0
     */
    public static final ErrorCode FATAL = new ErrorCode(
            Key.key("impactor", "fatal"),
            translatable("impactor.networking.response.fatal.display"),
            translatable("impactor.networking.response.fatal.description")
    );

}
