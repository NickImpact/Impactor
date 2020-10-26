package net.impactdev.impactor.sponge.mixins;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.sponge.text.placeholders.provided.tick.MeanTickTime;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixer;
import org.spongepowered.api.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.net.Proxy;

@Mixin(DedicatedServer.class)
public abstract class ImpactorTickTimeWatcher extends MinecraftServer implements MeanTickTime {

    private MinecraftServer instance;

    public ImpactorTickTimeWatcher(File anvilFileIn, Proxy proxyIn, DataFixer dataFixerIn, YggdrasilAuthenticationService authServiceIn, MinecraftSessionService sessionServiceIn, GameProfileRepository profileRepoIn, PlayerProfileCache profileCacheIn) {
        super(anvilFileIn, proxyIn, dataFixerIn, authServiceIn, sessionServiceIn, profileRepoIn, profileCacheIn);
    }

    @Inject(method = "init", at = @At(value = "INVOKE_ASSIGN", target = "java/lang/System.nanoTime()J", shift = At.Shift.BEFORE, ordinal = 0))
    public void registerImpactorInstanceWatcher(CallbackInfoReturnable<Boolean> cir) {
        instance = this;
        Impactor.getInstance().getRegistry().register(MeanTickTime.class, this);
    }

    @Override
    public double get() {
        return mean(this.instance.tickTimeArray) * 1.0E-6D;
    }

    @Override
    public Text getFormatted() {
        return Text.of(MeanTickTime.FORMATTER.format(this.get()));
    }

    private static long mean(long[] input) {
        long sum = 0L;
        for (long v : input)
        {
            sum += v;
        }
        return sum / input.length;
    }
}
