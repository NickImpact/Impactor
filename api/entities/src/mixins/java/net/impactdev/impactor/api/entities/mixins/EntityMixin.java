package net.impactdev.impactor.api.entities.mixins;

import net.kyori.adventure.pointer.Pointers;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(Entity.class)
@Implements(@Interface(iface = net.impactdev.impactor.api.entities.Entity.class, prefix = "impactor$"))
public abstract class EntityMixin implements Nameable {

    @Shadow public abstract UUID shadow$getUUID();

    public UUID impactor$uuid() {
        return this.shadow$getUUID();
    }

    @NotNull
    public Pointers impactor$pointers() {
        return Pointers.empty();
    }
}
