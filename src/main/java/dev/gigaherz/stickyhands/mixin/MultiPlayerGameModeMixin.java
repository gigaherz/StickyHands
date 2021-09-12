package dev.gigaherz.stickyhands.mixin;

import dev.gigaherz.stickyhands.StickyHands;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin
{
    @Inject(method="interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at=@At("RETURN"))
    public void interactWithEntityHook(Player player, Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info)
    {
        StickyHands.Events.afterRightClickEntity(hand, info.getReturnValue());
    }

    @Inject(method="interactAt(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/EntityHitResult;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at=@At("RETURN"))
    public void interactWithEntityHook(Player player, Entity entity, EntityHitResult hitResult, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info)
    {
        StickyHands.Events.afterRightClickEntity(hand, info.getReturnValue());
    }

    @Inject(method="useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            at=@At("RETURN"))
    public void interactWithBlockHook(LocalPlayer player, ClientLevel level, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> info)
    {
        StickyHands.Events.afterRightClickBlock(hand, info.getReturnValue());
    }

    @Inject(method="useItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at=@At("RETURN"))
    public void interactWithItemHook(Player player, Level level, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info)
    {
        StickyHands.Events.afterRightClickItem(hand, info.getReturnValue());
    }
}
