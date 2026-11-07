package com.example.jetpack;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class JetpackItem extends ArmorItem {
    /** Total fuel. */
    public static final int MAX_DURABILITY = 600;
    /** Thrust uses 1 durability every this many ticks (10 ticks = 0.5s => ~5 minutes of flight). */
    public static final int TICKS_PER_DURABILITY = 10;
    /** Upward acceleration per tick while holding jump. */
    public static final double THRUST = 0.10;
    /** Maximum upward speed (blocks per tick). */
    public static final double MAX_RISE_SPEED = 0.5;

    public JetpackItem(Holder<ArmorMaterial> material, Properties properties) {
        super(material, Type.CHESTPLATE, properties);
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }
}
