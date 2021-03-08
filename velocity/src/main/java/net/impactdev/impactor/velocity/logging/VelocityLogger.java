package net.impactdev.impactor.velocity.logging;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.velocity.VelocityImpactorBootstrap;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class VelocityLogger implements net.impactdev.impactor.api.logging.Logger {

    private final ImpactorPlugin plugin;

    public VelocityLogger(ImpactorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void noTag(String message) {
        VelocityImpactorBootstrap.getInstance().getProxy().getConsoleCommandSource().sendMessage(
                Identity.nil(),
                LegacyComponentSerializer.legacyAmpersand().deserialize(message),
                MessageType.SYSTEM
        );
    }

    @Override
    public void noTag(List<String> message) {
        message.forEach(this::noTag);
    }

    @Override
    public void info(String message) {
        String actual = plugin.getMetadata().getName() + " &7\u00bb " + message;

        VelocityImpactorBootstrap.getInstance().getProxy().getConsoleCommandSource().sendMessage(
                Identity.nil(),
                LegacyComponentSerializer.legacyAmpersand().deserialize(actual),
                MessageType.SYSTEM
        );
    }

    @Override
    public void info(List<String> message) {
        message.forEach(this::info);
    }


    @Override
    public void warn(String message) {
        String actual = plugin.getMetadata().getName() + " &7(&6Warning&7) " + message;

        VelocityImpactorBootstrap.getInstance().getProxy().getConsoleCommandSource().sendMessage(
                Identity.nil(),
                LegacyComponentSerializer.legacyAmpersand().deserialize(actual),
                MessageType.SYSTEM
        );
    }

    @Override
    public void warn(List<String> message) {
        message.forEach(this::warn);
    }

    @Override
    public void error(String message) {
        String actual = plugin.getMetadata().getName() + " &7(&cError&7) " + message;

        VelocityImpactorBootstrap.getInstance().getProxy().getConsoleCommandSource().sendMessage(
                Identity.nil(),
                LegacyComponentSerializer.legacyAmpersand().deserialize(actual),
                MessageType.SYSTEM
        );
    }

    @Override
    public void error(List<String> message) {
        message.forEach(this::error);
    }

    @Override
    public void debug(String message) {
        String actual = plugin.getMetadata().getName() + " &7(&bDebug&7) " + message;

        VelocityImpactorBootstrap.getInstance().getProxy().getConsoleCommandSource().sendMessage(
                Identity.nil(),
                LegacyComponentSerializer.legacyAmpersand().deserialize(actual),
                MessageType.SYSTEM
        );
    }

    public void debug(List<String> message) {
        if(this.plugin.inDebugMode()) {
            message.forEach(this::debug);
        }
    }

}
