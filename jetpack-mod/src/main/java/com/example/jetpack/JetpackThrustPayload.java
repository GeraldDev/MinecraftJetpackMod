package com.example.jetpack;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Sent client -> server every tick the player is thrusting. */
public record JetpackThrustPayload() implements CustomPacketPayload {
    public static final Type<JetpackThrustPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JetpackMod.MODID, "thrust"));
    public static final StreamCodec<ByteBuf, JetpackThrustPayload> CODEC =
            StreamCodec.unit(new JetpackThrustPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
