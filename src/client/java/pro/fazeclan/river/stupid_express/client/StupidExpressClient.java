package pro.fazeclan.river.stupid_express.client;

import dev.doctor4t.trainmurdermystery.entity.PlayerBodyEntity;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.world.entity.player.Player;

public class StupidExpressClient implements ClientModInitializer {

    public static Player target;
    public static PlayerBodyEntity targetBody;

    @Override
    public void onInitializeClient() {
    }
}
