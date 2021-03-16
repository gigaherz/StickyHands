package gigaherz.stickyhands.mixin;

import gigaherz.stickyhands.StickyHands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerController.class)
public class PlayerControllerMixin
{
    @Inject(method="interactWithEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/EntityRayTraceResult;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResultType;",
            at=@At("RETURN"))
    public void interactWithEntity(PlayerEntity player, Entity target, EntityRayTraceResult ray, Hand hand, CallbackInfoReturnable<ActionResultType> info)
    {
        StickyHands.Events.afterRightClickEntity(hand, info.getReturnValue());
    }

    @Inject(method="func_217292_a(Lnet/minecraft/client/entity/player/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Lnet/minecraft/util/ActionResultType;",
            at=@At("RETURN"))
    public void interactWithBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockRayTraceResult p_217292_4_, CallbackInfoReturnable<ActionResultType> info)
    {
        StickyHands.Events.afterRightClickBlock(hand, info.getReturnValue());
    }

    @Inject(method="processRightClick(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResultType;",
            at=@At("RETURN"))
    public void interactWithItem(PlayerEntity player, World worldIn, Hand hand, CallbackInfoReturnable<ActionResultType> info)
    {
        StickyHands.Events.afterRightClickItem(hand, info.getReturnValue());
    }
}
