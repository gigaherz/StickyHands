package gigaherz.stickyhands;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

@Mod(StickyHands.MODID)
public class StickyHands
{
    public static final String MODID = "stickyhands";

    public StickyHands() {
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    @Mod.EventBusSubscriber(value= Dist.CLIENT, modid=MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class Events {
        @Nullable
        private static Hand lastHand;

        @SubscribeEvent
        public static void tickEvent(TickEvent.ClientTickEvent event)
        {
            if (lastHand != null && !Minecraft.getInstance().gameSettings.keyBindUseItem.isKeyDown())
            {
                lastHand = null;
            }
        }

        // Needed because food returns PASS from onItemUse ¬_¬
        @SubscribeEvent
        public static void useStart(LivingEntityUseItemEvent.Start event)
        {
            if (!event.getEntityLiving().world.isRemote)
                return;

            if (event.getEntityLiving() instanceof PlayerEntity)
            {
                PlayerEntity player = (PlayerEntity)event.getEntityLiving();

                Hand hand = player.getActiveHand();
                if (hand != null) // Yes it's null sometimes... but only sometimes...
                    lastHand = hand;
            }
        }

        public static boolean beforeRightClickBlock(Hand hand)
        {
            return lastHand == null || lastHand == hand;
        }

        public static void afterRightClickBlock(Hand hand, ActionResultType actionresulttype)
        {
            if (actionresulttype == ActionResultType.SUCCESS)
                lastHand = hand;
        }
    }
}
