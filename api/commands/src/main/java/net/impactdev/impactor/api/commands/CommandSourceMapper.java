package net.impactdev.impactor.api.commands;

import net.minecraft.commands.CommandSourceStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;

public final class CommandSourceMapper implements SenderMapper<CommandSource, CommandSourceStack> {

    @Override
    public @NonNull CommandSourceStack map(@NonNull CommandSource base) {
        return base.delegate();
    }

    @Override
    public @NonNull CommandSource reverse(@NonNull CommandSourceStack mapped) {
        return null;
    }
}
