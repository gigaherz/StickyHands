package dev.gigaherz.stickyhands;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod(StickyHands.MODID)
public class StickyHands
{
    public static final String MODID = "stickyhands";

    public StickyHands() {
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (a, b) -> true));
    }

    @Mod.EventBusSubscriber(value= Dist.CLIENT, modid=MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class Events {
        @Nullable
        private static InteractionHand lastHand;

        @SubscribeEvent
        public static void tickEvent(TickEvent.ClientTickEvent event)
        {
            if (lastHand != null && !Minecraft.getInstance().options.keyUse.isDown())
            {
                lastHand = null;
            }
        }

        @SubscribeEvent
        public static void clickInputEvent(InputEvent.ClickInputEvent event)
        {
            if (lastHand != null && event.getHand() != lastHand)
            {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }

        // Food returns PASS from onItemUse ¬_¬
        @SubscribeEvent
        public static void useStart(LivingEntityUseItemEvent.Start event)
        {
            if (!event.getEntityLiving().level.isClientSide)
                return;

            if (event.getEntityLiving() instanceof Player)
            {
                Player player = (Player)event.getEntityLiving();

                InteractionHand hand = player.getUsedItemHand();
                if (hand != null) // Yes it's null sometimes... but only sometimes...
                    lastHand = hand;
            }
        }

        public static void afterRightClickEntity(InteractionHand hand, InteractionResult actionresulttype)
        {
            if (actionresulttype.consumesAction())
                lastHand = hand;
        }

        public static void afterRightClickBlock(InteractionHand hand, InteractionResult actionresulttype)
        {
            if (actionresulttype.consumesAction())
                lastHand = hand;
        }

        public static void afterRightClickItem(InteractionHand hand, InteractionResult actionresulttype)
        {
            if (actionresulttype.consumesAction())
                lastHand = hand;
        }
    }
}
