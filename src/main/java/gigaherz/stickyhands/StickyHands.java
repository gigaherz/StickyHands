package gigaherz.stickyhands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        private static Hand lastHand;

        @SubscribeEvent
        public static void mouseEvent(TickEvent.ClientTickEvent event)
        {
            if (lastHand != null && ! Minecraft.getInstance().gameSettings.keyBindUseItem.isKeyDown())
            {
                lastHand = null;
            }
        }

        @SubscribeEvent
        public static void playerInteract(PlayerInteractEvent.RightClickBlock event)
        {
            testAndCancel(event);
        }

        @SubscribeEvent
        public static void playerInteract(PlayerInteractEvent.RightClickItem event)
        {
            testAndCancel(event);
        }

        private static void testAndCancel(PlayerInteractEvent event)
        {
            if (lastHand != null && lastHand != event.getHand())
            {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.PASS);
            }
        }

        public static void afterRightClickBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockRayTraceResult blockraytraceresult, ActionResultType actionresulttype)
        {
            if (actionresulttype == ActionResultType.SUCCESS)
                lastHand = hand;
        }
    }
}
