package pro.fazeclan.river.stupid_express.arsonist;

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import pro.fazeclan.river.stupid_express.ModItems;
import pro.fazeclan.river.stupid_express.StupidExpress;
import pro.fazeclan.river.stupid_express.arsonist.cca.DousedPlayerComponent;

public class OilDousingHandler {

    public static void init() {
        UseEntityCallback.EVENT.register(((player, level, interactionHand, entity, entityHitResult) -> {
            if (!(player instanceof ServerPlayer interacting)) {
                return InteractionResult.PASS;
            }
            if (!interacting.gameMode.isSurvival()) {
                return InteractionResult.PASS;
            }
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.level());
            if (!gameWorldComponent.isRole(player, StupidExpress.ARSONIST)) {
                return InteractionResult.PASS;
            }
            if (!(entity instanceof ServerPlayer victim)) {
                return InteractionResult.PASS;
            }

            var item = player.getItemInHand(interactionHand);
            if (!item.is(ModItems.JERRY_CAN)) {
                return InteractionResult.PASS;
            }
            if (interacting.getCooldowns().isOnCooldown(item.getItem())) {
                return InteractionResult.PASS;
            }
            if (interacting.gameMode.isSurvival()) {
                var playerCount = ((ServerLevel) level).getPlayers(GameFunctions::isPlayerAliveAndSurvival).size();
                var cd = 45 - (5/3.0) * (double) playerCount;

                if (playerCount > 15) {
                    cd = 20;
                }

                interacting.getCooldowns().addCooldown(item.getItem(), (int) (cd * 20));
            }
            DousedPlayerComponent doused = DousedPlayerComponent.KEY.get(victim);
            doused.setDoused(true);
            doused.sync();

            //interacting.playSound(SoundEvents.BREWING_STAND_BREW);

            return InteractionResult.CONSUME;
        }));
    }

}
