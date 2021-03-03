package net.impactdev.impactor.sponge.listeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ichorpowered.protocolcontrol.event.PacketEvent;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.gui.signs.SignQuery;
import net.kyori.event.method.annotation.Subscribe;
import net.minecraft.network.play.client.CPacketUpdateSign;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.UUID;

public class SignListener {

    public final static Map<UUID, SignQuery<Text, Player>> requests = Maps.newHashMap();

    @Subscribe
    public void onSignUpdate(PacketEvent<CPacketUpdateSign> event) {
        if(requests.containsKey(event.profile().player())) {
            SignQuery<Text, Player> query = requests.remove(event.profile().player());
            Impactor.getInstance().getScheduler().executeSync(() -> {
                if(!query.getSubmissionHandler().process(Lists.newArrayList(event.packet().getLines()))) {
                    if(query.shouldReopenOnFailure()) {
                        requests.put(event.profile().player(), query);
                        query.sendTo(Sponge.getServer().getPlayer(event.profile().player()).get());
                    }
                }
            });
        }
    }

}
