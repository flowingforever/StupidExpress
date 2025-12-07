package pro.fazeclan.river.stupid_express.mixin.arsonist;

import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pro.fazeclan.river.stupid_express.arsonist.cca.DousedPlayerComponent;

@Mixin(GameFunctions.class)
public class ArsonistRemoveDousedMixin {

    @Inject(method = "initializeGame", at = @At("HEAD"))
    private static void initializeGame(ServerLevel serverWorld, CallbackInfo ci) {
        var dousedPlayers = serverWorld.getPlayers(player -> DousedPlayerComponent.KEY.get(player).isDoused());

        for (ServerPlayer doused : dousedPlayers) {
            var component = DousedPlayerComponent.KEY.get(doused);
            component.reset();
            component.sync();
        }
    }

}
