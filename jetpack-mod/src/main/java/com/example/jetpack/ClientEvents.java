package com.example.jetpack;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = JetpackMod.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.isPaused()) return;
        if (player.getAbilities().flying || player.isInWater() || player.isInLava()) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof JetpackItem) || JetpackItem.isEmpty(chest)) return;
        if (!mc.options.keyJump.isDown()) return;

        Vec3 motion = player.getDeltaMovement();
        double newY = Math.min(motion.y + JetpackItem.THRUST, JetpackItem.MAX_RISE_SPEED);
        player.setDeltaMovement(motion.x, newY, motion.z);
        player.fallDistance = 0;

        PacketDistributor.sendToServer(new JetpackThrustPayload());
    }
}
