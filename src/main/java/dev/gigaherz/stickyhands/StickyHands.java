package dev.gigaherz.stickyhands;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

import javax.annotation.Nullable;

@Mod(StickyHands.MODID)
public class StickyHands
{
    public static final String MODID = "stickyhands";

    public StickyHands() {
    }

    @Mod.EventBusSubscriber(value= Dist.CLIENT, modid=MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class Events {
        @Nullable
        private static InteractionHand lastHand;

        @SubscribeEvent
        public static void tickEvent(TickEvent.ClientTickEvent event)
        {
            if(event.phase == TickEvent.Phase.START)
            {
                if (lastHand != null && !Minecraft.getInstance().options.keyUse.isDown())
                {
                    lastHand = null;
                }
            }
        }

        @SubscribeEvent
        public static void clickInputEvent(InputEvent.InteractionKeyMappingTriggered event)
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
            if (!event.getEntity().level().isClientSide)
                return;

            if (event.getEntity() instanceof Player)
            {
                Player player = (Player)event.getEntity();

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
