package net.impactdev.impactotr.api.commands.mixins;

import net.impactdev.impactor.api.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements CommandSource.Stack {

    @Unique
    private CommandSource impactor$source;

    @Override
    public CommandSource source() {
        return this.impactor$source;
    }
}
