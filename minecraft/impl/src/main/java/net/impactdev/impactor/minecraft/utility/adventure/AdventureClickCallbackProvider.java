package net.impactdev.impactor.minecraft.utility.adventure;

import com.google.auto.service.AutoService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
@AutoService(ClickCallback.Provider.class)
public final class AdventureClickCallbackProvider implements ClickCallback.Provider {

    @Override
    public @NotNull ClickEvent create(@NotNull ClickCallback<Audience> callback, ClickCallback.@NotNull Options options) {
        return ClickEvent.runCommand(ClickCallbackRegistry.INSTANCE.register(callback, options));
    }

}
