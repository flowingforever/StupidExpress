package pro.fazeclan.river.stupid_express.client.mixin.modifier.lovers;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.RoleNameRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pro.fazeclan.river.stupid_express.StupidExpress;
import pro.fazeclan.river.stupid_express.client.StupidExpressClient;
import pro.fazeclan.river.stupid_express.constants.SEModifiers;

import java.util.UUID;

@Mixin(RoleNameRenderer.class)
public abstract class LoversHudMixin {

    @Shadow
    private static float nametagAlpha;

    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void loversHud(Font renderer, LocalPlayer player, GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {

        var clientPlayer = Minecraft.getInstance().player;
        var clientWorld = clientPlayer.level();

        var component = WorldModifierComponent.KEY.get(clientPlayer.level());
        var config = StupidExpress.CONFIG;
        if (component.isModifier(clientPlayer, SEModifiers.LOVERS)
                && WatheClient.isPlayerAliveAndInSurvival()) {

            var lovers = component.getAllWithModifier(SEModifiers.LOVERS);
            lovers.remove(clientPlayer.getUUID());

            var textYPos = context.guiHeight() - 12;
            var textXPos = 18;

            for (UUID uuid : lovers) {
                context.pose().pushPose();

                var loverInfo = clientPlayer.connection.getPlayerInfo(uuid);
                if (loverInfo == null) return;

                Component name;
                if (!config.modifiersSection.loversSection.loversKnowImmediately) {
                    name = Component.translatable("hud.stupid_express.lovers.notification");
                    textXPos -= 14;
                } else {
                    name = Component.translatable("tip.stupid_express.lovers.partner", loverInfo.getProfile().getName());
                }

                var role = GameWorldComponent.KEY.get(clientWorld).getRole(clientPlayer);
                if (role != null) {
                    if (GameWorldComponent.KEY.get(clientWorld).getRole(clientPlayer).identifier().equals(ResourceLocation.parse("noellesroles:executioner"))) {
                        textYPos -= 15;
                    }
                }
                if (config.modifiersSection.loversSection.loversKnowImmediately) {
                    PlayerFaceRenderer.draw(context,loverInfo.getSkin().texture(), 2, textYPos - 2,12);
                }
                context.drawString(renderer, name, textXPos, textYPos, SEModifiers.LOVERS.color());

                context.pose().popPose();
                textXPos += 14;
            }

        }
    }

    @Inject(
            method = "renderHud",
            at = @At("TAIL")
    )
    private static void renderLovers(Font renderer, LocalPlayer player, GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        var clientPlayer = Minecraft.getInstance().player;
        var clientLevel = clientPlayer.level();
        var component = WorldModifierComponent.KEY.get(clientLevel);
        if (StupidExpressClient.target == null) {
            return;
        }
        if (!component.isModifier(StupidExpressClient.target, SEModifiers.LOVERS)) {
            return;
        }
        var config = StupidExpress.CONFIG;
        if (WatheClient.isPlayerAliveAndInSurvival()
                && !config.modifiersSection.loversSection.loversKnowImmediately
                && component.isModifier(clientPlayer, SEModifiers.LOVERS)) {
            stupidexpress$renderLoversHud(renderer, context, Component.translatable("hud.stupid_express.lovers.partner"));
        } else if (WatheClient.isPlayerSpectatingOrCreative()) {
            var lovers = component.getAllWithModifier(SEModifiers.LOVERS);
            lovers.remove(StupidExpressClient.target.getUUID());
            for (UUID uuid : lovers) {
                var lover = clientLevel.getPlayerByUUID(uuid);
                if (lover == null) continue;
                stupidexpress$renderLoversHud(renderer, context, Component.translatable(
                        "hud.stupid_express.lovers.in_love",
                        lover.getName()
                ));
            }
        }
    }

    @Unique
    private static void stupidexpress$renderLoversHud(Font renderer, GuiGraphics context, Component component) {

        context.pose().pushPose();
        context.pose().translate(context.guiWidth() / 2.0f, context.guiHeight() / 2.0f - 35.0f, 0.0f);
        context.pose().scale(0.6f, 0.6f, 1.0f);

        context.drawString(
                renderer,
                component,
                -renderer.width(component) / 2,
                32,
                SEModifiers.LOVERS.color() | (int) (nametagAlpha * 255.0F) << 24
        );

        context.pose().popPose();
    }

}
