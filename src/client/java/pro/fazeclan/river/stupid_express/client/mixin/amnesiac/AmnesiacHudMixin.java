package pro.fazeclan.river.stupid_express.client.mixin.amnesiac;

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import dev.doctor4t.trainmurdermystery.client.gui.RoleNameRenderer;
import dev.doctor4t.trainmurdermystery.entity.PlayerBodyEntity;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pro.fazeclan.river.stupid_express.StupidExpress;
import pro.fazeclan.river.stupid_express.client.StupidExpressClient;

@Mixin(RoleNameRenderer.class)
public class AmnesiacHudMixin {

    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void stupidexpress$replaceRoleHud(Font renderer, LocalPlayer player, GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.level());
        if (StupidExpressClient.targetBody == null) {
            return;
        }
        if (gameWorldComponent.isRole(Minecraft.getInstance().player, StupidExpress.AMNESIAC) && !TMMClient.isPlayerSpectatingOrCreative()) {
            context.pose().pushPose();
            context.pose().translate(context.guiWidth() / 2.0f, context.guiHeight() / 2.0f + 6.0f, 0.0f);
            context.pose().scale(0.6f, 0.6f, 1.0f);

            Component status = Component.translatable("hud.amnesiac.select_body");
            context.drawString(renderer, status, -renderer.width(status) / 2, 32, 0x9baae8);

            context.pose().popPose();
        }
    }

    @Inject(method = "renderHud", at = @At(value = "INVOKE", target = "Ldev/doctor4t/trainmurdermystery/game/GameFunctions;isPlayerSpectatingOrCreative(Lnet/minecraft/world/entity/player/Player;)Z"))
    private static void stupidexpress$playerBodyRaycast(Font renderer, LocalPlayer player, GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        float range = GameFunctions.isPlayerSpectatingOrCreative(player) ? 8.0F : 2.0F;
        HitResult line = ProjectileUtil.getHitResultOnViewVector(player, entity -> entity instanceof PlayerBodyEntity, range);
        StupidExpressClient.targetBody = null;
        if (!(line instanceof EntityHitResult ehr)) {
            return;
        }
        if (!(ehr.getEntity() instanceof PlayerBodyEntity victim)) {
            return;
        }
        StupidExpressClient.targetBody = victim;
    }

}
