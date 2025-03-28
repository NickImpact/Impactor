package net.impactdev.impactotr.api.commands.mixins;

import net.impactdev.impactor.api.platform.entity.Subject;
import net.minecraft.commands.CommandSource;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandSource.class)
@Implements(@Interface(iface = Subject.class, prefix = "impactor$"))
public abstract class CommandSourceMixin {
}
