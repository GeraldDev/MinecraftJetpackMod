package com.example.jetpack;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(JetpackMod.MODID)
public class JetpackMod {
    public static final String MODID = "jetpack";

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    // Reuses the vanilla iron armor model texture so no custom armor texture is needed.
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> JETPACK_MATERIAL =
            ARMOR_MATERIALS.register("jetpack", () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 2);
                        map.put(ArmorItem.Type.LEGGINGS, 5);
                        map.put(ArmorItem.Type.CHESTPLATE, 6);
                        map.put(ArmorItem.Type.HELMET, 2);
                        map.put(ArmorItem.Type.BODY, 5);
                    }),
                    15,
                    SoundEvents.ARMOR_EQUIP_IRON,
                    () -> Ingredient.of(Items.NETHERITE_INGOT),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("iron"))),
                    1.0F,
                    0.0F));

    public static final DeferredItem<JetpackItem> JETPACK = ITEMS.register("jetpack",
            () -> new JetpackItem(JETPACK_MATERIAL,
                    new Item.Properties().durability(JetpackItem.MAX_DURABILITY).fireResistant()));

    public JetpackMod(IEventBus modBus) {
        ARMOR_MATERIALS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::registerPayloads);
        modBus.addListener(this::addToCreativeTab);
    }

    private void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(JETPACK.get());
        }
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(
                JetpackThrustPayload.TYPE,
                JetpackThrustPayload.CODEC,
                JetpackMod::handleThrust);
    }

    /** Server side: validates the jetpack, uses fuel (durability), spawns particles, prevents fall damage/kick. */
    private static void handleThrust(JetpackThrustPayload payload, IPayloadContext ctx) {
        if (!(ctx.player() instanceof ServerPlayer player)) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof JetpackItem) || JetpackItem.isEmpty(chest)) return;

        player.fallDistance = 0;
        player.connection.resetFlyingTicks(); // stops "Flying is not enabled on this server" kicks

        if (player.tickCount % JetpackItem.TICKS_PER_DURABILITY == 0) {
            chest.hurtAndBreak(1, player, EquipmentSlot.CHEST);
        }

        if (player.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 0.5, player.getZ(),
                    3, 0.15, 0.05, 0.15, 0.02);
            level.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 0.4, player.getZ(),
                    1, 0.15, 0.05, 0.15, 0.01);
        }
    }
}
